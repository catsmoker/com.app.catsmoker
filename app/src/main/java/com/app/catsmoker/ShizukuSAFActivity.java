package com.app.catsmoker;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ShizukuSAFActivity extends AppCompatActivity {

    private static final String SHIZUKU_PACKAGE = "moe.shizuku.privileged.api";
    private static final String PUBG_PATH = "Android/data/com.tencent.ig/files/UE4Game/ShadowTrackerExtra/ShadowTrackerExtra/Saved/SaveGames/active.sav";
    private static final String ASSET_FILE = "PUBG/active.sav";
    private Spinner gameSpinner;
    private ActivityResultLauncher<Intent> safLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shizukusaf);
        setTitle("Shizuku & SAF");

        // Instructions TextView
        TextView instructions = findViewById(R.id.instructions);
        String instructionText = "Starting Shizuku via Wireless Debugging (Android 11+)\n" +
                "No PC required but must be repeated after each reboot.\n\n" +
                "1. Enable Wireless Debugging\n" +
                "   - Enable Developer Options and USB Debugging\n" +
                "   - Open Wireless Debugging settings and enable it\n\n" +
                "2. Pairing with Shizuku (Only Once)\n" +
                "   - Start pairing in Shizuku\n" +
                "   - Tap 'Pair device with pairing code' in Wireless Debugging\n" +
                "   - Enter the pairing code from Shizuku’s notification\n\n" +
                "3. Start Shizuku\n" +
                "   - If it doesn’t start, toggle Wireless Debugging off and on\n\n" +
                "---\n\n" +
                "Starting Shizuku via ADB (Android 10 and below)\n" +
                "Requires a PC and must be repeated after each reboot.\n\n" +
                "1. Install ADB\n" +
                "   - Download SDK Platform Tools from Google\n" +
                "   - Open terminal and type 'adb' to verify\n\n" +
                "2. Enable USB Debugging\n" +
                "   - Settings → About Phone → Tap 'Build Number' 7 times\n" +
                "   - Enable USB Debugging in Developer Options\n\n" +
                "3. Connect to PC & Authorize ADB\n" +
                "   - Run 'adb devices' in terminal\n" +
                "   - Allow USB debugging on phone\n\n" +
                "4. Start Shizuku\n" +
                "   - Run: adb shell sh /sdcard/Android/data/moe.shizuku.privileged.api/files/start.sh\n" +
                "   - Check Shizuku app for confirmation";
        instructions.setText(instructionText);

        // Game Selection Spinner
        gameSpinner = findViewById(R.id.game_spinner);
        String[] games = {"None", "PUBG"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, games);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gameSpinner.setAdapter(adapter);

        // Buttons
        Button btnStartShizuku = findViewById(R.id.btn_start_shizuku);
        Button btnStartSaf = findViewById(R.id.btn_start_saf);

        btnStartShizuku.setOnClickListener(v -> {
            if (isShizukuInstalled()) {
                Toast.makeText(this, "Not implemented yet.", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/RikkaApps/Shizuku/releases"));
                startActivity(intent);
            }
        });

        // SAF Launcher for directory access
        safLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri treeUri = result.getData().getData();
                if (treeUri != null && "PUBG".equals(gameSpinner.getSelectedItem())) {
                    replacePubgActiveSav(treeUri);
                } else {
                    Toast.makeText(this, "Directory access granted, but no action taken.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnStartSaf.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse(Environment.getExternalStorageDirectory().getPath()));
            safLauncher.launch(intent);
        });
    }

    private boolean isShizukuInstalled() {
        try {
            getPackageManager().getPackageInfo(SHIZUKU_PACKAGE, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void replacePubgActiveSav(Uri treeUri) {
        try {
            // Build the target URI for active.sav in PUBG directory
            Uri targetUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, PUBG_PATH);

            // Copy asset file to SAF directory
            try (InputStream inputStream = getAssets().open(ASSET_FILE);
                 FileOutputStream outputStream = (FileOutputStream) getContentResolver().openOutputStream(targetUri)) {
                if (outputStream == null) {
                    Toast.makeText(this, "Failed to open output stream", Toast.LENGTH_SHORT).show();
                    return;
                }
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                Toast.makeText(this, "active.sav replaced successfully", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Failed to replace active.sav: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}