// 使い方
// 動画・配信の一覧を取得する時：getMoviesDoc() : ArrayList<Movie>
// 登録者の一覧を取得する時：getSubscribersDoc() : ArrayList<Subscriber>

package com.example.hinvas

import android.annotation.SuppressLint
import android.os.Build
import android.os.Parcelable
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

// ユーザー情報を登録・取得するときに用いるDataClass兼ドキュメント
data class User(
    @DocumentId
    val userId: String = "",
    val userName: String = "",
    val updatedTime: String = "",
)

// users/user/movies下のドキュメントを取得するときに用いるDataClass
// VideoとLiveの２つの要素を持つ
@Parcelize
data class Movie(
    @DocumentId
    val documentId: String = "", // ドキュメントID
    val videoId: String = "", // 動画ID
    val platform: String = "", // 動画プラットフォーム
    val liveBroadcastContent: String = "", // 予約配信、配信中、動画か判定
    val title: String = "", // 動画タイトル
    val thumbnail: String = "", // サムネのURL
    val description: String = "", // 動画説明
    val publishedAt: Timestamp? = null,  // 投稿日時. Date型から変換
    val subscriberId: String = "", // チャンネルID
    val subscriberName: String = "", // チャンネル名
    val subscriberIcon: String = "", // チャンネルアイコンのURL

    // liveのみ値を入力
    val liveStartTime: String = "", // 配信開始時間
    val scheduledStartTime: String = "", // 配信予定時間
    val liveEndTime: String = "" // 配信終了時間
) : Parcelable

// users/user/movies下に登録する動画に関するドキュメント
data class Video(
    @DocumentId
    val documentId: String = "", // ドキュメントID
    val videoId: String = "", // 動画ID
    val platform: String = "", // 動画プラットフォーム
    val liveBroadcastContent: String = "", // 予約配信、配信中、動画か判定
    val title: String = "", // 動画タイトル
    val thumbnail: String = "", // サムネのURL
    val description: String = "", // 動画説明
    val publishedAt: Timestamp? = null,  // 投稿日時. Date型から変換
    val subscriberId: String = "", // チャンネルID
    val subscriberName: String = "", // チャンネル名
    val subscriberIcon: String = "" // チャンネルアイコンのURL
)

// users/user/movies/に登録するLiveに関するドキュメント
data class Live(
    @DocumentId
    val documentId: String = "", // ドキュメントID
    val videoId: String = "", // 動画ID
    val platform: String = "", // 動画プラットフォーム
    val liveBroadcastContent: String = "", // 予約配信、配信中、動画か判定
    val title: String = "", // 動画タイトル
    val thumbnail: String = "", // サムネのURL
    val description: String = "", // 動画説明
    val publishedAt: Timestamp? = null, // 投稿日時. Date型から変換
    val liveStartTime: String = "", // 配信開始時間
    val scheduledStartTime: String = "", // 配信予定時間
    val liveEndTime: String = "", // 配信終了時間
    val subscriberId: String = "", // チャンネルID
    val subscriberName: String = "", // チャンネル名
    val subscriberIcon: String = "" // チャンネルアイコンのURL
)

// users/user/channels/に登録する登録者に関するドキュメント
data class Subscriber(
    val subscriberId: String = "", // チャンネルID
    val platform: String = "", // 所属プラットフォーム
    val subscriberName: String = "", // チャンネル名
    val subscriberIcon: String = "" // チャンネルアイコンのURL
)

// users/user/notificationsLists/に登録する通知リストに関するドキュメント
data class NotificationList(
    @DocumentId
    val documentId: String = "", // ドキュメントID
    val notificationsListName: String = "", // 通知リスト名
    val notificationsListIcon: String = "", // 通知リストのアイコンのURL
    val notificationsSum: Int = 0, // 通知リストに追加されている通知の個数
    val createdAt: Timestamp? = null, // 通知リストを作成した時間
)

// users/user/notificationsLists/notificationsList/notificationsに登録する通知リストに追加される通知に関するドキュメント
data class NotificationListItem(
    @DocumentId
    val documentId: String = "", // ドキュメントID
    val videoId: String = "", // 動画ID
    val title: String = "", // 通知名
    val platform: String = "", // 動画プラットフォーム
    val liveBroadcastContent: String = "", // 予約配信、配信中、動画か判定
    val thumbnail: String = "", // サムネのURL
    val publishedAt: Timestamp? = null,  // 投稿日時. Date型から変換
    val subscriberName: String = "", // クリエータ名
    val subscriberIcon: String = "", // チャンネルアイコンのURL

    val addAt : Timestamp? = null, // 通知リストに追加した時間
)

data class AccessToken(
    val token: String = "",
    val platform: String = "",
    val createdAt: Timestamp? = null
)

class FireStore {

    // FireStore関連
    private val db = FirebaseFirestore.getInstance()
    private var userDocId: String = "user1" // ユーザーid,ユーザー認証・ログイン関連の機能実装時に動的に設定するようにする

    // API関連
    private val youtubeApi = YoutubeApi()

    // コルーチン関連
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    // Youtubeの動画情報をデータベースに登録する
    @RequiresApi(Build.VERSION_CODES.O)
    fun addAccessToken(token: String, platform: String) {
        try {
            val accessToken = AccessToken(
                token = token,
                platform = platform,
                createdAt = Timestamp(Date()),
            )

            // データベース登録処理
            val data = scope.launch {
                db.collection("users").document(userDocId).collection("accessTokens").document(platform + "AccessToken")
                    .set(accessToken)
                    .await()
            }
            runBlocking {
                data.join()
            }

        } catch (e: Exception) {
            Log.e("addYoutubeVideo_Request", e.message!!)
        }
    }


    // 動画・配信情報を登録する
    fun addYoutubeMovie(videoId: String): Boolean {
        var movieFlag = "" // 動画か配信か判定するための変数

        try {
            val data = scope.launch {
                movieFlag = youtubeApi.checkLive(youtubeApi.getLiveStatus(videoId))
            }
            runBlocking {
                data.join()
            }
            when (movieFlag) {
                "動画です" -> {
                    addYoutubeVideo(videoId)
                    return true
                }
                "配信中です", "予約している配信です", "配信は終了しています" -> {
                    addYoutubeLive(videoId)
                    return true
                }
                "エラー" -> {
                    Log.d("addYoutubeMovie", "YoutubeApi().checkLive()で例外エラーが発生しました")
                    return false
                }
                else -> {
                    Log.d("addYoutubeMovie", "movieFlagに予期せぬ値が入力されていました")
                    return false
                }
            }
        } catch (e: Exception) {
            Log.e("addYoutubeMovie_Request", e.message!!)
            return false
        }
    }

    // Youtubeの動画情報をデータベースに登録する
    private fun addYoutubeVideo(videoId: String) {
        var videoData: YoutubeVideo? = null
        try {
            var data = scope.launch {
                videoData = youtubeVideoInputData(youtubeApi.getVideos(videoId))
            }
            runBlocking {
                data.join()
            }
            val video = Video(
                videoId = videoData!!.videoId,
                platform = "Youtube",
                liveBroadcastContent = videoData!!.liveBroadcastContent, // videoData!!.liveBroadcastContent,
                title = videoData!!.title,
                thumbnail = videoData!!.thumbnailUrl,
                description = videoData!!.description,
                publishedAt = stringToTimestamp(videoData!!.publishedAt),
                subscriberId = videoData!!.channelId,
                subscriberName = videoData!!.channelName,
                subscriberIcon = videoData!!.channelIconUrl
            )
            val movieDocId: String =
                video.platform + video.videoId // プラットフォーム名 + videoIdの形式でドキュメントIDを生成

            // データベース登録処理
            data = scope.launch {
                db.collection("users").document(userDocId).collection("movies").document(movieDocId)
                    .set(video)
                    .await()
            }
            runBlocking {
                data.join()
            }

        } catch (e: Exception) {
            Log.e("addYoutubeVideo_Request", e.message!!)
        }
    }

    // Youtubeの配信情報をYoutubeに登録する
    private fun addYoutubeLive(videoId: String) {
        var liveData: YoutubeLive? = null
        try {
            var data = scope.launch {
                liveData = youtubeLive(youtubeApi.getVideos(videoId))
            }
            runBlocking {
                data.join()
            }
            val live = Live(
                videoId = liveData!!.videoId,
                platform = "Youtube",
                liveBroadcastContent = liveData!!.liveBroadcastContent, //liveData!!.liveBroadcastContent,
                title = liveData!!.title,
                thumbnail = liveData!!.thumbnailUrl,
                description = liveData!!.description,
                publishedAt = stringToTimestamp(liveData!!.videoTime),
                liveStartTime = liveData!!.liveStartTime,
                scheduledStartTime = liveData!!.scheduledStartTime,
                liveEndTime = liveData!!.liveEndTime,
                subscriberId = liveData!!.channelId,
                subscriberName = liveData!!.channelTitle,
                subscriberIcon = liveData!!.channelIconUrl
            )
            val movieDocId: String =
                live.platform + live.videoId // プラットフォーム名 + videoIdの形式でドキュメントIDを生成

            data = scope.launch {
                db.collection("users").document(userDocId).collection("movies").document(movieDocId)
                    .set(live)
                    .await()
            }
            runBlocking {
                data.join()
            }

        } catch (e: Exception) {
            Log.e("addYoutubeLive_Request", e.message!!)
        }
    }

    @SuppressLint("LongLogTag")
    fun addYoutubeChannel(channelId: String): Boolean {
        var channelData: YoutubeChannel? = null
        try {
            var data = scope.launch {
                channelData = youtubeChannelInputData(youtubeApi.getChannels(channelId))
            }
            runBlocking {
                data.join()
            }
            val subscriber = Subscriber(
                subscriberId = channelData!!.channelId,
                platform = "Youtube",
                subscriberName = channelData!!.title,
                subscriberIcon = channelData!!.icon
            )

            if (subscriber == Subscriber(
                    subscriberId = "エラー",
                    platform = "Youtube",
                    subscriberName = "エラー",
                    subscriberIcon = "エラー"
                )
            ) {
                Log.e("addYoutubeChannel", "存在しないチャンネルIDが入力されました")
                return false
            } else {
                val channelDocId: String = subscriber.platform + subscriber.subscriberId

                // データベース登録処理
                data = scope.launch {
                    db.collection("users").document(userDocId).collection("subscribers")
                        .document(channelDocId)
                        .set(subscriber)
                        .await()
                }
                runBlocking {
                    data.join()
                }
                return true
            }
        } catch (e: Exception) {
            Log.e("addYoutubeChannel_Request", e.message!!)
            return false
        }
    }

    fun addNotificationList(notificationsListName: String) {
        val notificationList = NotificationList (
            notificationsListName = notificationsListName, // 通知リスト名
            notificationsListIcon = "", // 通知リストのアイコンのURL
            notificationsSum = 0, // 通知リストに追加されている通知の個数
            createdAt = Timestamp(Date()), // 通知リストを作成した時間
        )
        val data = scope.launch {
            db.collection("users").document(userDocId).collection("notificationLists")
                .add(notificationList)
                .await()
        }
        runBlocking {
            data.join()
        }
    }

    fun addNotificationListItem(movie : Movie, notificationListDocId : String){
        val notificationListItem = NotificationListItem(
            videoId = movie.videoId, // 動画ID
            title = movie.title, // 通知名
            platform = movie.platform, // 動画プラットフォーム
            liveBroadcastContent = movie.liveBroadcastContent, // 予約配信、配信中、動画か判定
            thumbnail = movie.thumbnail, // サムネのURL
            publishedAt = movie.publishedAt,  // 投稿日時. Date型から変換
            subscriberName = movie.subscriberName, // クリエータ名
            subscriberIcon = movie.subscriberIcon, // チャンネルアイコンのURL
            addAt = Timestamp(Date()) // 通知リストに追加した時間
        )

        val data = scope.launch {
            db.collection("users").document(userDocId).collection("notificationLists").document(notificationListDocId)
                .collection("notificationListItems").document(notificationListItem.platform + notificationListItem.videoId)
                .set(notificationListItem)
        }
        runBlocking {
            data.join()
        }
    }

    // 裏画面処理で更新された時間を登録する
    @RequiresApi(Build.VERSION_CODES.O)
    fun addUpdatedTime() {
        try {
            val user = User(
                updatedTime = LocalDateTime.now().toString().split(".")[0]
            )
            val data = scope.launch {
                db.collection("users").document(userDocId).set(user)
            }
            runBlocking {
                data.join()
            }
        } catch (e: Exception) {
            Log.e("addUpdatedTime_Request", e.message!!)
        }
    }

    // DBに保存されている自身のuserドキュメントを取得する
    private fun getMyUserDoc(): User {
        var userData = User()
        val data = scope.launch {
            userData = db.collection("users").document(userDocId).get().await()
                .toObject(User::class.java)!!
        }
        runBlocking {
            data.join()
        }
        return userData
    }

    // DBに保存されている登録者をすべて取得する
    // 戻り値：ArrayList<Subscriber>
    fun getAccessTokensDoc(): ArrayList<AccessToken> {
        var accessTokensList = ArrayList<AccessToken>()
        val data = scope.launch {
            val querySnapshot = db.collection("users").document(userDocId).collection("accessTokens")
                .get().await()
            accessTokensList = ArrayList(querySnapshot.toObjects<AccessToken>())
        }
        runBlocking {
            data.join()
        }
        return accessTokensList
    }

    // DBに保存されている動画・配信をすべて取得する
    // 戻り値：ArrayList<Movie>
    fun getMoviesDoc(): ArrayList<Movie> {
        var moviesList = ArrayList<Movie>()
        val data = scope.launch {
            val querySnapshot = db.collection("users").document(userDocId).collection("movies")
                .orderBy("publishedAt", Query.Direction.DESCENDING)
                .get().await()
            moviesList = ArrayList(querySnapshot.toObjects<Movie>())
            for (movie in moviesList) {
                Log.d("投稿日時", timestampToString(movie.publishedAt!!))
            }
        }
        runBlocking {
            data.join()
        }
        return moviesList
    }

    // DBに保存されている登録者をすべて取得する
    // 戻り値：ArrayList<Subscriber>
    fun getSubscribersDoc(): ArrayList<Subscriber> {
        var subscribersList = ArrayList<Subscriber>()
        val data = scope.launch {
            val querySnapshot = db.collection("users").document(userDocId).collection("subscribers")
                .get().await()
            subscribersList = ArrayList(querySnapshot.toObjects<Subscriber>())
        }
        runBlocking {
            data.join()
        }
        return subscribersList
    }

    // DBに保存されている通知リストをすべて取得する
    fun getNotificationListsDoc() : ArrayList<NotificationList> {
        var notificationList = ArrayList<NotificationList>()
        val data = scope.launch {
            val querySnapshot = db.collection("users").document(userDocId).collection("notificationLists")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get().await()
            notificationList = ArrayList(querySnapshot.toObjects<NotificationList>())
        }
        runBlocking {
            data.join()
        }
        return notificationList
    }

    // DBに保存されている特定の通知リストの通知をすべて取得する
    fun getNotificationListItemsDoc(notificationListDocId : String) : ArrayList<NotificationListItem> {
        var notificationListItemsList = ArrayList<NotificationListItem>()
        val data = scope.launch {
            val querySnapshot = db.collection("users").document(userDocId).collection("notificationLists")
                .document(notificationListDocId).collection("notificationListItems")
                .orderBy("addAt", Query.Direction.ASCENDING)
                .get().await()
            notificationListItemsList = ArrayList(querySnapshot.toObjects<NotificationListItem>())
        }
        runBlocking {
            data.join()
        }
        return notificationListItemsList
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getUpdatedMovies() : Boolean {
        // 最終更新時間を取得
        val updatedTime = getMyUserDoc().updatedTime // 最終更新時間を取得
        var checkUpdater = false // DBへの登録処理が実行されたらtureを返す

        Log.d("worker", updatedTime)
        // 登録者の新規で投稿された動画・配信を検知し、DBに登録
        for (subscriber in getSubscribersDoc()) {
            for (getVideoId in getYoutubeVideoIDList(
                youtubeApi.getChannelVideosSearch(
                    subscriber.subscriberId,
                    timeConverter(updatedTime)
                )
            )) {
                addYoutubeMovie(getVideoId)
                checkUpdater = true
            }
        }

        // 更新時間を現在の時刻に設定
        addUpdatedTime()

        return checkUpdater
    }

    fun updateCntUpNotificationsSum(documentId: String) {
        try {
            db.collection("users").document(userDocId).collection("notificationLists").document(documentId)
                .update("notificationsSum", FieldValue.increment(1))
        } catch (e:Exception) {
            Log.e("addUpdatedTime_Request", e.message!!)
        }
    }
    fun updateNotificationListIcon(documentId: String, iconUrl: String){
        try {
            db.collection("users").document(userDocId).collection("notificationLists").document(documentId)
                .update("notificationsListIcon", iconUrl)
        } catch (e:Exception) {
            Log.e("addUpdatedTime_Request", e.message!!)
        }
    }

    // String型の日時をTimestamp型に変換
    @SuppressLint("SimpleDateFormat")
    fun stringToTimestamp(strDate: String): Timestamp? {
        Log.d("date",strDate)
        val sdFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val date = sdFormat.parse(strDate)
        return date?.let { Timestamp(it) }
    }

    @SuppressLint("SimpleDateFormat")
    fun timestampToString(timestamp: Timestamp): String {
        val date = timestamp.toDate()
        val dateFormat = SimpleDateFormat("yyyy年MM月dd日 HH:mm")
        return dateFormat.format(date)
    }
}