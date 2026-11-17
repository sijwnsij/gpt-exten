#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")"

: "${ANDROID_HOME:?Set ANDROID_HOME to Android SDK root}"
ABI="${BUILD_ABI:-arm64-v8a}"
case "$ABI" in
  arm64-v8a) TRIPLE="aarch64-linux-android" ;;
  x86_64) TRIPLE="x86_64-linux-android" ;;
  *) echo "Unsupported ABI: $ABI" >&2; exit 1 ;;
esac
PLATFORM_VERSION="${ANDROID_PLATFORM_VERSION:-35}"
BUILD_TOOLS_VERSION="${ANDROID_BUILD_TOOLS_VERSION:-35.0.0}"
NDK_VERSION="${ANDROID_NDK_VERSION:-27.2.12479018}"
ANDROID_JAR="$ANDROID_HOME/platforms/android-$PLATFORM_VERSION/android.jar"
BT="$ANDROID_HOME/build-tools/$BUILD_TOOLS_VERSION"
NDK="${ANDROID_NDK_HOME:-$ANDROID_HOME/ndk/$NDK_VERSION}"
TOOLBIN="$NDK/toolchains/llvm/prebuilt/$(uname -s | tr '[:upper:]' '[:lower:]')-x86_64/bin"
# macOS ARM64 NDK host binaries are darwin-x86_64 (via Rosetta); CI builds on Linux.
if [[ "$(uname -s)" == "Darwin" ]]; then TOOLBIN="$NDK/toolchains/llvm/prebuilt/darwin-x86_64/bin"; fi

for bin in "$BT/aapt2" "$BT/d8" "$BT/zipalign" "$BT/apksigner" "$TOOLBIN/clang"; do
  test -x "$bin" || { echo "Missing tool: $bin" >&2; exit 1; }
done
test -f "$ANDROID_JAR" || { echo "Missing android.jar: $ANDROID_JAR" >&2; exit 1; }

OBJ="build/$ABI"
mkdir -p "$OBJ/classes" "$OBJ/compiled" "$OBJ/dex" "$OBJ/staging/lib/$ABI"
"$TOOLBIN/clang" --target="${TRIPLE}31" -shared -fPIC -O2 -Wall -Wextra \
  -Wl,-z,relro,-z,now -o "$OBJ/staging/lib/$ABI/libarm64loop.so" app/jni/native_loop.c -llog
readelf -h "$OBJ/staging/lib/$ABI/libarm64loop.so" | grep -E 'Class:|Machine:|Type:'

find app/src -name '*.java' -print0 | xargs -0 javac -encoding UTF-8 -source 8 -target 8 \
  -cp "$ANDROID_JAR" -d "$OBJ/classes"
mapfile -d '' JAVA_CLASSES < <(find "$OBJ/classes" -name '*.class' -print0)
"$BT/d8" --release --min-api 31 --output "$OBJ/dex" "${JAVA_CLASSES[@]}"
"$BT/aapt2" compile --dir app/res -o "$OBJ/compiled"
mapfile -d '' FLATS < <(find "$OBJ/compiled" -name '*.flat' -print0)
"$BT/aapt2" link -o "$OBJ/base.apk" -I "$ANDROID_JAR" --manifest app/AndroidManifest.xml "${FLATS[@]}"
cp "$OBJ/dex/classes.dex" "$OBJ/staging/classes.dex"
cp "$OBJ/base.apk" "$OBJ/unsigned.apk"
(cd "$OBJ/staging" && zip -q -0 "../unsigned.apk" classes.dex "lib/$ABI/libarm64loop.so")
"$BT/zipalign" -f -p 4 "$OBJ/unsigned.apk" "$OBJ/aligned.apk"
KEYSTORE="$OBJ/debug.keystore"
if [[ ! -f "$KEYSTORE" ]]; then
  keytool -genkeypair -v -keystore "$KEYSTORE" -storepass android -keypass android \
    -keyalg RSA -keysize 2048 -validity 3650 -alias androiddebugkey \
    -dname 'CN=ARM64 Overlay Test, O=Local Build, C=US' >/dev/null 2>&1
fi
OUT="${OUTPUT_APK:-$PWD/build/Arm64OverlayDemo-$ABI.apk}"
mkdir -p "$(dirname "$OUT")"
"$BT/apksigner" sign --ks "$KEYSTORE" --ks-key-alias androiddebugkey \
  --ks-pass pass:android --key-pass pass:android --out "$OUT" "$OBJ/aligned.apk"
"$BT/apksigner" verify --verbose --print-certs "$OUT"
unzip -l "$OUT" | grep -E 'classes.dex|lib/.+/libarm64loop.so'
sha256sum "$OUT"
echo "RESULT=$OUT"
