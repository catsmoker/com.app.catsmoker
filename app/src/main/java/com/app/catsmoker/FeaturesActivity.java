package com.app.catsmoker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class FeaturesActivity extends AppCompatActivity {

    private static final String TAG = "FeaturesActivity";
    private MaterialButton btnToggleCrosshair;

    private final androidx.activity.result.ActivityResultLauncher<Intent> overlayPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "Overlay permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Overlay permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_features);
        setTitle("More Features");

        // Initialize UI elements
        btnToggleCrosshair = findViewById(R.id.btn_toggle_crosshair);

        // Initial button states
        updateCrosshairButtonState(isServiceRunning());

        // Crosshair toggle button
        btnToggleCrosshair.setOnClickListener(v -> {
            if (isServiceRunning()) {
                Log.d(TAG, "Deactivating crosshair");
                stopService(new Intent(this, CrosshairOverlayService.class));
                Toast.makeText(this, "Crosshair Deactivated", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Activating crosshair");
                if (Settings.canDrawOverlays(this)) {
                    startService(new Intent(this, CrosshairOverlayService.class));
                    Toast.makeText(this, "Crosshair Activated", Toast.LENGTH_SHORT).show();
                } else {
                    requestOverlayPermission();
                }
            }
            updateCrosshairButtonState(isServiceRunning());
        });
    }

    private void updateCrosshairButtonState(boolean isRunning) {
        btnToggleCrosshair.setText(isRunning ? "Deactivate Crosshair" : "Activate Crosshair");
        Log.d(TAG, "Crosshair button state: " + (isRunning ? "Running" : "Not Running"));
    }

    private boolean isServiceRunning() {
        android.app.ActivityManager manager = (android.app.ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (android.app.ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (CrosshairOverlayService.class.getName().equals(service.service.getClassName())) {
                Log.d(TAG, "CrosshairOverlayService is running");
                return true;
            }
        }
        Log.d(TAG, "CrosshairOverlayService is not running");
        return false;
    }

    private void requestOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                .setData(android.net.Uri.parse("package:" + getPackageName()));
        overlayPermissionLauncher.launch(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCrosshairButtonState(isServiceRunning());
    }
}