package com.app.catsmoker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnGameUnlocker;
    private Button btnCrosshair;
    private boolean isGameUnlockerActive = false;
    private boolean isCrosshairActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize buttons
        btnGameUnlocker = findViewById(R.id.btnGameUnlocker);
        btnCrosshair = findViewById(R.id.btnCrosshair);
        Button btnWebView = findViewById(R.id.btnWebView);
        Button btnExit = findViewById(R.id.btnExit);

        // Set up button listeners
        btnGameUnlocker.setOnClickListener(v -> toggleGameUnlocker());
        btnCrosshair.setOnClickListener(v -> toggleCrosshair());
        btnWebView.setOnClickListener(v -> openWebView());
        btnExit.setOnClickListener(v -> exitApp());
    }

    @SuppressLint("SetTextI18n")
    private void toggleGameUnlocker() {
        isGameUnlockerActive = !isGameUnlockerActive;
        if (isGameUnlockerActive) {
            // Activate GameUnlocker functionality
            Toast.makeText(this, "GameUnlocker Activated", Toast.LENGTH_SHORT).show();
            btnGameUnlocker.setText("Deactivate Game Unlocker");
        } else {
            // Deactivate GameUnlocker functionality
            Toast.makeText(this, "GameUnlocker Deactivated", Toast.LENGTH_SHORT).show();
            btnGameUnlocker.setText("Activate Game Unlocker");
        }
    }

    @SuppressLint("SetTextI18n")
    private void toggleCrosshair() {
        isCrosshairActive = !isCrosshairActive;
        if (isCrosshairActive) {
            // Check for overlay permission
            if (Settings.canDrawOverlays(this)) {
                // Start the CrosshairOverlayService
                Intent intent = new Intent(this, CrosshairOverlayService.class);
                startService(intent);
                Toast.makeText(this, "Crosshair Activated", Toast.LENGTH_SHORT).show();
                btnCrosshair.setText("Deactivate Crosshair");
            } else {
                // Request overlay permission
                Toast.makeText(this, "Please grant overlay permission", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                isCrosshairActive = false; // Reset the state
            }
        } else {
            // Stop the CrosshairOverlayService
            Intent intent = new Intent(this, CrosshairOverlayService.class);
            stopService(intent);
            Toast.makeText(this, "Crosshair Deactivated", Toast.LENGTH_SHORT).show();
            btnCrosshair.setText("Activate Crosshair");
        }
    }

    private void openWebView() {
        // Open MyWebViewActivity
        Intent intent = new Intent(this, MyWebViewActivity.class);
        startActivity(intent);
    }

    private void exitApp() {
        // Exit the app
        finishAffinity();
    }
}