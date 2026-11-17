#!/usr/bin/env bash
set -euo pipefail
mkdir -p ci-results
PACKAGE="com.example.arm64loop"
APK="${APK_TO_TEST:-dist/Arm64OverlayDemo-arm64-v8a.apk}"
{
  echo "Android SDK: $(adb shell getprop ro.build.version.sdk | tr -d '\r')"
  echo "ABI list: $(adb shell getprop ro.product.cpu.abilist | tr -d '\r')"
  echo "Kernel: $(adb shell uname -m | tr -d '\r')"
} | tee ci-results/environment.txt

if [[ "$(adb shell getprop ro.build.version.sdk | tr -d '\r')" != "31" ]]; then
  echo "Wrong Android version: expected API 31" >&2
  exit 1
fi
adb install -r "$APK"
adb shell appops set "$PACKAGE" SYSTEM_ALERT_WINDOW allow
adb logcat -c
adb shell am start -W -n "$PACKAGE/.MainActivity"
sleep 2

# Find the real button on-screen instead of assuming a fixed density or coordinate.
for attempt in 1 2 3 4 5; do
  if adb shell uiautomator dump /sdcard/arm64-ui.xml >/dev/null 2>&1 && \
      adb pull /sdcard/arm64-ui.xml ci-results/ui.xml >/dev/null 2>&1 && \
      grep -q '授权并启动悬浮窗' ci-results/ui.xml; then
    break
  fi
  sleep 3
done
python3 - <<'PY'
import re
import subprocess
import xml.etree.ElementTree as ET
root = ET.parse('ci-results/ui.xml').getroot()
for node in root.iter('node'):
    if node.attrib.get('text') == '授权并启动悬浮窗':
        coords = list(map(int, re.findall(r'\d+', node.attrib['bounds'])))
        x, y = (coords[0] + coords[2]) // 2, (coords[1] + coords[3]) // 2
        print(f'Tapping start button at ({x}, {y})')
        subprocess.run(['adb', 'shell', 'input', 'tap', str(x), str(y)], check=True)
        break
else:
    raise RuntimeError('Start button missing in UI hierarchy')
PY
sleep 8
adb logcat -d -s Arm64LoopNative:I '*:S' | tee ci-results/native-logcat.txt
adb shell dumpsys activity services "$PACKAGE" > ci-results/services.txt
adb shell dumpsys window windows > ci-results/windows.txt
adb exec-out screencap -p > ci-results/overlay.png || true
if ! grep -q 'JNI\[arm64-v8a\]' ci-results/native-logcat.txt; then
  echo 'No ARM64 JNI log output observed' >&2
  exit 1
fi
COUNT="$(grep -c 'JNI\[arm64-v8a\]' ci-results/native-logcat.txt || true)"
echo "ARM64 JNI ticker entries: $COUNT"
if (( COUNT < 3 )); then
  echo 'The native loop did not run for at least three ticks' >&2
  exit 1
fi
if ! grep -q 'LoopOverlayService' ci-results/services.txt; then
  echo 'Foreground overlay service not found' >&2
  exit 1
fi
echo 'PASS: Android 12 installation, start, overlay service, and ARM64 JNI tick loop'
