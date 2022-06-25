package com.example.hinvas

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
    }
}