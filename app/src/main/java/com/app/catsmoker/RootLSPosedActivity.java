package com.app.catsmoker;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RootLSPosedActivity extends AppCompatActivity {

    private static final String[] SUPPORTED_GAMES = {
            "com.activision.callofduty.shooter",
            "com.activision.callofduty.warzone",
            "com.garena.game.codm",
            "com.tencent.tmgp.kr.codm",
            "com.vng.codmvn",
            "com.tencent.tmgp.cod",
            "com.tencent.ig",
            "com.pubg.imobile",
            "com.pubg.krmobile",
            "com.rekoo.pubgm",
            "com.vng.pubgmobile",
            "com.tencent.tmgp.pubgmhd",
            "com.dts.freefiremax",
            "com.dts.freefireth",
            "com.epicgames.fortnite"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_lsposed);

        // Instructions TextView
        TextView instructions = findViewById(R.id.instructions);
        StringBuilder instructionText = new StringBuilder("Root & LSPosed Instructions:\n\n" +
                "1. Check Root Access\n" +
                "   - Ensure device is rooted\n" +
                "   - Use 'Root Checker' to verify\n\n" +
                "2. Install Magisk\n" +
                "   - Download Magisk Canary\n\n" +
                "3. (Optional) Install Shamiko Module\n" +
                "   - Hides root detection\n\n" +
                "4. Install LSPosed_mod Module\n" +
                "   - Open Magisk and install\n\n" +
                "5. Launch LSPosed Manager\n" +
                "   - Open the app\n\n" +
                "6. Enable the CatSmoker Module\n" +
                "   - Modules → Search CatSmoker → Enable\n\n" +
                "7. Manage Supported Games\n" +
                "   - Supported games spoofed as OnePlus 12:\n");

        for (String game : SUPPORTED_GAMES) {
            instructionText.append("     - ").append(game).append("\n");
        }

        instructionText.append("\n8. Force Stop the Game\n" +
                "   - Apply changes via LSPosed");

        instructions.setText(instructionText.toString());

        // Status TextView
        TextView status = findViewById(R.id.status);
        boolean isRooted = checkRootStatus(); // Placeholder for actual root check
        status.setText(isRooted ? "Activated" : "Disabled");

        // Button to refresh status or trigger manual check
        Button refreshButton = findViewById(R.id.btn_refresh);
        refreshButton.setOnClickListener(v -> {
            boolean updatedRootStatus = checkRootStatus();
            status.setText(updatedRootStatus ? "Activated" : "Disabled");
            Toast.makeText(this, "Status refreshed", Toast.LENGTH_SHORT).show();
        });
    }

    // Placeholder method for root checking - implement actual logic as needed
    private boolean checkRootStatus() {
        // This is a simulation. In a real app, use a root detection library like RootBeer
        try {
            Runtime.getRuntime().exec("su");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}