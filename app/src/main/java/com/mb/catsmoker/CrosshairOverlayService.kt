package com.mb.catsmoker

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.app.NotificationCompat

class CrosshairOverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private var crosshairView: ImageView? = null
    private lateinit var params: WindowManager.LayoutParams
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "CrosshairServiceChannel"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        setupOverlay()
    }

    @SuppressLint("ForegroundServiceType")
    private fun startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Crosshair Service",
                NotificationManager.IMPORTANCE_LOW
            )
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Crosshair Overlay")
            .setContentText("Crosshair is active")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your icon
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupOverlay() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        crosshairView = ImageView(this).apply {
            setImageResource(R.drawable.aimcross)
        }

        val crosshairSize = 165
        params = WindowManager.LayoutParams(
            crosshairSize,
            crosshairSize,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 500
            y = 1200
        }

        crosshairView?.let { windowManager.addView(it, params) }

        crosshairView?.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    crosshairView?.let { windowManager.updateViewLayout(it, params) }
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroy() {
        crosshairView?.let {
            windowManager.removeView(it)
            crosshairView = null
        }
        stopForeground(true)
        super.onDestroy()
    }
}