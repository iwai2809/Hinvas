package com.example.hinvas

import android.util.Log
import org.json.JSONObject

class YoutubeApi{
    //APIキー
    private val apiKey:String = "AIzaSyBXdX4BdeprET5FnUnh5DTHAcXSaoqP_cU"

    //Youtube Data API v3をたたくためのurl
    private  val endPointUrl:String = "https://www.googleapis.com/youtube/v3/"

    //Youtube Data API v3 のmethod配列
    private val method: List<String> = listOf("videos", "channels", "search")

    private var methods = 0

    //URLを組み替える
    //単一の動画内の要素を返すURL
    fun getVideos(videoID:String):String{
        methods = 0
        val url = "$endPointUrl${method[methods]}?id=$videoID&key=$apiKey&part=snippet"
        Log.d("videoURL", "url=$url")
        return HttpRequest().getRequest(url)
    }

    //チャンネルの情報を取得するためのURL
    fun getChannels(channelID:String):String{
        methods = 1
        val url = "$endPointUrl${method[methods]}?id=$channelID&key=$apiKey&part=snippet"
        Log.d("channelURL", "url=$url")
        return HttpRequest().getRequest(url)
    }

    //channelに関する情報を取るためのURL
    fun getChannelVideosSearch(channelID: String, publishTime: String):String{
        methods = 2
        val url = "$endPointUrl${method[methods]}?channelId=$channelID&key=$apiKey&order=date&publishedAfter=$publishTime&type=video&part=snippet"
        Log.d("searchURL", "url=$url")
        return HttpRequest().getRequest(url)
    }

    //カスタムURLを元にチャンネルIDを取得
    fun customUrlConverter(customUrl:String):String{
        methods = 2
        val url = "$endPointUrl${method[methods]}?key=$apiKey&type=channel&part=snippet&q=$customUrl"
        Log.d("カスタムURL","url=$url")
        return try {
            JSONObject(HttpRequest().getRequest(url)).getJSONArray("items").getJSONObject(0).getJSONObject("id").getString("channelId").toString()
        }catch (e: Exception) {
            "入力されたカスタムURLでは推測できませんでした。"
        }
    }


    //  配信情報を取得する
    fun getYoutubeLive(channelID: String):String{
        methods = 2
        val url = "$endPointUrl${method[methods]}?channelId=$channelID&key=$apiKey&part=snippet&order=date&maxResults=1"
//        val url = "$endPointUrl${method[methods]}?channelId=$channelID&key=$apiKey&part=snippet&order=date"
        Log.d("liveURL", "url=$url")
        return HttpRequest().getRequest(url)
    }

    //配信の終了時刻を取得する
    fun getYoutubeLiveStatus(videoID: String): Triple<String, String, String>{
        methods = 0
        val url = "$endPointUrl${method[methods]}?id=$videoID&key=$apiKey&part=liveStreamingDetails"
        Log.d("liveURL", "url=$url")
        val broadcastTime = broadcastData(HttpRequest().getRequest(url))
        return  Triple(broadcastTime.first, broadcastTime.second, broadcastTime.third)
    }

    fun getLiveStatus(videoID: String): String {
        methods = 0
        val url = "$endPointUrl${method[methods]}?id=$videoID&key=$apiKey&part=liveStreamingDetails"
        Log.d("liveURL", "url=$url")
        return HttpRequest().getRequest(url)
    }

    //配信中、予約配信、配信終了、動画か判定する
    fun checkLive(jsonStr: String):String {
        Log.d("Result", "jsonStr=$jsonStr")

        try {
            val json = JSONObject(jsonStr)
            val items = json.getJSONArray("items")
            val itemList = items.getJSONObject(0)
            //配信か動画かどうかを判定する
            if (itemList.isNull("liveStreamingDetails")) {
                //動画の時
                return "動画です"
            }

            //上記のif文で動画でないことはわかったので、
            //配信中、予約している配信、配信終了かどうかを判断する
            //snippetにある"liveBroadcastContent"という項目で判断する
            val liveStatus = youtubeVideoInputData(getVideos(itemList.getString("id")))
            Log.d("liveStatus", "liveStatus=$liveStatus")

            Log.d("liveBroadcastContent", liveStatus.liveBroadcastContent)
            //配信中の時
            if (liveStatus.liveBroadcastContent == "live") {
                return "配信中です"
            }
            //予約している配信の時
            if (liveStatus.liveBroadcastContent == "upcoming　") {
                return "予約している配信です"
            }

            //配信が終わっている時
            return "配信は終了しています"
        } catch (e: Exception) {
            return "エラー"
        }
    }
}
