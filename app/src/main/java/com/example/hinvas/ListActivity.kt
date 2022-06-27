package com.example.hinvas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

//一覧画面です
class ListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val list_Backbtn=findViewById<Button>(R.id.list_Backbtn)

        //戻るボタン
        list_Backbtn.setOnClickListener {
            finish()
        }
    }
}