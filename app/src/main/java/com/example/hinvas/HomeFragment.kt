package com.example.hinvas

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.*
import java.net.URL
import android.view.MenuItem
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle

@Parcelize
data class MovieData(
    val title: String,
    val subscriberName: String,
    val thumbnail: Bitmap,
    val subscriberIcon: Bitmap,
    val notificationBackgroundColor: Int,
    val platformIcon: Int,
    val movieStyleIcon: Int
) : Parcelable

class HomeFragment : Fragment() {

    private val fireStore = FireStore()

    private val dataList = ArrayList<MovieData>() // 通知を表示する際に使用する配列
    private val linkList = ArrayList<String>() // 外部リンクを格納する配列
    private val movieDataList = ArrayList<Movie>() // NoticeListAddFragmentに渡すMovieデータを格納したリスト

    private lateinit var listView: ListView // listview関連のやつ

    // スワイプ処理関連
    private lateinit var mySwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var emptyMySwipeRefreshLayout: SwipeRefreshLayout

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    lateinit var icon: Bitmap
    private lateinit var thumbnail: Bitmap


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        setListData()// 初期データをセット

        return View.inflate(context, R.layout.fragment_home, null)
    }

    private fun setListData() {
        // データリストを初期化
        dataList.clear()
        movieDataList.clear()

        for (movie in fireStore.getMoviesDoc()) {

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
            movieDataList.add(movie)
        }
    }

    // データリストをアダプターにセット
    private fun setListViewAdapter() {
        val moviesListAdapter = CustomAdapter(requireContext(), dataList)
        listView.adapter = moviesListAdapter

        // リストをクリックしたときの処理を実装
        // リンクにアクセス（動画先）
        listView.setOnItemClickListener { _, _, position, _ ->
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(linkList[position])))
        }

        listView.setOnItemLongClickListener{ _, _, position, _ ->
            val noticeListAddFragment = NoticeListAddFragment()
            val showNoticeData = dataList[position]
            val sendMovieData = movieDataList[position]


            // Bundleインスタンスを作成
            val bundle = Bundle()
            // putXXXXで値をセットする
            bundle.putParcelable("BUNDLE_KEY_MOVIE_DATA", showNoticeData)
            bundle.putParcelable("BUNDLE_KEY_MOVIE", sendMovieData)

            // Fragmentに値をセットする
            noticeListAddFragment.arguments = bundle

            noticeListAddFragment.show((activity as FragmentActivity).supportFragmentManager,"navigation_bottom_sheet")

            return@setOnItemLongClickListener true
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("LongLogTag", "CutPasteId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView = view.findViewById(R.id.list)
        mySwipeRefreshLayout = view.findViewById(R.id.swiperefresh)
        emptyMySwipeRefreshLayout = view.findViewById(R.id.emptySwiperefresh)

//         機種ごとの画面の縦の長さを取得する
        view.viewTreeObserver?.addOnWindowFocusChangeListener {
            // 初回時に生成
            setWholeViewHeight(
                wholeViewHeight = view.findViewById<LinearLayout>(R.id.viewSize).height,
                mainViewLayout = view.findViewById<LinearLayout>(R.id.mainView),
            )
        }

        // 画面が切り替わるときに生成
        setWholeViewHeight(
            wholeViewHeight = view.findViewById<LinearLayout>(R.id.viewSize).height,
            mainViewLayout = view.findViewById<LinearLayout>(R.id.mainView),
        )

        // dataが0件の場合emptyを表示
        if (dataList.isEmpty()) {
            // 表示・非表示設定 それぞれに対応したSwipeRefreshLayoutを表示・非表示にする
            mySwipeRefreshLayout.visibility = View.GONE
            emptyMySwipeRefreshLayout.visibility = View.VISIBLE
        } else {
            setListViewAdapter()
        }
        // 通知が存在する時のスワイプ処理
        mySwipeRefreshLayout.setOnRefreshListener {
            Log.d("スワイプ処理（Default）", "")
            // クォータ量制限によりコメントアウト
//            val data = scope.launch {
//                fireStore.getUpdatedMovies()
//            }
//            runBlocking {
//                data.join()
//            }
//
//            setListData()
//            setListViewAdapter()
//
//            listView.invalidateViews()
            mySwipeRefreshLayout.isRefreshing = false
        }

        // 通知が存在しない時のスワイプ処理
        emptyMySwipeRefreshLayout.setOnRefreshListener {
            Log.d("スワイプ処理（Empty）", "")
            // クォータ量制限によりコメントアウト
//            var checkUpdater = false
//            val data = scope.launch {
//                checkUpdater = fireStore.getUpdatedMovies()
//            }
//            runBlocking {
//                data.join()
//            }
//
//            setListData()
//            setListViewAdapter()
//
//            // データが空の時から追加した時
//            if (checkUpdater) {
//                mySwipeRefreshLayout.visibility = View.VISIBLE
//                emptyMySwipeRefreshLayout.visibility = View.GONE
//            }
//            listView.invalidateViews()
            emptyMySwipeRefreshLayout.isRefreshing = false
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.home_filter_menu, menu)
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                when(item.itemId){
                    R.id.HomeFilterMenu->{
                        val intent = Intent(activity, HomeFilterActivity::class.java)
                        startActivity(intent)
                    }
                }
                return true
            }
        },viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    class CustomAdapter(context: Context, private var dataList: ArrayList<MovieData>) :
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
}