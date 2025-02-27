package com.mb.catsmoker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MoreInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_info)
        title = "More Information"
    }
}