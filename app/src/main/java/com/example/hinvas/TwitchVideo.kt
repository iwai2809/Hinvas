package com.example.hinvas

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import org.json.JSONArray
import org.json.JSONObject

data class TwitchVideo(
    val videoId: String,        // 動画のID
    val title: String,          // 動画タイトル
    val thumbnailUrl: String,   // サムネイルのURL
    val channelIconUrl: String, // チャンネルアイコンのURL
    val publishedAt: String,    // 投稿日時
    val channelId: String,      // チャンネルID
    val channelName: String,    // チャンネル名
    val liveBroadcastContent: String    // 予約配信、配信中、動画か判定
)


@RequiresApi(Build.VERSION_CODES.O)
fun getTwitchVideoIDList(videoJsonStr: String, channelJsonStr: String, publishTime: String): ArrayList<TwitchVideo>{
    return try{
        val result: ArrayList<TwitchVideo> = ArrayList()
        val videos: JSONArray = JSONObject(videoJsonStr).getJSONArray("data")
        val channel: JSONArray = JSONObject(channelJsonStr).getJSONArray("data")
        for (i in 0 until videos.length()) {
            // 指定した時間以降の動画かを判定
            if(timeConverter(publishTime) > videos.getJSONObject(i).getString("published_at")) break

            val url = videos.getJSONObject(i).getString("thumbnail_url").replace("%{width}","640").replace("%{height}", "400")

            val video = TwitchVideo(
                videos.getJSONObject(i).getString("id"),
                videos.getJSONObject(i).getString("title"),
                url,
                channel.getJSONObject(0).getString("profile_image_url"),
                videos.getJSONObject(i).getString("published_at"),
                videos.getJSONObject(i).getString("user_id"),
                videos.getJSONObject(i).getString("user_name"),
                videos.getJSONObject(i).getString("type"),
            )

            result.add(video)
            Log.d("getTwitchVideoIDList", "${video.title} + ${video.publishedAt} + ${video.channelIconUrl}")
        }
        result
    } catch (e: Exception){
        Log.d("getTwitchVideoIDList", "error")
        return ArrayList()
    }
}