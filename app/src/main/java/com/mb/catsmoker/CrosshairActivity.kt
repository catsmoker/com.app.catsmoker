package com.mb.catsmoker

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class CrosshairActivity : AppCompatActivity() {

    private lateinit var btnActivateCrosshair: MaterialButton
    private var isServiceBound = false
    private var serviceConnection: ServiceConnection? = null

    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (Settings.canDrawOverlays(this)) {
            startCrosshairService()
            updateButtonState(true)
            Toast.makeText(this, "Crosshair Activated", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Overlay permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crosshair)

        btnActivateCrosshair = findViewById(R.id.btnActivateCrosshair)
        updateButtonState(isServiceRunning())

        btnActivateCrosshair.setOnClickListener {
            if (isServiceRunning()) {
                stopCrosshairService()
                updateButtonState(false)
                Toast.makeText(this, "Crosshair Deactivated", Toast.LENGTH_SHORT).show()
            } else {
                if (Settings.canDrawOverlays(this)) {
                    startCrosshairService()
                    updateButtonState(true)
                    Toast.makeText(this, "Crosshair Activated", Toast.LENGTH_SHORT).show()
                } else {
                    requestOverlayPermission()
                }
            }
        }
    }

    private fun updateButtonState(isRunning: Boolean) {
        btnActivateCrosshair.text = if (isRunning) "Deactivate Crosshair" else "Activate Crosshair"
    }

    private fun isServiceRunning(): Boolean {
        return isServiceBound // Simplified check; could be enhanced with service state tracking
    }

    private fun requestOverlayPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
            data = android.net.Uri.parse("package:$packageName")
        }
        overlayPermissionLauncher.launch(intent)
    }

    private fun startCrosshairService() {
        val intent = Intent(this, CrosshairOverlayService::class.java)
        startForegroundService(intent)
        bindService(intent, object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                isServiceBound = true
                serviceConnection = this
            }
            override fun onServiceDisconnected(name: ComponentName?) {
                isServiceBound = false
            }
        }, Context.BIND_AUTO_CREATE)
    }

    private fun stopCrosshairService() {
        val intent = Intent(this, CrosshairOverlayService::class.java)
        stopService(intent)
        serviceConnection?.let { unbindService(it) }
        isServiceBound = false
    }

    override fun onDestroy() {
        if (isServiceBound) {
            serviceConnection?.let { unbindService(it) }
        }
        super.onDestroy()
    }
}