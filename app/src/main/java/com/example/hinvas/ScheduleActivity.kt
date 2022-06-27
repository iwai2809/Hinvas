package com.example.hinvas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ScheduleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        val schedule_Backbtn=findViewById<Button>(R.id.schedule_Backbtn)

        //戻るボタン
        schedule_Backbtn.setOnClickListener {
            finish()
        }
    }
}