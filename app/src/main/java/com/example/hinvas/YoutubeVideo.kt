package com.example.hinvas

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime

data class YoutubeVideo(
    val videoId: String,        // 動画のID
    val title: String,          // 動画タイトル
    val thumbnailUrl: String,   // サムネイルのURL
    val channelIconUrl: String, // チャンネルアイコンのURL
    val description: String,    // 動画説明
    val publishedAt: String,    // 投稿日時
    val channelId: String,      // チャンネルID
    val channelName: String,    // チャンネル名
    val liveBroadcastContent: String    // 予約配信、配信中、動画か判定
)

data class YoutubeLive(
    val title: String,                                  // 配信タイトル
    val videoId: String,                                // 動画ID
    val thumbnailUrl: String,                           // サムネイルのURL
    val channelIconUrl: String,                         // チャンネルアイコンのURL
    val liveUrl: String,                                // 配信URL
    val description: String,                            // 概要欄
    val channelId: String,                              // チャンネルID
    val channelTitle: String,                           // チャンネル名
    val videoTime: String,                              // 動画投稿時間
    val liveStartTime: String,                          // 配信開始時間
    val scheduledStartTime: String,                     // 配信予約時間
    val liveEndTime: String,                            // 配信終了時間
    val liveBroadcastContent: String                    // 配信状況
)

@RequiresApi(Build.VERSION_CODES.O)
fun timeConverter(time: String): String{
    // 時間の入力フォーマット
    // yyyy-mm-ddThh:mm:ss
    // 2020-01-01T09:00:00
    return try {
        val universalTime = LocalDateTime.parse(time).minusHours(9)
        var result = universalTime.toString()

        if (universalTime.second == 0) result += ":00"

        Log.d("timeConverter", result)
        result + "Z"
    } catch (e: Exception){
        Log.d("timeConverter", "error")
        "エラー"
    }
}

fun getYoutubeVideoIDList(jsonStr: String): ArrayList<String>{
    return try{
        val videoIds = ArrayList<String>()
        val items: JSONArray = JSONObject(jsonStr).getJSONArray("items")
        for (i in 0 until items.length()) {
            videoIds.add(items.getJSONObject(i).getJSONObject("id").getString("videoId"))
        }

        videoIds
    } catch (e: Exception){
        Log.d("getVideosIDList", "error")
        return ArrayList()
    }
}

fun youtubeVideoInputData(jsonStr: String): YoutubeVideo {

    Log.d("videoJson", "jsonStr=$jsonStr")

    try {
        //json　解体
        val json = JSONObject(jsonStr)
        val items = json.getJSONArray("items")
        val snippetObj = items.getJSONObject(0).getJSONObject("snippet")

        val livedata = YoutubeApi().getYoutubeLiveStatus(items.getJSONObject(0).getString("id"))
        var liveBroadcastContent = snippetObj.getString("liveBroadcastContent")
        liveBroadcastContent = broadcastStatus(livedata.second, liveBroadcastContent)

        return YoutubeVideo(
            items.getJSONObject(0).getString("id"),     //動画ID
            snippetObj.getString("title"),                  //動画タイトル
            snippetObj.getJSONObject("thumbnails").getJSONObject("default")
                .getString("url"),   // サムネイルのURL
            youtubeChannelInputData(YoutubeApi().getChannels(snippetObj.getString("channelId"))).icon,      // チャンネルアイコンのURL
            snippetObj.getString("description"),            //動画説明
            snippetObj.getString("publishedAt"),            //投稿日時
            snippetObj.getString("channelId"),              //チャンネルＩＤ
            snippetObj.getString("channelTitle"),           //チャンネル名
            liveBroadcastContent
        )
    } catch (e: Exception){
        return YoutubeVideo(
            "エラー",     //動画ID
            "エラー",                  //動画タイトル
            "エラー",   // サムネイルのURL
            "エラー",      // チャンネルアイコンのURL
            "エラー",            //動画説明
            "エラー",            //投稿日時
            "エラー",              //チャンネルＩＤ
            "エラー",           //チャンネル名
            "エラー"
        )
    }
}

fun youtubeLive(jsonStr: String): YoutubeLive {
    Log.d("liveJson", "jsonStr=$jsonStr")

    try {
        //json　解体
        val json = JSONObject(jsonStr)
        val items = json.getJSONArray("items")
        val snippetObj = items.getJSONObject(0).getJSONObject("snippet")

        val livedata = YoutubeApi().getYoutubeLiveStatus(items.getJSONObject(0).getString("id"))
        var liveBroadcastContent = snippetObj.getString("liveBroadcastContent")
        liveBroadcastContent = broadcastStatus(livedata.second, liveBroadcastContent)


        return YoutubeLive(
            snippetObj.getString("title"),
            items.getJSONObject(0).getString("id"),
            snippetObj.getJSONObject("thumbnails").getJSONObject("default").getString("url"),
            youtubeChannelInputData(YoutubeApi().getChannels(snippetObj.getString("channelId"))).icon,
            "https://www.youtube.com/watch?v=" + items.getJSONObject(0).getString("id"),
            snippetObj.getString("description"),
            snippetObj.getString("channelId"),
            snippetObj.getString("channelTitle"),
            snippetObj.getString("publishedAt"),
            livedata.first,
            livedata.third,
            livedata.second,
            liveBroadcastContent
        )
    } catch (e: Exception) {
        return YoutubeLive(
            "エラー",
            "エラー",
            "エラー",
            "エラー",
            "エラー",
            "エラー",
            "エラー",
            "エラー",
            "エラー",
            "エラー",
            "エラー",
            "エラー",
            "エラー"
        )
    }
}

fun broadcastData(jsonStr: String): Triple<String, String, String>{
    Log.d("Result", "jsonStr=$jsonStr")

    val json = JSONObject(jsonStr)
    val items = json.getJSONArray("items")
    val itemList = items.getJSONObject(0)

    //配信かどうか
    if (itemList.has("liveStreamingDetails")) {
        val liveStreaming = itemList.getJSONObject("liveStreamingDetails")
        //first:開始時間, second:終了時間, third:予約時間

        //予約していた配信が終わっていた
        if (liveStreaming.has("actualEndTime") && liveStreaming.has("actualStartTime") && liveStreaming.has("scheduledStartTime")) {
            return Triple(liveStreaming.getString("actualStartTime"),liveStreaming.getString("actualEndTime"), liveStreaming.getString("scheduledStartTime"))
        }

        //予約している配信の開始前
        if (liveStreaming.isNull("actualEndTime") && liveStreaming.isNull("actualStartTime") && liveStreaming.has("scheduledStartTime")){
            return  Triple("false", "false", liveStreaming.getString("scheduledStartTime"))
        }

        //予約配信の配信中
        if(liveStreaming.isNull("actualEndTime") && liveStreaming.has("actualStartTime") && liveStreaming.has("scheduledStartTime")){
            return  Triple(liveStreaming.getString("actualStartTime"), "false", liveStreaming.getString("scheduledStartTime"))
        }

        //配信中
        if(liveStreaming.has("actualStartTime") && liveStreaming.isNull("actualEndTime") && liveStreaming.isNull("scheduledStartTime")){
            return Triple(liveStreaming.getString("actualStartTime"), "false", "false")
        }

        //配信終了
        if(liveStreaming.has("actualStartTime") && liveStreaming.has("actualEndTime") && liveStreaming.isNull("scheduledStartTime")){
            return Triple(liveStreaming.getString("actualStartTime"), liveStreaming.getString("actualEndTime"), "false")
        }

    }
    return Triple("false","false", "false")
}

fun broadcastStatus(liveEndTime: String, liveBroadcastContent: String):String{
    //配信中、予約配信、配信終了、動画か判定する
    Log.d("broadcastStatus", "liveEndTime=$liveEndTime")
    Log.d("broadcastStatus", "liveBroadcastContent=$liveBroadcastContent")

    val liveStatus: String

    if(liveEndTime == "false") {
        //配信終了時間(liveEndTime)が"false"時は"配信中"or"配信予約でまだ始まっていない"or"投稿された動画"
        liveStatus = when (liveBroadcastContent) {
            "none" -> {
                //liveEndTimeが"false"and liveBroadcastContentが"none"の時は動画
                "動画"
            }
            "live" -> {
                //liveEndTimeが"false"and liveBroadcastContentが"live"の時は配信中
                "配信中"
            }
            else -> {
                //上記以外の時は配信予約and 配信開始していない
                "配信予約"
            }
        }
    }else{
        //配信終了時間(liveEndTime)に"false"以外が入っているときは配信が終わっている
        liveStatus = "配信終了"
    }

    return  liveStatus
}