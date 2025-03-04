package com.app.catsmoker;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.WindowManager;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MagnificationOverlayService extends Service {

    private static final String TAG = "MagnificationService";
    private WindowManager windowManager;
    private ImageView scopeView;
    private WindowManager.LayoutParams params;
    private int initialX, initialY;
    private float initialTouchX, initialTouchY;
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;

    private static final int NOTIFICATION_ID = 2;
    private static final String CHANNEL_ID = "MagnificationServiceChannel";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
        startForegroundService();
    }

    @SuppressLint("ForegroundServiceType")
    private void startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Magnification Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Magnification Overlay")
                .setContentText("Scope is active")
                .setSmallIcon(android.R.drawable.ic_menu_zoom)
                .build();

        startForeground(NOTIFICATION_ID, notification);
        Log.d(TAG, "Foreground service started");
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupOverlay(Intent intent) {
        String magnification = intent != null ? intent.getStringExtra("magnification") : "2x";
        float initialScale = "4x".equals(magnification) ? 2.0f : 1.0f; // Null-safe check

        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        scopeView = new ImageView(this);
        scopeView.setImageResource(R.drawable.scope_reticle); // Placeholder drawable
        scopeView.setScaleX(initialScale);
        scopeView.setScaleY(initialScale);

        int scopeSize = 300; // Larger for scope effect
        int layoutType = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                WindowManager.LayoutParams.TYPE_PHONE;

        params = new WindowManager.LayoutParams(
                scopeSize,
                scopeSize,
                layoutType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = 0;

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                scaleFactor *= detector.getScaleFactor();
                scaleFactor = Math.max(0.5f, Math.min(scaleFactor, 4.0f)); // Limit scale
                scopeView.setScaleX(scaleFactor * initialScale);
                scopeView.setScaleY(scaleFactor * initialScale);
                return true;
            }
        });

        windowManager.addView(scopeView, params);
        Log.d(TAG, "Magnification overlay added with " + magnification);

        scopeView.setOnTouchListener((view, event) -> {
            scaleGestureDetector.onTouchEvent(event); // Handle pinch-to-zoom
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = params.x;
                    initialY = params.y;
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    params.x = initialX + (int) (event.getRawX() - initialTouchX);
                    params.y = initialY + (int) (event.getRawY() - initialTouchY);
                    windowManager.updateViewLayout(scopeView, params);
                    return true;
                default:
                    return false;
            }
        });
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started with startId: " + startId);
        setupOverlay(intent); // Move overlay setup here with intent
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (scopeView != null) {
            try {
                windowManager.removeView(scopeView);
                Log.d(TAG, "Magnification overlay removed");
            } catch (Exception e) {
                Log.e(TAG, "Error removing magnification overlay: " + e.getMessage());
            }
            scopeView = null;
        }
        stopForeground(true);
        Log.d(TAG, "Service destroyed");
        stopSelf();
        super.onDestroy();
    }
}