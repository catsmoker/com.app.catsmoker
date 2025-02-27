package com.mb.catsmoker

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import java.io.File

class RootLSPosedActivity : AppCompatActivity() {

    companion object {
        private val packageProps = mapOf(
            "com.YoStar.AetherGazer" to createOP12Props(),
            // ... (keep all other package mappings)
            "vng.games.revelation.mobile" to createOP12Props()
        )

        private fun createOP12Props(): Map<String, String> = mapOf(
            "MANUFACTURER" to "OnePlus",
            "MODEL" to "PJD110",
            "BRAND" to "OnePlus",
            "DEVICE" to "OnePlus12"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root_lsposed)

        findViewById<MaterialButton>(R.id.btnActivate).setOnClickListener {
            activateSpoofing()
        }
    }

    private fun activateSpoofing() {
        if (!isRooted()) {
            Toast.makeText(this, "Root access not detected. Please ensure your device is rooted.", Toast.LENGTH_LONG).show()
            return
        }

        try {
            spoofDeviceProperties()
            Toast.makeText(this, "Game Unlocker Activated - Spoofed as OnePlus 12", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Spoofing failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun isRooted(): Boolean {
        val paths = arrayOf(
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su"
        )
        return paths.any { File(it).exists() } || try {
            Runtime.getRuntime().exec("which su").waitFor() == 0
        } catch (e: Exception) {
            false
        }
    }

    private fun spoofDeviceProperties() {
        packageProps.forEach { (_, props) ->
            props.forEach { (key, value) ->
                try {
                    val field = android.os.Build::class.java.getDeclaredField(key)
                    field.isAccessible = true
                    field.set(null, value)
                    field.isAccessible = false
                } catch (e: Exception) {
                    // Log failure but continue with other properties
                }
            }
        }
    }
}