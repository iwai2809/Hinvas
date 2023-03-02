package com.example.hinvas

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.hinvas.databinding.FragmentFirestoreTestBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.util.*

class FireStoreTestFragment : Fragment(){

    private var _binding : FragmentFirestoreTestBinding? = null
    private val binding get() = _binding!!
    private val fireStore = FireStore()
    private val notificationListName = "test"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentFirestoreTestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addSubscriberBtn.setOnClickListener {
            try {
                addDataResultText(fireStore.addYoutubeChannel(binding.addSubscriberIdText.text.toString()))
                binding.addSubscriberIdText.text.clear()
            } catch (e: Exception) {
                binding.addDataResultText.text = "ボタン入力でエラーが発生しました"
                binding.addSubscriberIdText.text.clear()
                Log.e("Request", e.message!!)
            }
        }

        binding.addMovieBtn.setOnClickListener {
            try {
                addDataResultText(fireStore.addYoutubeMovie(binding.addVideoIdText.text.toString()))
                binding.addVideoIdText.text.clear()
            } catch (e : Exception) {
                binding.addDataResultText.text = "ボタン入力でエラーが発生しました"
                binding.addVideoIdText.text.clear()
                Log.d("Request", e.message!!)
            }
        }

        binding.addNotificationListBtn.setOnClickListener {
            try {
                fireStore.addNotificationList("test")
                binding.addDataResultText.text = "正常に追加されました"
            } catch (e : Exception) {
                binding.addDataResultText.text = "ボタン入力でエラーが発生しました"
                Log.d("Request", e.message!!)
            }
        }

        binding.addNotificationListItemBtn.setOnClickListener {
            try {
                fireStore.addNotificationListItem(
                    Movie(
                            videoId = "hvSvscMoPhc", // 動画ID
                            platform = "Youtube", // 動画プラットフォーム
                            liveBroadcastContent = "動画", // 予約配信、配信中、動画か判定
                            title = "現地サポーター「信じられない」快挙に各国から祝福(2022年11月24日)", // 動画タイトル
                            thumbnail = "https://i.ytimg.com/vi/hvSvscMoPhc/default.jpg", // サムネのURL
                            description = "", // 動画説明
                            publishedAt = Timestamp(Date()),  // 投稿日時. Date型から変換
                            subscriberId = "UCGCZAYq5Xxojl_tSXcVJhiQ", // チャンネルID
                            subscriberName = "ANNnewsCH", // チャンネル名
                            subscriberIcon = "https://yt3.ggpht.com/ytc/AMLnZu-FgJXrRUz4xN3cqdifxDMOfJE-lnbClvHsKigopQ=s240-c-k-c0x00ffffff-no-rj", // チャンネルアイコンのURL
                    ), notificationListName
                )
                binding.addDataResultText.text = "正常に追加されました"
            } catch (e: Exception) {
                binding.addDataResultText.text = "ボタン入力でエラーが発生しました"
                Log.d("Request", e.message!!)
            }
        }

        binding.getMovieBtn.setOnClickListener {
            try {
                val showList = fireStore.getMoviesDoc()
                binding.getDataResultText.text = showList.toString()
            } catch (e: Exception) {
                binding.getDataResultText.text = "ボタン入力でエラーが発生しました"
                Log.d("Request", e.message!!)
            }
        }

        binding.getSubscriberBtn.setOnClickListener {
            try {
                val showList = fireStore.getSubscribersDoc()
                binding.getDataResultText.text = showList.toString()
            } catch (e: Exception) {
                binding.getDataResultText.text = "ボタン入力でエラーが発生しました"
                Log.d("Request", e.message!!)
            }
        }

        binding.getNotificationListBtn.setOnClickListener {
            try {
                val showList = fireStore.getNotificationListsDoc()
                binding.getDataResultText.text = showList.toString()
            } catch (e: Exception) {
                binding.getDataResultText.text = "ボタン入力でエラーが発生しました"
                Log.d("Request", e.message!!)
            }
        }

        binding.getNotificationListItemBtn.setOnClickListener {
            try {
                val showList = fireStore.getNotificationListItemsDoc(notificationListName)
                binding.getDataResultText.text = showList.toString()
            } catch (e: Exception) {
                binding.getDataResultText.text = "ボタン入力でエラーが発生しました"
                Log.d("Request", e.message!!)
            }
        }

        binding.toFragmentSettingBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun addDataResultText(boolean: Boolean) {
        if (boolean) {
            binding.addDataResultText.text = "正常に追加されました"
        } else {
            binding.addDataResultText.text = "エラーが発生しました"
        }
    }
}