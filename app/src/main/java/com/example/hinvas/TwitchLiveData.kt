package com.example.hinvas

import org.json.JSONObject

data class TwitchLive(
    val channelName: String,       //チャンネルの名前
    val channelID: String,        //チャンネルのID
    val liveTitle: String,        //配信タイトル
    val thumbnailUrl: String,     //サムネイルのURL
    val channelIcon: String,      //チャンネルのアイコン
    val gameTitle: String         //ゲームタイトル
)

fun getTwitchLiveChannelId(json: JSONObject): ArrayList<Int> {
    val result : ArrayList<Int> = ArrayList()

    return try {
        val data = json.getJSONArray("data")

        if (data.toString() != "[]") {
            for (i in 0 until data.length()) {
                result.add(data.getJSONObject(i).getString("user_id").toInt())
            }
        }
        result
    } catch (e : Exception) {
        result.add(-1)
        result
    }
}

fun twitchLiveDataJson(json: JSONObject, channel: String): ArrayList<TwitchLive> {
    val result: ArrayList<TwitchLive> = ArrayList()

    try {
        val data = json.getJSONArray("data")
        val channelJson = JSONObject(channel)
        val channelData = channelJson.getJSONArray("data")
        //だれも配信していない場合空なので、空じゃない場合に処理を行う
        if (data.toString() != "[]") {
            for (i in 0 until data.length()) {

                val url = data.getJSONObject(i).getString("thumbnail_url").replace("{width}","640").replace("{height}", "400")
                result.add(
                    TwitchLive(
                        data.getJSONObject(i).getString("user_name"),
                        data.getJSONObject(i).getString("user_id"),
                        data.getJSONObject(i).getString("title"),
                        url,
                        channelData.getJSONObject(i).getString("profile_image_url"),
                        data.getJSONObject(i).getString("game_name")
                    )
                )
            }
        }
        return result

    } catch (e: Exception) {

        result.add(
            TwitchLive(
                "エラー",
                "エラー",
                "エラー",
                "エラー",
                "エラー",
                "エラー"
            )
        )

        return result
    }
}
