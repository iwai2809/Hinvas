package com.example.hinvas

import android.util.Log
import okhttp3.*
import okhttp3.Headers.Companion.toHeaders
import java.io.IOException

//APIを叩いてその結果を受け取るクラス
class HttpRequest {

    private lateinit var request: Request

    @Throws(IOException::class)
    fun getRequest(API_URL:String): String{

        //request生成
        request = Request.Builder().url(API_URL).build()

        Log.d("Result", "API_URL=$API_URL")

        val response = OkHttpClient().newCall(request).execute()
        Log.d("Result","response=$response")

        return response.peekBody(999999L).string()
    }

    @Throws(IOException::class)
    fun getRequest(API_URL:String, headers : Map<String, String>): String{

        val headerBuild = headers.toHeaders()
        request = Request.Builder().headers(headerBuild).url(API_URL).build()

        Log.d("Result", "API_URL=$API_URL")

        val response = OkHttpClient().newCall(request).execute()
        Log.d("Result","response=$response")

        return response.peekBody(999999L).string()
    }
}
