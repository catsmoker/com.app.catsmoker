package com.app.catsmoker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import rikka.shizuku.Shizuku;
import rikka.shizuku.ShizukuProvider;
import rikka.shizuku.SystemServiceHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class EditQuality extends AppCompatActivity {
    private static final String TAG = "EditQuality";
    private static final int SHIZUKU_PERMISSION_REQUEST_CODE = 1001;

    Button apply, open;
    Spinner spinner;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private final Shizuku.OnRequestPermissionResultListener permissionResultListener =
            (requestCode, grantResult) -> {
                if (requestCode == SHIZUKU_PERMISSION_REQUEST_CODE) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "Shizuku permission granted");
                    } else {
                        showAlert("Shizuku permission denied. The app needs this permission to function.", "Error");
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_quality);

        preferences = getSharedPreferences("settings", MODE_PRIVATE);
        editor = preferences.edit();

        apply = findViewById(R.id.edit);
        open = findViewById(R.id.open);
        spinner = findViewById(R.id.select);

        // Check and request Shizuku permissions
        checkShizukuPermission();

        // Populate Spinner with options
        String[] qualityOptions = {
                "Select Quality", "Mid 480p", "Q 480p", "Mid 540p", "Q 540p", "Mid 720p", "Q 720p"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, qualityOptions);
        spinner.setAdapter(adapter);

        // Load saved selection
        int savedSelection = preferences.getInt("selection", 0);
        spinner.setSelection(savedSelection);

        // Apply quality change
        apply.setOnClickListener(v -> {
            int selectedPosition = spinner.getSelectedItemPosition();
            if (selectedPosition == 0) {
                showAlert("Select a quality first", "Warning");
            } else {
                String qualityFile = getQualityFile(selectedPosition);
                replaceWithShizuku(qualityFile, getString(R.string.file_path));
                editor.putInt("selection", selectedPosition).apply();
            }
        });

        // Open PUBG
        open.setOnClickListener(v -> {
            if (isAppInstalled(getString(R.string.pubg_package_name))) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage(getString(R.string.pubg_package_name));
                startActivity(launchIntent);
            } else {
                showAlert("PUBG is not installed", "Warning");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Shizuku.removeRequestPermissionResultListener(permissionResultListener);
    }

    private void checkShizukuPermission() {
        if (!Shizuku.pingBinder()) {
            showAlert("Shizuku is not running. Please start Shizuku first.", "Error");
            return;
        }

        Shizuku.addRequestPermissionResultListener(permissionResultListener);

        if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
            if (Shizuku.shouldShowRequestPermissionRationale()) {
                showAlert("The app needs Shizuku permission to modify system files.", "Permission Required");
            }
            Shizuku.requestPermission(SHIZUKU_PERMISSION_REQUEST_CODE);
        }
    }

    private void replaceWithShizuku(String quality, String destinationPath) {
        if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
            showAlert("Shizuku permission not granted", "Error");
            return;
        }

        try {
            // Create a UserService to execute commands with elevated privileges
            String command = String.format("cp %s %s", quality, destinationPath);
            Process process = Shizuku.newProcess(new String[]{"sh", "-c", command}, null, null);

            // Copy the asset file to the destination
            try (InputStream in = getAssets().open(quality);
                 OutputStream out = new FileOutputStream(destinationPath)) {
                copyFile(in, out);

                // Set proper permissions
                process = Shizuku.newProcess(new String[]{"chmod", "644", destinationPath}, null, null);
                int result = process.waitFor();

                if (result == 0) {
                    showAlert("Quality settings updated successfully", "Success");
                } else {
                    showAlert("Failed to set file permissions", "Error");
                }
            }
        } catch (IOException | InterruptedException e) {
            Log.e(TAG, "Error during file operation", e);
            showAlert("Failed to replace file: " + e.getMessage(), "Error");
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[8192];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    private void showAlert(String message, String title) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private boolean isAppInstalled(String packageName) {
        try {
            getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private String getQualityFile(int position) {
        return switch (position) {
            case 1 -> "quality480p/UserCustom.ini";
            case 2 -> "quality480pMid/UserCustom.ini";
            case 3 -> "quality540p/UserCustom.ini";
            case 4 -> "quality540pMid/UserCustom.ini";
            case 5 -> "quality720p/UserCustom.ini";
            case 6 -> "quality270pMid/UserCustom.ini";
            default -> "";
        };
    }
}