package com.example.hinvas

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.hinvas.databinding.ActivityTwitcastingApiBinding
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException



class TwitCastingAPI : AppCompatActivity() {

    private lateinit var binding: ActivityTwitcastingApiBinding
    private val oauthURL = "https://apiv2.twitcasting.tv/oauth2/authorize?client_id=%s&response_type=code"
    private var strAccessToken : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_twitcasting_api)

        openBrowser()
        // View Binding
        binding = ActivityTwitcastingApiBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // 【おすすめリストをlogcatに出力する】ボタンを押す
        binding.ResponseTCSearchLiveMovieButton.setOnClickListener {
            fetchRecommendList()
        }
    }

    // 外部ブラウザでツイキャスにログインして連携アプリを許可するため、暗黙的Intentで外部ブラウザを立ち上げる
    private fun openBrowser() {
        val uri = Uri.parse(String.format(oauthURL, BuildConfig.CLIENT_ID))
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

//    // intentからクエリパラメーター：codeを取り出す
//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        val code : String? = intent?.data?.getQueryParameter("code")
//        // codeの値が取れているかチェック
//        if (code != null) {
//            Log.d("code_check", code)
//        }
//        fetchAccessToken(code)
//    }

    // アスセストークンを取得する
//    private fun fetchAccessToken(code: String?) {
//        val request: Request = Request.Builder()
//            .post(createRequestBody(code))
//            .url("https://apiv2.twitcasting.tv/oauth2/access_token")
//            .build()
//        val client = OkHttpClient.Builder().build()
//        client.newCall(request)
//            .enqueue(object: Callback {
//                // エラーのとき
//                override fun onFailure(call: Call, e: IOException) {
//                    Log.v("アクセストークン値","取れてない")
//                    Log.e("Result", e.message.toString())
//                }
//
//                // 正常のとき
//                @Throws(IOException::class)
//                override fun onResponse(call: Call, response: Response) {
//                    val accessToken: AccessToken = Gson().fromJson(
//                        response.body?.charStream(),
//                        AccessToken::class.java)
//                    strAccessToken = accessToken.getAccessToken()
//                    if (strAccessToken != null) {
//                        if (strAccessToken != null) {
//                            Log.v("アクセストークン値", strAccessToken!!)
//                        }
//                    }
//                }
//            })
//    }

    private fun createRequestBody(code: String?): RequestBody {
        return FormBody.Builder()
            .add("code", code!!)
            .add("grant_type", "authorization_code")
            .add("client_id", BuildConfig.CLIENT_ID)
            .add("client_secret", BuildConfig.CLIENT_SECRET)
            .add("redirect_uri", BuildConfig.CALLBACK_URL)
            .build()
    }

    // おすすめリスト(タグ：Apex)を取得する
    private fun fetchRecommendList() {
        val accessToken = strAccessToken
        val request: Request = Request.Builder()
            .get()
            .url("https://apiv2.twitcasting.tv/search/lives?limit=3&type=tag&context=Apex&lang=ja") // AccessTokenとAPIバージョンをリクエストヘッダに指定
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("X-Api-Version", BuildConfig.API_VERSION)
            .build()
        val client = OkHttpClient.Builder().build()
        client.newCall(request)
            .enqueue(object : Callback {
                // エラーのとき
                override fun onFailure(call: Call, e: IOException) {
                    Log.v("おすすめリスト","取れてない")
                    Log.e("Result", e.message.toString())
                }

                // 正常のとき
                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response){
                    val jsonStr : String? = response.body?.string()
                    if (jsonStr != null) {
                        Log.v("おすすめリスト", jsonStr)
                    }
                }
            })
    }
}