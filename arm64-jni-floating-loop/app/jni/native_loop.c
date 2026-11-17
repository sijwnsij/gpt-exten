#include <jni.h>
#include <android/log.h>
#include <inttypes.h>
#include <stdint.h>
#include <stdio.h>
#include <time.h>

#if defined(__aarch64__)
#define NATIVE_ABI "arm64-v8a"
#elif defined(__x86_64__)
#define NATIVE_ABI "x86_64"
#else
#define NATIVE_ABI "unsupported"
#endif

static uint64_t g_tick_count = 0;

JNIEXPORT jstring JNICALL
Java_com_example_arm64loop_NativeBridge_abi(JNIEnv *env, jclass type) {
    (void)type;
    return (*env)->NewStringUTF(env, NATIVE_ABI);
}

JNIEXPORT jstring JNICALL
Java_com_example_arm64loop_NativeBridge_tick(JNIEnv *env, jclass type) {
    (void)type;
    const uint64_t count = __atomic_add_fetch(&g_tick_count, 1, __ATOMIC_SEQ_CST);
    struct timespec ts = {0};
    clock_gettime(CLOCK_MONOTONIC, &ts);
    char buffer[192];
    snprintf(buffer, sizeof(buffer), "JNI[%s] #%-5" PRIu64 " uptime=%" PRIu64 "s",
             NATIVE_ABI, count, (uint64_t)ts.tv_sec);
    __android_log_print(ANDROID_LOG_INFO, "Arm64LoopNative", "%s", buffer);
    return (*env)->NewStringUTF(env, buffer);
}
