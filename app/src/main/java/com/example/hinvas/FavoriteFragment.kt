package com.example.hinvas

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.*
import java.net.URL

class FavoriteFragment : Fragment(), InputTextDialogFragment.CallbackListener {

    private val dataList = ArrayList<NotificationListData>()

    private lateinit var root: View
    private lateinit var listView: ListView
    private lateinit var emptyView: TextView
    private lateinit var addBtn: LinearLayout

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    lateinit var icon: Bitmap

    @Parcelize
    data class NotificationListData(
        val documentId: String,
        val notificationsListName: String,
        val notificationsListIcon: Bitmap,
        val notificationsSum: Int = 0,
    ) : Parcelable


    override fun updateButtonClickFromFragmentDialog(dialog: DialogFragment, text: String) {
        FireStore().addNotificationList(text)
        redrawView()
    }

    override fun onResume() {
        super.onResume()
        redrawView() // フラグメントの再描画を行うメソッド
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_favorite, container, false)

        addBtn = root.findViewById(R.id.add_btn)

        //追加ボタンのイベント
        addBtn.setOnClickListener {
            //ダイアログのView生成・表示
            val newFragment = InputTextDialogFragment(_title = "新しいリスト", _success = "作成", _cancel = "キャンセル")
            newFragment.show(childFragmentManager, "test")
        }

        setListData() // 初期データセット
        return root
    }

    private fun setListData() {
        dataList.clear() // データリストの初期化

        for (notificationList in FireStore().getNotificationListsDoc()) {
            val imageDownload = scope.launch {
                icon = try {
                    URL(notificationList.notificationsListIcon).openStream().use {
                        BitmapFactory.decodeStream(it)
                    }
                } catch (e: Exception) {
                    BitmapFactory.decodeResource(resources, R.drawable.ic_folder)
                }
            }
            runBlocking {
                imageDownload.join()
            }
            val data = NotificationListData(
                documentId = notificationList.documentId,
                notificationsListName = notificationList.notificationsListName,
                notificationsListIcon = icon,
                notificationsSum = notificationList.notificationsSum
            )

            dataList.add(data)
        }
    }

    private fun setListViewAdapter() {
        val favoriteListAdapter = CustomAdapter(requireContext(), dataList)
        listView.adapter = favoriteListAdapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val activityIntent = Intent(activity, FavoriteNoticeListItemActivity::class.java)
            activityIntent.putExtra("documentId", dataList[position].documentId)
            activityIntent.putExtra("notificationsListName", dataList[position].notificationsListName)
            activityIntent.putExtra("notificationsSum", dataList[position].notificationsSum)
            startActivity(activityIntent)
//            val activityIntent = Intent(this, StreamerAddActivity::class.java)
//            activityIntent.putExtra("url", intent.getStringExtra(Intent.EXTRA_TEXT).toString())
//            Log.d("main", "activity")
//            startActivity(activityIntent)
        }
    }

    class CustomAdapter(context: Context, private var dataList: ArrayList<NotificationListData>) :
        ArrayAdapter<NotificationListData>(context, 0, dataList) {

        private val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        @SuppressLint("SetTextI18n")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val data = dataList[position]

            var view = convertView
            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.favorite_list_item, parent, false)
            }

            view?.findViewById<ImageView>(R.id.list_icon)?.setImageBitmap(data.notificationsListIcon)
            view?.findViewById<TextView>(R.id.list_name)?.text = data.notificationsListName
            // 処理が重くなるためコメントアウト
//            view?.findViewById<TextView>(R.id.list_item_sum)?.text = "${data.notificationsSum}本の動画"

            return view!!
        }
    }

    @SuppressLint("CutPasteId")

    private fun setViewSize(view: View) {
        // 画面が切り替わるときに生成
        setWholeViewHeight(
            wholeViewHeight = view.findViewById<LinearLayout>(R.id.view_size).height,
            mainViewLayout = view.findViewById<LinearLayout>(R.id.view_size)
        )

        // addBtn,scrollView,listView,emptyViewの高さを再設定
        val addBtnHeight = view.findViewById<LinearLayout>(R.id.view_size).layoutParams.height / 10
        val scrollViewHeight = view.findViewById<LinearLayout>(R.id.view_size).layoutParams.height * 9 / 10

        val addBtnLp = view.findViewById<LinearLayout>(R.id.add_btn).layoutParams
        val scrollViewLp = view.findViewById<ScrollView>(R.id.scroll_view).layoutParams
        val listViewLp = listView.layoutParams
        val emptyViewLp = emptyView.layoutParams

        addBtnLp.height = addBtnHeight
        scrollViewLp.height = scrollViewHeight
        listViewLp.height = scrollViewHeight
        emptyViewLp.height = scrollViewHeight

        view.findViewById<LinearLayout>(R.id.add_btn).layoutParams = addBtnLp
        view.findViewById<ScrollView>(R.id.scroll_view).layoutParams = scrollViewLp
        listView.layoutParams = listViewLp
        emptyView.layoutParams = emptyViewLp
    }
    // フラグメントの再描画を行うメソッド, 複数の箇所（onResume(),updateButtonClickFromFragmentDialog())で実行されるためメソッド化
    private fun redrawView() {
        setListData()
        setListViewAdapter()

        // データが空の時
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

        // dataが0件の場合empty_viewを表示
        if (dataList.isEmpty()) {
            listView.visibility = View.GONE     // list_viewを非表示（完全に消す）
            emptyView.visibility = View.VISIBLE // empty_viewを表示
        } else {
            setListViewAdapter()
        }
    }
}