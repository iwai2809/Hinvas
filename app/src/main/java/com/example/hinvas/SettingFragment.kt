package com.example.hinvas

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View

import kotlinx.coroutines.*
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.hinvas.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private val mainHandler = Handler(Looper.getMainLooper())

    private var twitch: TwitchApi = TwitchApi()
    private val nicoVideo: NicoVideoApi =
        NicoVideoApi("%E5%88%9D%E9%9F%B3%E3%83%9F%E3%82%AF", "title", "-viewCounter")
    private val youtube = YoutubeApi()

    private val fireStore = FireStore()

    private val scope = CoroutineScope(Dispatchers.Default + Job())

    private var twitchToken = ""
    private var result: String = ""

    private fun openBrowser() {
        val uri: Uri = Uri.parse(String.format(twitch.oauthURL))
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    override fun onResume() {
        //openBrowser後の処理
        super.onResume()
        for (token in fireStore.getAccessTokensDoc()) {
            //TwitchのTokenがあったらbreak
            if (token.platform == "Twitch") {
                twitchToken = token.token
                break
            }
        }
        if (twitchToken == "") openBrowser()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentSettingBinding.inflate(inflater, container, false)

        nicoVideo.setParams("fields", "contentId,title,viewCounter")
        nicoVideo.setParamsOffset(0, 3)
        nicoVideo.setParamsContext("apiguide")

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            // onSaveInstateで保存されたデータを復元
            result = savedInstanceState.getString("result").toString()
            mainHandler.post { binding.resultTxt.text = result }
        }

        binding.twitchAPITestBtn.setOnClickListener {
            val data = scope.launch {
                //TwitchApiインスタンスにTokenをセット
                twitch.setToken(twitchToken)

                //チャンネルID取得
                val twitchFollowedUsers : ArrayList<TwitchChannelId> = getTwitchFollowChannelId(twitch.getChannelId())
                //フォローしているuserのuserIdをログで確認
                for(i in 0 until twitchFollowedUsers.size){
                    Log.d("Twitch_ユーザーフォローチャンネル","twitchFollwedUsers[$i]=${twitchFollowedUsers[i].ChannelId}")
                    Log.d("Twitch_チャンネルデータ", getChannelData(twitch.channelData(twitchFollowedUsers[i].ChannelId.toInt())).toString())
                    Log.d("Twitch_アーカイブデータ", getTwitchVideoIDList(twitch.getChannelVideosSearch(twitchFollowedUsers[i].ChannelId.toInt()), twitch.channelData(twitchFollowedUsers[i].ChannelId.toInt()), "2022-12-05T11:20:37").toString())
                }

                // ライブデータは、アイコンのURLを取得するために現在配信しているチャンネルidの一覧を取得する必要がある
                val liveUsers : ArrayList<Int> = getTwitchLiveChannelId(twitch.twitchLiveData())
                //TwitchLiveDataでフォローした配信中のデータ一覧を配列で取得している
                //特定の配信データは.toString()の前に添え字をいれて、配列要素の指定ができます
                for(i in 0 until liveUsers.size){
                    Log.d("Twitch_ライブデータ", twitchLiveDataJson(twitch.twitchLiveData(), twitch.channelData(liveUsers[i])).toString())
                }

                val twitchVideos: ArrayList<TwitchVideo> = getTwitchVideoIDList(twitch.getChannelVideosSearch(49207184), twitch.channelData(49207184), "2022-12-05T11:20:37")
                result = twitchVideos[0].title
            }
            runBlocking {
                data.join()
            }
            try {
                mainHandler.post { binding.resultTxt.text = result }
            } catch (e: Exception) {
                Log.e("Request", e.message!!)
            }
        }

        binding.nicoAPITestBtn.setOnClickListener {
            //httpリクエスト
            val data = scope.launch {
                result = nicoVideo.getApiData()
            }
            mainHandler.post { binding.resultTxt.text = "待機中" }
            runBlocking {
                data.join()
            }
            try {
                mainHandler.post { binding.resultTxt.text = result }
            } catch (e: Exception) {
                Log.e("Request", e.message!!)
            }
        }

        binding.youtubeAPITestBtn.setOnClickListener {
            //httpリクエスト
            try {
                val data = scope.launch {

//                    val liveVideo = youtubeLive(youtube.getYoutubeLive("UCIzWKGAxX6pv3PAIVD7mIlA"))
//                    Log.d("dataclass", "$liveVideo")

//                    val channel = youtubeChannelInputData(youtube.getChannels("UCIzWKGAxX6pv3PAIVD7mIlA"))
//                    Log.d("dataclass", "$channel")

                    val video = youtubeVideoInputData(youtube.getVideos("OIycHcCMQ-g"))
                    Log.d("dataclass", "$video")

                    // チャンネルの指定した時間移行のコンテンツのvideoIDをリストにして返す
                    val timeVideoIDs = getYoutubeVideoIDList(
                        youtube.getChannelVideosSearch(
                            "UCIzWKGAxX6pv3PAIVD7mIlA",
                            timeConverter("2020-01-01T09:00:00")
                        )
                    )
                    Log.d("videoId", "$timeVideoIDs")

                    result = video.title
                }
                mainHandler.post { binding.resultTxt.text = "待機中" }
                runBlocking {
                    data.join()
                }
                mainHandler.post { binding.resultTxt.text = result }
            } catch (e: Exception) {
                Log.e("Request", e.message!!)
            }
        }

        //カスタムURL確認用ボタンです
        binding.customurlbtn.setOnClickListener {
            try {
                val data = scope.launch {
                    val url =
                        youtube.customUrlConverter(customUrl = "https://www.youtube.com/c/NintendoJPofficial")
                    result = url
                }
                mainHandler.post { binding.resultTxt.text = "待機中" }
                runBlocking {
                    data.join()
                }
                mainHandler.post { binding.resultTxt.text = result }
            } catch (e: Exception) {
                Log.e("Request", e.message!!)
            }
        }

        //正規表現チェック用
        binding.checkReguxBtn.setOnClickListener {
            val regex = Regex("^(http(s)?://(www.)?)?youtube.com/channel/[-._0-9a-zA-Z]{3,30}$")
//            val customRegex = Regex("^(http(s)?://www.)?youtube.com/(@[-._0-9a-zA-Z]* | c/[-._0-9a-zA-Z]* | user/[-._0-9a-zA-Z]*)")
            val customRegex =
                Regex("^(http(s)?://(www.)?)?youtube.com/(@|c/|user/)[-._0-9a-zA-Z]{3,30}\$")
            val checkList = listOf(
                // 普通のチャンネルURL
                "https://www.youtube.com/channel/UCm2cVxhKS9eFBKL9iiCP-6A", // 普通のチャンネルID：【https://www.youtube.com/channel/】
                "http://www.youtube.com/channel/UCm2cVxhKS9eFBKL9iiCP-6A", // 普通のチャンネルID：https→http
                "ahttp://www.youtube.com/channel/UCm2cVxhKS9eFBKL9iiCP-6A", // 普通のチャンネルID：https→http
                "youtube.com/channel/UCm2cVxhKS9eFBKL9iiCP-6A", // 普通のチャンネルID：youtube,comから始まる
                "ayoutube.com/channel/UCm2cVxhKS9eFBKL9iiCP-6A", // 普通のチャンネルID：youtube,comから始まる false

                "https://www.youtube.com/channel/aa", // 文字数チェック：2文字
                "https://www.youtube.com/channel/aaa", // 文字数チェック：3文字
                "https://www.youtube.com/channel/123456789012345678901234567890", // 文字数チェック：30文字
                "https://www.youtube.com/channel/1234567890123456789012345678901", // 文字数チェック：31文字

                // カスタムURL
                "https://www.youtube.com/@TokaiOnAir", // ハンドルURL：【https://www.youtube.com/@】
                "http://www.youtube.com/@TokaiOnAir", // ハンドルURL：https→http
                "youtube.com/@TokaiOnAir", // ハンドルURL:youtube,comから始まる
                "ayoutube.com/@TokaiOnAir", // ハンドルURL:youtube,comから始まる
                "https://www.youtube.com/c/FromSoftwareInc", // カスタムURL：【https://www.youtube.com/c/】
                "https://www.youtube.com/user/HikakinGames", // 以前のユーザー名のURL：【https://www.youtube.com/user/】
                "https://www.youtube.com/watch?v=ss9S3M_emWY&t=1s", // false
                "あ" // false
            )
            Log.d("正規表現チェック", "デフォルトのチャンネルURLの場合")
            Log.d("正規表現チェック", "----------------------------------------------")
            for (checkItem in checkList) {
                if (checkItem.matches(regex)) {
                    Log.d("正規表現チェック", "$checkItem：【true】です")
                } else {
                    Log.d("正規表現チェック", "$checkItem：【false】です")
                }
            }
            Log.d("正規表現チェック", "----------------------------------------------")
            Log.d("正規表現チェック", "カスタムチャンネルURLの場合")
            Log.d("正規表現チェック", "----------------------------------------------")
            for (checkItem in checkList) {
                if (checkItem.matches(customRegex)) {
                    Log.d("正規表現チェック", "$checkItem：【true】です")
                } else {
                    Log.d("正規表現チェック", "$checkItem：【false】です")
                }
            }
        }

        binding.toFireStoreTestBtn.setOnClickListener {
            val fireStoreTestFragment = FireStoreTestFragment()
            parentFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, fireStoreTestFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}