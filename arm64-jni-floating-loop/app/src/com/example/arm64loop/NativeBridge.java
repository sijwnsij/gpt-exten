package com.example.arm64loop;

/** Java -> JNI -> C. The release APK packages ONLY lib/arm64-v8a/libarm64loop.so. */
public final class NativeBridge {
    static {
        System.loadLibrary("arm64loop");
    }

    private NativeBridge() {
    }

    public static native String abi();
    public static native String tick();
}
