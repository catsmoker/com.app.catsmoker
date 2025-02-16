package com.app.catsmoker;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnGameUnlocker, btnCrosshair;
    private boolean isGameUnlockerActive = false;
    private boolean isCrosshairActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeButtons();
        setButtonListeners();
    }

    private void initializeButtons() {
        btnGameUnlocker = findViewById(R.id.btnGameUnlocker);
        btnCrosshair = findViewById(R.id.btnCrosshair);
    }

    private void setButtonListeners() {
        findViewById(R.id.btnGameUnlocker).setOnClickListener(v -> toggleGameUnlocker());
        findViewById(R.id.btnCrosshair).setOnClickListener(v -> toggleCrosshair());
        findViewById(R.id.btnEditQuality).setOnClickListener(v -> startActivity(new Intent(this, EditQuality.class)));
        findViewById(R.id.btnWebView).setOnClickListener(v -> startActivity(new Intent(this, MyWebViewActivity.class)));
        findViewById(R.id.btnExit).setOnClickListener(v -> finishAffinity());
    }

    @SuppressLint("SetTextI18n")
    private void toggleGameUnlocker() {
        isGameUnlockerActive = !isGameUnlockerActive;
        btnGameUnlocker.setText(isGameUnlockerActive ? "Deactivate Game Unlocker" : "Activate Game Unlocker");
        Toast.makeText(this, "GameUnlocker " + (isGameUnlockerActive ? "Activated" : "Deactivated"), Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    private void toggleCrosshair() {
        if (isCrosshairActive) {
            stopService(new Intent(this, CrosshairOverlayService.class));
            btnCrosshair.setText("Activate Crosshair");
            Toast.makeText(this, "Crosshair Deactivated", Toast.LENGTH_SHORT).show();
        } else {
            if (Settings.canDrawOverlays(this)) {
                startService(new Intent(this, CrosshairOverlayService.class));
                btnCrosshair.setText("Deactivate Crosshair");
                Toast.makeText(this, "Crosshair Activated", Toast.LENGTH_SHORT).show();
            } else {
                // Show a message and ask the user to grant overlay permission
                Toast.makeText(this, "Please grant overlay permission", Toast.LENGTH_SHORT).show();
                // Launch the settings screen where the user can enable the overlay permission
                startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())));
                return;  // Prevents setting the state prematurely
            }
        }
        isCrosshairActive = !isCrosshairActive;
    }
}
