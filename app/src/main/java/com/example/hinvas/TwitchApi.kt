package com.example.hinvas

import android.util.Log
import org.json.JSONObject

class TwitchApi {

    // チャンネル情報取得のURL
    private val endPointURL: String = "https://api.twitch.tv/helix/"

    private var accessToken: String? = null
    private var userId: String? = null
    private val clientID: String = "8g5n88dd7rmqpkv6vqrc86ow7yg8kp"

    val oauthURL =
        "https://id.twitch.tv/oauth2/authorize?response_type=token&client_id=$clientID&redirect_uri=https://hinvas.com/callback&scope=channel:manage:videos+user:read:follows"

    // アクセストークンを保存
    fun setToken(token: String): Boolean {
        return try{
            val inspectionUrl = "https://id.twitch.tv/oauth2/validate"
            Log.d("TwitchAPI", JSONObject(HttpRequest().getRequest(inspectionUrl, mapOf("Authorization" to "OAuth $token"))).getString("user_id"))
            accessToken = token
            //TwitchApi()のtokenInspectionの関数を実行してuserIDを取得している
            userId = tokenInspection()
            true
        } catch (e: java.lang.Exception) {
            Log.e("Twitch.setToken", e.message!!)
            false
        }
    }

    // チャンネルの動画リストを取得（デフォルト２０件、指定可能：最大１００件）
    fun getChannelVideosSearch(channelId: Int): String{
        val url = endPointURL + "videos?user_id=$channelId&type=archive "
        return try {
            val headers : Map<String, String> = mapOf("Authorization" to "Bearer $accessToken", "Client-Id" to clientID)
            JSONObject(HttpRequest().getRequest(url, headers)).toString()
        } catch (e: java.lang.Exception) {
            Log.e("Twitch.getChannelVideos", e.message!!)
            "取得失敗"
        }
    }

    //フォローしているチャンネルIDを取得
    fun getChannelId():String{
        val url = endPointURL+"users/follows?from_id=$userId"
        return try {
            val headers : Map<String, String> = mapOf("Authorization" to "Bearer $accessToken", "Client-Id" to clientID)
            HttpRequest().getRequest(url, headers)
        }catch (e:Exception){
            Log.e("Twitch.getChannelId",e.message!!)
            "取得失敗"
        }
    }

    // 認証したユーザーのユーザーIDを取得
    private fun tokenInspection():String{
        return try{
            val inspectionUrl = "https://id.twitch.tv/oauth2/validate"
            JSONObject(HttpRequest().getRequest(inspectionUrl, mapOf("Authorization" to "OAuth $accessToken"))).getString("user_id")

        } catch (e: java.lang.Exception) {
            Log.e("Twitch.tokenInspection", e.message!!)
            "エラー"
        }
    }

    fun twitchLiveData(): JSONObject {
        val userFollowsUrl = "https://api.twitch.tv/helix/streams/followed?user_id=$userId"
        val headers: Map<String, String> =
            mapOf("Authorization" to "Bearer $accessToken", "Client-Id" to clientID)

        return JSONObject(HttpRequest().getRequest(userFollowsUrl, headers))
    }

    fun channelData(channelID:Int): String {
        return try{
            val channelDataUrl = "https://api.twitch.tv/helix/users?id=$channelID"
            val headers: Map<String, String> =
                mapOf("Authorization" to "Bearer $accessToken", "Client-Id" to clientID)
            JSONObject(HttpRequest().getRequest(channelDataUrl, headers)).toString()
        } catch (e: java.lang.Exception) {
            Log.e("channelData", e.message!!)
            "エラー"
        }
    }
}