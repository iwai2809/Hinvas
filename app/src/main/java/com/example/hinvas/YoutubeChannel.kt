package com.example.hinvas

import org.json.JSONObject

data class YoutubeChannel(
    val title : String,           //チャンネル名
    val icon : String,            //icon画像
    val channelId : String,         //チャンネルID
)

fun youtubeChannelInputData(channelJson: String) : YoutubeChannel{
    try {
        //channelJsonの分解
        val cjson = JSONObject(channelJson)
        val cItems = cjson.getJSONArray("items")
        val cItemList = cItems.getJSONObject(0)
        val cSnippetObj = cItemList.getJSONObject("snippet")
        val thumbnailObj = cSnippetObj.getJSONObject("thumbnails")
        val thumbnailUrl = thumbnailObj.getJSONObject("medium")

        return YoutubeChannel(
            cSnippetObj.getString("title"),             //チャンネル名
            thumbnailUrl.getString("url"),              //icon画像
            cItemList.getString("id"),                  //チャンネルID
        )
    } catch (e: Exception){
        return YoutubeChannel(
            "エラー",
            "エラー",
            "エラー"
        )
    }
}
