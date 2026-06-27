package com.custom.automation.helper;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Switch;

public class FloatingPanelService extends Service {
    private WindowManager windowManager;
    private View floatingView;
    private WindowManager.LayoutParams layoutParams;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        floatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_panel, null);

        layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        layoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        layoutParams.y = 150;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(floatingView, layoutParams);

        SharedPreferences prefs = getSharedPreferences("AutomationSettings", MODE_PRIVATE);
        Switch swToggle = floatingView.findViewById(R.id.sw_toggle_automation);
        Button btnClose = floatingView.findViewById(R.id.btn_close_panel);

        swToggle.setChecked(prefs.getBoolean("is_active", false));
        swToggle.setOnCheckedChangeListener((buttonView, isChecked) ->
            prefs.edit().putBoolean("is_active", isChecked).apply());

        btnClose.setOnClickListener(v -> stopSelf());

        // ميزة سحب وتحريك اللوحة العائمة بحرية عبر الشاشة
        floatingView.findViewById(R.id.panel_drag_header).setOnTouchListener(new View.OnTouchListener() {
            private int initialY;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialY = layoutParams.y;
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        layoutParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(floatingView, layoutParams);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingView != null) windowManager.removeView(floatingView);
    }
}
