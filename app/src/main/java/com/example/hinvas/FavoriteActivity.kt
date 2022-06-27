package com.example.hinvas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class FavoriteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        val favorite_Backbtn=findViewById<Button>(R.id.favorite_Backbtn)

        //戻るボタン
        favorite_Backbtn.setOnClickListener {
            finish()
        }
    }
}