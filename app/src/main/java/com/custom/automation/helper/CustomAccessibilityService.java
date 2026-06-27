package com.custom.automation.helper;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.view.accessibility.AccessibilityEvent;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;

public class CustomAccessibilityService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // التحقق من حزمة التطبيق المستهدف المحدد للأتمتة (مثل تطبيقات توصيل الركاب com.jeeny.driver)
        if (event.getPackageName() == null || !event.getPackageName().toString().equals("com.jeeny.driver")) {
            return;
        }

        SharedPreferences prefs = getSharedPreferences("AutomationSettings", MODE_PRIVATE);
        // التحقق من تفعيل مفتاح التشغيل التلقائي من اللوحة العائمة أولاً
        if (!prefs.getBoolean("is_active", false)) return;

        // جلب أبعاد الشاشة الحالية بشكل ديناميكي لتطبيق النسبة المئوية الدقيقة لزر الاستجابة
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        // حساب الإحداثيات الهندسية المباشرة لأسفل الشاشة (بنسبة 90% عمودياً وفي المنتصف أفقياً)
        int executionX = screenWidth / 2;
        int executionY = (int) (screenHeight * 0.90);

        // بناء وتنفيذ إيماءة النقر المادي المباشر فوريّاً وبأعلى سرعة ممكنة
        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
        Path clickPath = new Path();
        clickPath.moveTo(executionX, executionY);
        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(clickPath, 0, 45));

        dispatchGesture(gestureBuilder.build(), null, null);
    }

    @Override
    public void onInterrupt() {}
}
