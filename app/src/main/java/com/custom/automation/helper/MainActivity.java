package com.custom.automation.helper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("AutomationSettings", MODE_PRIVATE);

        EditText etMinDist = findViewById(R.id.et_min_distance);
        EditText etMaxDist = findViewById(R.id.et_max_distance);
        EditText etMinValue = findViewById(R.id.et_min_value);
        EditText etMaxValue = findViewById(R.id.et_max_value);
        Button btnAccessibility = findViewById(R.id.btn_grant_accessibility);
        Button btnLaunch = findViewById(R.id.btn_launch_panel);

        // استرجاع المدخلات المحفوظة مسبقاً لضمان عدم ضياع البيانات
        etMinDist.setText(sharedPreferences.getString("min_dist", ""));
        etMaxDist.setText(sharedPreferences.getString("max_dist", ""));
        etMinValue.setText(sharedPreferences.getString("min_val", ""));
        etMaxValue.setText(sharedPreferences.getString("max_val", ""));

        btnAccessibility.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        });

        btnLaunch.setOnClickListener(v -> {
            // حفظ المعايير المدخلة في مستودع البيانات المشترك
            sharedPreferences.edit()
                .putString("min_dist", etMinDist.getText().toString())
                .putString("max_dist", etMaxDist.getText().toString())
                .putString("min_val", etMinValue.getText().toString())
                .putString("max_val", etMaxValue.getText().toString())
                .apply();

            // التحقق من صلاحية الظهور فوق التطبيقات قبل تشغيل الخدمة
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                Intent overlayIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(overlayIntent, 123);
            } else {
                startService(new Intent(MainActivity.this, FloatingPanelService.class));
            }
        });
    }
}
