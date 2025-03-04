package com.app.catsmoker;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class FeaturesActivity extends AppCompatActivity {

    private static final String TAG = "FeaturesActivity";
    private MaterialButton btnToggleCrosshair;
    private MaterialButton btnToggleMagnification;
    private Spinner magnificationSpinner;

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
        btnToggleMagnification = findViewById(R.id.btn_toggle_magnification);
        magnificationSpinner = findViewById(R.id.magnification_spinner);

        // Setup magnification spinner
        String[] magnifications = {"2x", "4x"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, magnifications);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        magnificationSpinner.setAdapter(adapter);

        // Initial button states
        updateCrosshairButtonState(isServiceRunning(CrosshairOverlayService.class));
        updateMagnificationButtonState(isServiceRunning(MagnificationOverlayService.class));

        // Crosshair toggle button
        btnToggleCrosshair.setOnClickListener(v -> {
            if (isServiceRunning(CrosshairOverlayService.class)) {
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
            updateCrosshairButtonState(isServiceRunning(CrosshairOverlayService.class));
        });

        // Magnification toggle button
        btnToggleMagnification.setOnClickListener(v -> {
            if (isServiceRunning(MagnificationOverlayService.class)) {
                Log.d(TAG, "Deactivating magnification overlay");
                stopService(new Intent(this, MagnificationOverlayService.class));
                Toast.makeText(this, "Magnification Deactivated", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Activating magnification overlay");
                if (Settings.canDrawOverlays(this)) {
                    Intent intent = new Intent(this, MagnificationOverlayService.class);
                    intent.putExtra("magnification", magnificationSpinner.getSelectedItem().toString());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent);
                    } else {
                        startService(intent);
                    }
                    Toast.makeText(this, "Magnification Activated (" + magnificationSpinner.getSelectedItem() + ")", Toast.LENGTH_SHORT).show();
                } else {
                    requestOverlayPermission();
                }
            }
            updateMagnificationButtonState(isServiceRunning(MagnificationOverlayService.class));
        });
    }

    private void updateCrosshairButtonState(boolean isRunning) {
        btnToggleCrosshair.setText(isRunning ? "Deactivate Crosshair" : "Activate Crosshair");
        Log.d(TAG, "Crosshair button state: " + (isRunning ? "Running" : "Not Running"));
    }

    private void updateMagnificationButtonState(boolean isRunning) {
        btnToggleMagnification.setText(isRunning ? "Deactivate Magnification" : "Activate Magnification");
        Log.d(TAG, "Magnification button state: " + (isRunning ? "Running" : "Not Running"));
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        android.app.ActivityManager manager = (android.app.ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (android.app.ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.d(TAG, serviceClass.getSimpleName() + " is running");
                return true;
            }
        }
        Log.d(TAG, serviceClass.getSimpleName() + " is not running");
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
        updateCrosshairButtonState(isServiceRunning(CrosshairOverlayService.class));
        updateMagnificationButtonState(isServiceRunning(MagnificationOverlayService.class));
    }
}