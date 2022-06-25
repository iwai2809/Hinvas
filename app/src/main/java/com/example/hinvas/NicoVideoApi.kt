package com.example.hinvas

import android.util.Log
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class NicoVideoApi(private val q: String, targets: String, _sort: String) {

    private val endPointURL: String = "https://api.search.nicovideo.jp/api/v2/snapshot/video/contents/search"

    private var url: String = ""
    val getUrl = NicoVideoApi::url
    val global = Global.getInstance()


    var data: String = ""

    init{
        url = "$endPointURL?q=$q&targets=$targets&_sort=$_sort"
    }

    fun setParams(paramsName: String, params: String){
        url = "$url&$paramsName=$params"
    }

    fun setParamsOffset(offset: Int, limit: Int){
        url = "$url&_offset=$offset&_limit=$limit"
    }

    fun setParamsContext(context: String){
        url = "$url&_context=$context"
    }

    @Throws(IOException::class)
    fun getApiData(): String{

        //OkHttpClinet生成
        val client = OkHttpClient()

        //request生成
        val request: Request = Request.Builder().url(url).build()

        Log.d("Result", "url=$url")

        //非同期リクエスト
        client.newCall(request)
            .enqueue(object : Callback {
                //エラーのとき
                override fun onFailure(call: Call, e: IOException) {
                    Log.v("JSON", "取れてない")
                    Log.e("Result", e.message.toString())
                }

                //正常のとき
                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response){

                    //response取り出し
                    val jsonStr = response.peekBody(99999L).string()
                    Log.d("Result", "jsonStr=$jsonStr")
                    global.jsonStr = jsonStr
                    Log.d("Result", "$global.jsonStr")
                }
            })

        //JSON処理
        return try {
            //jsonパース
            val json = JSONObject(global.jsonStr)
            json.getString("data")
        } catch (e: java.lang.Exception) {
            Log.e("Result", e.message!!)
            "エラー"
        }
    }
}