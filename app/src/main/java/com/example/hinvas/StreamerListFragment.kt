package com.example.hinvas

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.*
import android.widget.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.*
import java.net.URL

class StreamerListFragment : Fragment() {

    private val linkList = ArrayList<String>() // 外部リンクを格納する配列
    private val dataList = ArrayList<StreamerData>() // 通知を表示する際に使用する配列

    private lateinit var listView: ListView
    private lateinit var emptyView: TextView

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    lateinit var icon: Bitmap

    data class StreamerData(
        val streamerIcon: Bitmap,
        val streamerPlatform: String,
        val streamerContributor: String,
        // 機能ができるまでコメントアウト（青丸）
//        val streamerNotice: String
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // super.onCreateView(inflater, container, savedInstanceState)
        setListData()// 初期データをセット
        return inflater.inflate(R.layout.fragment_streamer_list, container, false)
    }

    // DBのデータを格納
    private fun setListData() {
        // データリストを初期化
        dataList.clear()

        // 実際のDBからデータを抽出し、格納
        for (subscriber in FireStore().getSubscribersDoc()) {

            // 機能ができるまでコメントアウト（青丸）
//            val resultNotice: String = if (noticeCheck(subscriber.subscriberId)) {
//                "・"
//            } else {
//                ""
//            }

            val imageDownload = scope.launch {
                icon = try {
                    URL(subscriber.subscriberIcon).openStream().use {
                        BitmapFactory.decodeStream(it)
                    }
                } catch (e: Exception) {
                    BitmapFactory.decodeResource(resources, R.drawable.error_image)
                }
            }
            runBlocking {
                imageDownload.join()
            }
            val data = StreamerData(
                icon,
                subscriber.platform,
                subscriber.subscriberName,
//                機能ができるまでコメントアウト（青丸）
//                resultNotice
            )

            dataList.add(data)

            if (subscriber.platform == "Youtube") {
                linkList.add("https://www.youtube.com/channel/" + subscriber.subscriberId)
            } else {
                Log.e("subscriber.platform", "予期せぬ値が入力されています")
                linkList.add("https://console.firebase.google.com/") // 適当なリンクを用意（配列の順番がバグる＆存在しないリンクだと強制終了するため）
            }
        }
    }

    // データリストをアダプターにセット
    private fun setListViewAdapter() {
        val streamerListAdapter = CustomAdapter(requireContext(), dataList)
        listView.adapter = streamerListAdapter

        // リストをクリックしたときの処理を実装
        // リンクにアクセス（動画先）
        listView.setOnItemClickListener { _, _, position, _ ->
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(linkList[position])))
        }
    }

    class CustomAdapter(context: Context, private var dataList: ArrayList<StreamerData>) :
        ArrayAdapter<StreamerData>(context, 0, dataList) {
        private val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            val data = dataList[position]

            var view = convertView
            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.streamer_list_item, parent, false)
            }

            view?.findViewById<ImageView>(R.id.channelIcon)?.setImageBitmap(data.streamerIcon)

            // プラットフォームの半別
            // リンクと表示するプラットフォームのアイコンの画像を設定する
            if (data.streamerPlatform == "Youtube") {
                view?.findViewById<ImageView>(R.id.platform_icon)
                    ?.setImageResource(R.drawable.ic_youtube)
            } else {
                Log.e("data.streamerPlatform", "予期せぬプラットフォームが入力されています")
                view?.findViewById<ImageView>(R.id.platform_icon)
                    ?.setImageResource(R.drawable.ic_error)
            }

            view?.findViewById<TextView>(R.id.contributor)?.text = data.streamerContributor

            // 機能ができるまでコメントアウト（青丸）
//            view?.findViewById<TextView>(R.id.notice)?.text = data.streamerNotice

            return view!!
        }
    }

    @SuppressLint("CutPasteId")
    private fun setViewSize(view: View) {
        // 画面が切り替わるときに生成
        setWholeViewHeight(
            wholeViewHeight = view.findViewById<LinearLayout>(R.id.viewSize).height,
            mainViewLayout = view.findViewById<ScrollView>(R.id.scrollView)
        )

        // listViewとemptyViewの高さを再設定
        val listViewLp = listView.layoutParams
        val emptyViewLp = emptyView.layoutParams
        listViewLp.height = view.findViewById<ScrollView>(R.id.scrollView).layoutParams.height
        emptyViewLp.height = view.findViewById<ScrollView>(R.id.scrollView).layoutParams.height
        listView.layoutParams = listViewLp
        emptyView.layoutParams = emptyViewLp
    }

    // 更新処理
    override fun onResume() {
        super.onResume()

        setListData()
        setListViewAdapter()

        // データが空の時から追加した時
        if (dataList.isNotEmpty()) {
            listView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
        }
        listView.invalidateViews()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView = view.findViewById(R.id.list_view)
        emptyView = view.findViewById(R.id.empty_view)

        setViewSize(view)

        // dataが0件の場合emptyを表示
        if (dataList.isEmpty()) {
            // 表示・非表示設定
            listView.visibility = View.GONE     // list_viewを非表示（完全に消す）
            emptyView.visibility = View.VISIBLE // empty_viewを表示
        } else {
            setListViewAdapter()
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_option, menu)
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                when(item.itemId){
                    R.id.LoginMenu->{
                        val intent = Intent(activity, StreamerAddActivity::class.java)
                        startActivity(intent)
                    }
                    R.id.FilterMenu->{
                        val intent=Intent(activity,SearchFilterActivity::class.java)
                        startActivity(intent)
                    }
                }
                return true
            }
        },viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
    // 機能が実装できるまではコメントアウト（青丸）
//    fun noticeCheck(id: String): Boolean {
//        if (true) {
//            return true
//        } else {
//            return false
//        }
//    }
}
