package com.example.arm64loop;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayDeque;

public final class LoopOverlayService extends Service {
    private static final String CHANNEL_ID = "arm64_jni_overlay";
    private static final int NOTIFICATION_ID = 1001;
    private static final int MAX_LINES = 12;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final ArrayDeque<String> recentLines = new ArrayDeque<>();
    private WindowManager wm;
    private View window;
    private WindowManager.LayoutParams params;
    private TextView logs;
    private TextView detail;
    private ScrollView scroll;
    private boolean looping;

    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            if (!looping) {
                return;
            }
            try {
                appendLine(NativeBridge.tick());
            } catch (Throwable error) {
                appendLine("JNI 错误：" + error.getClass().getSimpleName());
                looping = false;
                stopSelf();
                return;
            }
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationManager nm = getSystemService(NotificationManager.class);
        nm.createNotificationChannel(new NotificationChannel(CHANNEL_ID, "JNI 悬浮窗运行状态",
                NotificationManager.IMPORTANCE_LOW));
        PendingIntent pi = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("JNI 原生循环正在运行")
                .setContentText("点击回到应用，悬浮窗每秒更新")
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .setContentIntent(pi)
                .setOngoing(true)
                .build();
        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!Settings.canDrawOverlays(this)) {
            stopSelf();
            return START_NOT_STICKY;
        }
        if (window == null) {
            showOverlay();
        }
        if (!looping) {
            looping = true;
            handler.post(ticker);
        }
        return START_NOT_STICKY;
    }

    private void showOverlay() {
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        params = new WindowManager.LayoutParams(
                dp(332), dp(273),
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = dp(18);
        params.y = dp(110);

        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(13), dp(10), dp(13), dp(13));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.rgb(20, 27, 42));
        bg.setCornerRadius(dp(16));
        bg.setStroke(dp(1), Color.rgb(71, 88, 119));
        panel.setBackground(bg);

        LinearLayout titleRow = new LinearLayout(this);
        titleRow.setGravity(Gravity.CENTER_VERTICAL);
        panel.addView(titleRow, new LinearLayout.LayoutParams(-1, dp(44)));

        TextView title = new TextView(this);
        title.setText("●  JNI 循环日志");
        title.setTextColor(Color.rgb(104, 239, 176));
        title.setTextSize(16);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        titleRow.addView(title, new LinearLayout.LayoutParams(0, -2, 1));
        title.setOnTouchListener(new View.OnTouchListener() {
            private int sx, sy;
            private float dx, dy;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        sx = params.x;
                        sy = params.y;
                        dx = event.getRawX();
                        dy = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = sx + (int) (event.getRawX() - dx);
                        params.y = sy + (int) (event.getRawY() - dy);
                        wm.updateViewLayout(window, params);
                        return true;
                    default:
                        return true;
                }
            }
        });

        Button clear = new Button(this);
        clear.setText("清空");
        clear.setTextSize(12);
        clear.setAllCaps(false);
        titleRow.addView(clear, new LinearLayout.LayoutParams(dp(64), dp(42)));
        clear.setOnClickListener(v -> {
            recentLines.clear();
            logs.setText("等待下次 JNI 调用...\n");
        });

        Button close = new Button(this);
        close.setText("×");
        close.setTextSize(20);
        close.setAllCaps(false);
        titleRow.addView(close, new LinearLayout.LayoutParams(dp(44), dp(42)));
        close.setOnClickListener(v -> stopSelf());

        detail = new TextView(this);
        detail.setTextColor(Color.rgb(165, 185, 211));
        detail.setTextSize(12);
        detail.setPadding(dp(3), dp(3), 0, dp(7));
        try {
            detail.setText("Native ABI: " + NativeBridge.abi() + "   |   间隔 1s");
        } catch (UnsatisfiedLinkError error) {
            detail.setText("Native ABI: 加载失败");
        }
        panel.addView(detail);

        scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        GradientDrawable logBg = new GradientDrawable();
        logBg.setColor(Color.rgb(9, 14, 26));
        logBg.setCornerRadius(dp(10));
        scroll.setBackground(logBg);
        LinearLayout.LayoutParams scrollLp = new LinearLayout.LayoutParams(-1, 0, 1);
        panel.addView(scroll, scrollLp);

        logs = new TextView(this);
        logs.setText("等待原生 JNI 循环...\n");
        logs.setTextColor(Color.rgb(129, 224, 177));
        logs.setTextSize(11.5f);
        logs.setTypeface(Typeface.MONOSPACE);
        logs.setPadding(dp(10), dp(9), dp(8), dp(8));
        scroll.addView(logs);

        window = panel;
        wm.addView(window, params);
    }

    private void appendLine(String value) {
        recentLines.addLast(value);
        while (recentLines.size() > MAX_LINES) {
            recentLines.removeFirst();
        }
        StringBuilder sb = new StringBuilder();
        for (String line : recentLines) {
            sb.append(line).append('\n');
        }
        logs.setText(sb.toString());
        scroll.post(() -> scroll.fullScroll(View.FOCUS_DOWN));
    }

    @Override
    public void onDestroy() {
        looping = false;
        handler.removeCallbacks(ticker);
        if (wm != null && window != null) {
            wm.removeView(window);
            window = null;
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private int dp(float size) {
        return (int) (size * getResources().getDisplayMetrics().density + 0.5f);
    }
}
