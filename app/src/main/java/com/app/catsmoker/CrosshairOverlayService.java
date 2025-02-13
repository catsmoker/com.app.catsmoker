package com.app.catsmoker;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class CrosshairOverlayService extends Service {

    private WindowManager windowManager;
    private ImageView crosshairView;
    private WindowManager.LayoutParams params;
    private int initialX, initialY;
    private float initialTouchX, initialTouchY;

    @Override
    public IBinder onBind(Intent intent) {
        return null; // This service does not support binding
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the WindowManager
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        // Create the crosshair view
        crosshairView = new ImageView(this);
        crosshairView.setImageResource(R.drawable.aimcross); // Set your crosshair drawable here

        // Set a small size for the crosshair image (e.g., 50x50 pixels)
        int crosshairSize = 165; // Size in pixels
        params = new WindowManager.LayoutParams(
                crosshairSize, // Width
                crosshairSize, // Height
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        // Position the crosshair at the center of the screen
        params.gravity = Gravity.TOP | Gravity.START; // Use TOP|START for manual positioning
        params.x = 500;
        params.y = 1200;

        // Add the crosshair view to the window
        windowManager.addView(crosshairView, params);

        // Set up touch listener to make the crosshair draggable
        crosshairView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Save the initial touch coordinates and window position
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        // Calculate the new position of the window
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        // Update the window layout
                        windowManager.updateViewLayout(crosshairView, params);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Remove the crosshair view when the service is destroyed
        if (crosshairView != null) {
            windowManager.removeView(crosshairView);
        }
    }
}