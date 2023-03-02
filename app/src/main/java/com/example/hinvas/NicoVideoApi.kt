package com.example.hinvas

import android.util.Log
import org.json.JSONObject
import java.io.IOException

class NicoVideoApi(q: String, targets: String, _sort: String) {

    private val endPointURL: String = "https://api.search.nicovideo.jp/api/v2/snapshot/video/contents/search"

    private var url: String = ""

    //urlの作成
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
    fun getApiData(): String {
        //Json処理
        return try {
            val json = JSONObject(HttpRequest().getRequest(url))
            json.getString("data")
        }catch (e: java.lang.Exception){
            Log.e("Result", e.message!!)
            "エラー"
        }
    }
}