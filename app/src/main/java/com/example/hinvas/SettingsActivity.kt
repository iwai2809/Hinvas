package com.example.hinvas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val settings_Backbtn=findViewById<Button>(R.id.settings_Backbtn)

        //戻るボタン
        settings_Backbtn.setOnClickListener {
            finish()
        }
    }
}