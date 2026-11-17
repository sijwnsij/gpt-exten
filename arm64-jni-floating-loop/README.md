# ARM64 JNI 悬浮窗循环测试 APK

Android 12 / API 31 起，纯 Java + C JNI，无 AndroidX 或第三方 SDK。

## 功能
- 首次点击“授权并启动悬浮窗”，按系统提示授权“在其他应用上层显示”。
- 再次点击按钮启动前台 Service、悬浮窗口，每 1 秒由 Java 调用 `NativeBridge.tick()`，进入 `libarm64loop.so`。
- C 层维护自增计数、计算系统运行秒数，并使用 Android native log 发送 `Arm64LoopNative` 日志；将字符串返回给 Java，展示在悬浮窗。
- 悬浮窗可拖动、清空、关闭，日志保留最近 12 行；主页面可停止 Service。
- 发行 APK **只包含 ARM64：`lib/arm64-v8a/libarm64loop.so`**，不含 `armeabi-v7a` 或 `x86_64`。

## 构建
安装 Android SDK Platform 35、Build Tools 35.0.0、NDK 27.2.12479018、Java 17，设置 `ANDROID_HOME`：

```bash
./build.sh
```

生成 `build/Arm64OverlayDemo-arm64-v8a.apk`。调试签名，仅供测试，非 Play 商店正式发行证书。

## 安装和测试

```bash
adb install -r build/Arm64OverlayDemo-arm64-v8a.apk
adb shell am start -n com.example.arm64loop/.MainActivity
# 手机上允许悬浮窗权限，点击“授权并启动悬浮窗”
adb logcat -s Arm64LoopNative:I '*:S'
```

在 x86_64 CI 模拟器进行同逻辑对照测试（**不是发行包**）：

```bash
BUILD_ABI=x86_64 ./build.sh
```

## 权限
- `SYSTEM_ALERT_WINDOW`：悬浮窗，必须由用户在系统页面允许。
- `FOREGROUND_SERVICE`：保持循环服务运行，通知栏显示运行状态。

> Android 12 起系统对悬浮窗触控和前台服务有限制。该示例不尝试绕过系统授权，也不自动请求无障碍权限。
