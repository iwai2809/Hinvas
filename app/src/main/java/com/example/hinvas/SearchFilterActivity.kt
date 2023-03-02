package com.example.hinvas


import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

class SearchFilterActivity  : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val youtubeBtn=findViewById<ImageButton>(R.id.YoutubeBtn)
        val twitcasBtn=findViewById<ImageButton>(R.id.TwicasBtn)
        val twitchBtn=findViewById<ImageButton>(R.id.TwitchBtn)
        val niconicoBtn=findViewById<ImageButton>(R.id.niconicobtn)
        val ApplyLayout=findViewById<RelativeLayout>(R.id.ApplyLayout)
        val ApplyBtn=findViewById<Button>(R.id.ApplyBtn)



        // Spinnerの取得
        val sortSpinner = findViewById<Spinner>(R.id.SortSpinner)

        // Adapterの生成
        val sortAdapter = ArrayAdapter.createFromResource(this, R.array.sortspinnerItems, R.layout.spinner_item)

        // 選択肢の各項目のレイアウト
        sortAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        // AdapterをSpinnerのAdapterとして設定
        sortSpinner.adapter = sortAdapter

        // OnItemSelectedListenerの実装
        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            // 項目が選択された時に呼ばれる
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            }

            // 基本的には呼ばれないが、何らかの理由で選択されることなく項目が閉じられたら呼ばれる
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

            var youtubeJudge=true
            youtubeBtn.setOnClickListener {
                youtubeJudge = if(youtubeJudge){
                    youtubeBtn.setBackgroundResource(R.color.blue)
                    false
                }else{
                    youtubeBtn.setBackgroundResource(R.color.white)
                    true
                }
            }
            var twitchJudge=true
            twitchBtn.setOnClickListener {
                twitchJudge = if(twitchJudge){
                    twitchBtn.setBackgroundResource(R.color.blue)
                    false
                }else{
                    twitchBtn.setBackgroundResource(R.color.white)
                    true
                }
            }
            var twitcasJudge=true
            twitcasBtn.setOnClickListener {
                twitcasJudge = if(twitcasJudge){
                    twitcasBtn.setBackgroundResource(R.color.blue)
                    false
                }else{
                    twitcasBtn.setBackgroundResource(R.color.white)
                    true
                }
            }

            var niconicoJudge=true
            niconicoBtn.setOnClickListener {
                niconicoJudge = if(niconicoJudge){
                    niconicoBtn.setBackgroundResource(R.color.blue)
                    false
                }else{
                    niconicoBtn.setBackgroundResource(R.color.white)
                    true
                }
            }
            ApplyLayout.setOnClickListener{
                Toast.makeText(applicationContext,"適用されました",Toast.LENGTH_LONG).show()
            }
            ApplyBtn.setOnClickListener{
                Toast.makeText(applicationContext,"適用されました",Toast.LENGTH_LONG).show()
            }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var returnVal = true

        if (item.itemId == android.R.id.home) {
            finish()
        } else {
            returnVal = super.onOptionsItemSelected(item)
        }
        return returnVal
    }
}