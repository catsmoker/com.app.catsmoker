package com.mb.catsmoker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupButtons()
    }

    private fun setupButtons() {
        findViewById<MaterialButton>(R.id.btnRootLSPosed).setOnClickListener {
            startActivitySafely(RootLSPosedActivity::class.java)
        }

        findViewById<MaterialButton>(R.id.btnShizuku).setOnClickListener {
            startActivitySafely(ShizukuActivity::class.java)
        }

        findViewById<MaterialButton>(R.id.btnCrosshair).setOnClickListener {
            startActivitySafely(CrosshairActivity::class.java)
        }

        findViewById<MaterialButton>(R.id.btnWebsite).setOnClickListener {
            openWebsite("https://catsmoker.github.io")
        }

        findViewById<MaterialButton>(R.id.btnMoreInfo).setOnClickListener {
            startActivitySafely(MoreInfoActivity::class.java)
        }

        findViewById<MaterialButton>(R.id.btnExit).setOnClickListener {
            finishAffinity()
        }
    }

    private fun startActivitySafely(cls: Class<*>) {
        try {
            startActivity(Intent(this, cls))
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openWebsite(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: Exception) {
            Toast.makeText(this, "Unable to open browser: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}