package com.example.hinvas

import android.annotation.SuppressLint
import android.app.ActionBar
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import java.net.URL

class FavoriteNoticeListItemActivity : AppCompatActivity() {

    private val dataList = ArrayList<MovieData>() // 通知を表示する際に使用する配列
    private val linkList = ArrayList<String>() // 外部リンクを格納する配列

    private lateinit var listView: ListView // listview関連のやつ
    private lateinit var emptyView : TextView

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    lateinit var icon: Bitmap
    private lateinit var thumbnail: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_notice_list_item)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        val fragment = Fragment()
//        val fragmentTransaction = supportFragmentManager.beginTransaction()
//        fragmentTransaction.add(R.layout.fragment_favorite, fragment)
//        fragmentTransaction.commit()

            listView = findViewById(R.id.list_view)
        emptyView = findViewById(R.id.empty_view)

        findViewById<ImageView>(R.id.list_name_change_btn).setOnClickListener {
        }

        setListData()// 初期データをセット
        setView()
        // dataが0件の場合emptyを表示
        if (dataList.isEmpty()) {
            // 表示・非表示設定 それぞれに対応したSwipeRefreshLayoutを表示・非表示にする
            listView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            setListViewAdapter()
        }
    }

    override fun onOptionsItemSelected(item:MenuItem): Boolean {
        var returnVal = true

        if (item.itemId == android.R.id.home) {
            finish()
        } else {
            returnVal = super.onOptionsItemSelected(item)
        }
        return returnVal
    }

    private fun setListData(){
        // データリストを初期化
        dataList.clear()

        for (movie in FireStore().getNotificationListItemsDoc(intent.getStringExtra("documentId").toString())) {

            var notificationBackgroundColor: Int
            var movieStyleIcon: Int
            var platformIcon: Int


            // 動画か配信かで表示する際の色を変更する
            when (movie.liveBroadcastContent) {
                "動画" -> {
                    notificationBackgroundColor = R.drawable.border_style_video
                    movieStyleIcon = R.drawable.ic_video
                }
                "配信予約", "配信中", "配信終了" -> {
                    notificationBackgroundColor = R.drawable.border_style_live
                    movieStyleIcon = R.drawable.ic_live
                }
                else -> {
                    Log.e("GetLiveBroadcastContent", "liveBroadcastContentに予期せぬ値が入力されています")
                    notificationBackgroundColor = R.drawable.border_style_error
                    movieStyleIcon = R.drawable.border_style_error
                }
            }

            val imageDownload = scope.launch {
                icon = try {
                    URL(movie.subscriberIcon).openStream().use {
                        BitmapFactory.decodeStream(it)
                    }
                } catch (e: Exception) {
                    BitmapFactory.decodeResource(resources, R.drawable.error_image)
                }
                thumbnail = try {
                    URL(movie.thumbnail).openStream().use {
                        BitmapFactory.decodeStream(it)
                    }
                } catch (e: Exception) {
                    BitmapFactory.decodeResource(resources, R.drawable.error_image)
                }
            }
            runBlocking {
                imageDownload.join()
            }

            // プラットフォームの半別
            // リンクと表示するプラットフォームのアイコンの画像を設定する
            if (movie.platform == "Youtube") {
                platformIcon = R.drawable.ic_youtube
                linkList.add("https://m.youtube.com/watch?v=" + movie.videoId)
            } else {
                Log.e("movie.platform", "予期せぬプラットフォームが入力されています")
                platformIcon = R.drawable.ic_error
                linkList.add("https://console.firebase.google.com/")
            }


            val data = MovieData(
                title = movie.title,
                subscriberName = movie.subscriberName,
                thumbnail = thumbnail,
                subscriberIcon = icon,
                notificationBackgroundColor = notificationBackgroundColor,
                platformIcon = platformIcon,
                movieStyleIcon = movieStyleIcon
            )

            dataList.add(data)
        }
    }

    private fun setListViewAdapter() {
        val moviesListAdapter = CustomAdapter(this, dataList)
        listView.adapter = moviesListAdapter

        // リストをクリックしたときの処理を実装
        // リンクにアクセス（動画先）
        listView.setOnItemClickListener { _, _, position, _ ->
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(linkList[position])))
        }
    }

    private class CustomAdapter(context: Context, private var dataList: ArrayList<MovieData>) :
        ArrayAdapter<MovieData>(context, 0, dataList) {
        private val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            // dataの取得
            val data = dataList[position]

            // レイアウトの設定
            var view = convertView
            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.home_list_item, parent, false)
            }

            view?.findViewById<TextView>(R.id.subscriber_name)?.text = data.subscriberName
            view?.findViewById<TextView>(R.id.movie_title)?.text = data.title
            view?.findViewById<ImageView>(R.id.thumbnail)
                ?.setImageBitmap(data.thumbnail)
            view?.findViewById<ImageView>(R.id.subscriber_icon)
                ?.setImageBitmap(data.subscriberIcon)
            view?.findViewById<LinearLayout>(R.id.notification_item)
                ?.setBackgroundResource(data.notificationBackgroundColor)
            view?.findViewById<ImageView>(R.id.platform_icon)
                ?.setImageResource(data.platformIcon)
            view?.findViewById<ImageView>(R.id.movie_style)
                ?.setImageResource(data.movieStyleIcon)

            return view!!
        }
    }

    @SuppressLint("CutPasteId", "SetTextI18n")
    private fun setView() {
        setNavigationBarViewHeight(0) // ナビゲーションバーのheightを0に再設定

        findViewById<TextView>(R.id.notice_list_name).text = intent.getStringExtra("notificationsListName")
        findViewById<TextView>(R.id.notice_item_sum).text = "${dataList.size}本の動画"
        // 画面が切り替わるときに生成
        setWholeViewHeight(
            wholeViewHeight = findViewById<LinearLayout>(R.id.view_size).height,
            mainViewLayout = findViewById<LinearLayout>(R.id.view_size)
        )

        // addBtn,scrollView,listView,emptyViewの高さを再設定
        val headerHeight = findViewById<LinearLayout>(R.id.view_size).layoutParams.height * 3 / 20
        val scrollViewHeight = findViewById<LinearLayout>(R.id.view_size).layoutParams.height * 17 / 20

        val headerLp = findViewById<LinearLayout>(R.id.header).layoutParams
        val scrollViewLp = findViewById<ScrollView>(R.id.scroll_view).layoutParams
        val listViewLp = listView.layoutParams
        val emptyViewLp = emptyView.layoutParams

        headerLp.height = headerHeight
        scrollViewLp.height = scrollViewHeight
        listViewLp.height = scrollViewHeight
        emptyViewLp.height = scrollViewHeight

        findViewById<LinearLayout>(R.id.header).layoutParams = headerLp
        findViewById<ScrollView>(R.id.scroll_view).layoutParams = scrollViewLp
        listView.layoutParams = listViewLp
        emptyView.layoutParams = emptyViewLp
    }

}
