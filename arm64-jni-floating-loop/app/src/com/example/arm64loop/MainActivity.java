package com.example.arm64loop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class MainActivity extends Activity {
    private TextView status;
    private String abiMessage = "JNI 尚未检测";

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        try {
            abiMessage = "JNI 加载成功，当前架构：" + NativeBridge.abi();
        } catch (UnsatisfiedLinkError error) {
            abiMessage = "JNI 加载失败：" + error.getMessage();
        }

        LinearLayout root = new LinearLayout(this);
        root.setPadding(dp(24), dp(42), dp(24), dp(24));
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(244, 247, 252));

        TextView title = new TextView(this);
        title.setText("ARM64 JNI 循环悬浮窗");
        title.setTextSize(23);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setTextColor(Color.rgb(22, 34, 56));
        root.addView(title);

        TextView info = new TextView(this);
        info.setText("Android 12+ · Java 调用原生 .so · 每秒循环打印一次");
        info.setTextColor(Color.DKGRAY);
        info.setTextSize(14);
        info.setPadding(0, dp(13), 0, dp(18));
        root.addView(info);

        status = new TextView(this);
        status.setTextSize(15);
        status.setTextColor(Color.rgb(35, 58, 84));
        status.setPadding(dp(16), dp(16), dp(16), dp(16));
        GradientDrawable card = new GradientDrawable();
        card.setColor(Color.WHITE);
        card.setCornerRadius(dp(13));
        status.setBackground(card);
        root.addView(status, new LinearLayout.LayoutParams(-1, -2));

        Button start = new Button(this);
        start.setText("授权并启动悬浮窗");
        LinearLayout.LayoutParams startLp = new LinearLayout.LayoutParams(-1, dp(56));
        startLp.topMargin = dp(24);
        root.addView(start, startLp);
        start.setOnClickListener(v -> {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } else if (abiMessage.startsWith("JNI 加载成功")) {
                startForegroundService(new Intent(this, LoopOverlayService.class));
                status.setText(abiMessage + "\n已请求启动悬浮窗。");
            } else {
                status.setText(abiMessage + "\n原生库未加载，不能启动。");
            }
        });

        Button stop = new Button(this);
        stop.setText("停止循环并关闭悬浮窗");
        LinearLayout.LayoutParams stopLp = new LinearLayout.LayoutParams(-1, dp(54));
        stopLp.topMargin = dp(8);
        root.addView(stop, stopLp);
        stop.setOnClickListener(v -> {
            stopService(new Intent(this, LoopOverlayService.class));
            refreshStatus();
        });

        TextView tip = new TextView(this);
        tip.setText("首次需要在系统页面允许“在其他应用上层显示”。\n"
                + "悬浮窗可拖动、清空日志、关闭。\n"
                + "通过 adb logcat -s Arm64LoopNative:I 查看原生打印。");
        tip.setTextSize(13);
        tip.setGravity(Gravity.START);
        tip.setTextColor(Color.GRAY);
        tip.setPadding(0, dp(20), 0, 0);
        root.addView(tip);
        setContentView(root);
        refreshStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (status != null) {
            refreshStatus();
        }
    }

    private void refreshStatus() {
        String permission = Settings.canDrawOverlays(this) ? "已授权" : "未授权";
        status.setText(abiMessage + "\n悬浮窗权限：" + permission);
    }

    private int dp(float size) {
        return (int) (size * getResources().getDisplayMetrics().density + 0.5f);
    }
}
