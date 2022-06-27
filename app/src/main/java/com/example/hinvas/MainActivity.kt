package com.example.hinvas

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    //Widget宣言
    lateinit var ans: TextView
    lateinit var clickButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Widget初期化
        //Widget初期化
        ans = findViewById<View>(R.id.txt01) as TextView
        clickButton = findViewById<View>(R.id.btn01) as Button
        val transitionText= findViewById<TextView>(R.id.transition_text)
        val homeBtn=findViewById<Button>(R.id.home_btn)
        val listBtn=findViewById<Button>(R.id.list_btn)
        val favoriteBtn=findViewById<Button>(R.id.favorite_btn)
        val scheduleBtn=findViewById<Button>(R.id.schedule_btn)
        val settingsBtn=findViewById<Button>(R.id.settings_btn)


        //ボタンクリック

        clickButton.setOnClickListener(View.OnClickListener {
            //httpリクエスト
            try {
//                //okhttpを利用するカスタム関数（下記）
//                httpRequest("https://api.search.nicovideo.jp/api/v2/snapshot/video/contents/search?q=%E5%88%9D%E9%9F%B3%E3%83%9F%E3%82%AF&targets=title&fields=contentId,title,viewCounter&_sort=-viewCounter&_offset=0&_limit=3&_context=apiguide")
                val nicoVideo: NicoVideoApi = NicoVideoApi("%E5%88%9D%E9%9F%B3%E3%83%9F%E3%82%AF", "title", "-viewCounter")

                nicoVideo.setParams("fields", "contentId,title,viewCounter")
                nicoVideo.setParamsOffset(0, 3)
                nicoVideo.setParamsContext("apiguide")

                //親スレッドUI更新
                val mainHandler = Handler(Looper.getMainLooper())
                mainHandler.post(Runnable { ans.text = nicoVideo.getApiData() })

            } catch (e: Exception) {
                Log.e("Request", e.message!!)
            }
        })

        //ホームに遷移
        homeBtn.setOnClickListener{
            transitionText.setText(R.string.text_home)

        }

        //一覧画面に遷移
        listBtn.setOnClickListener{
//            transitionText.setText(R.string.text_list)
            val  list_Intent =Intent(this,ListActivity::class.java)
            startActivity(list_Intent)
        }

        //お気に入りリストに遷移
        favoriteBtn.setOnClickListener{
//            transitionText.setText(R.string.text_favorite)
            val  favorite_Intent=Intent(this,FavoriteActivity::class.java)
            startActivity(favorite_Intent)
        }

        //schedule画面に遷移
        scheduleBtn.setOnClickListener{
//            transitionText.setText(R.string.text_schedule)
            val schedule_Intent=Intent(this,ScheduleActivity::class.java)
            startActivity(schedule_Intent)
        }

        //設定画面に遷移
        settingsBtn.setOnClickListener{
//            transitionText.setText(R.string.text_settings)
            val settings_Intent=Intent(this,SettingsActivity::class.java)
            startActivity(settings_Intent)
        }

    }
}