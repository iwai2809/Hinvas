package com.example.hinvas

import android.annotation.SuppressLint
import android.util.Log
import org.json.JSONObject

data class TwitchChannelId(
    val ChannelId:String        //チャンネルID
)

data class TwitchChannelData(
    val ChannelTitle:String,
    val ChannelIcon:String,
    val ChannelId:String
)

@SuppressLint("LongLogTag")
fun getTwitchFollowChannelId(jsonStr: String): ArrayList<TwitchChannelId>{
    return try{
        val result: ArrayList<TwitchChannelId> = ArrayList()
        val json = JSONObject(jsonStr)
        val channelIds = json.getJSONArray("data")
        for(i in 0 until channelIds.length()){
            val followChannelIds = TwitchChannelId(
                channelIds.getJSONObject(i).getString("to_id")
            )
            result.add(followChannelIds)
        }
        result
    }catch (e:Exception){
        Log.d("getTwitchFollowChannelId", e.message!!)
        return ArrayList()
    }
}

fun getChannelData(jsonStr:String): ArrayList<TwitchChannelData>{
    return try{
        val result: ArrayList<TwitchChannelData> = ArrayList()
        val json = JSONObject(jsonStr)
        val channelIds = json.getJSONArray("data")
        for(i in 0 until channelIds.length()){
            val followChannelData = TwitchChannelData(
                channelIds.getJSONObject(i).getString("display_name"),
                channelIds.getJSONObject(i).getString("profile_image_url"),
                channelIds.getJSONObject(i).getString("id")
            )
            result.add(followChannelData)
        }

        result
    }catch (e:Exception){
        Log.d("配信者データ", e.message!!)
        return ArrayList()
    }
}