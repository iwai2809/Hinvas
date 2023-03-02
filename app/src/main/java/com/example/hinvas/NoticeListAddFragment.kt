package com.example.hinvas

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

data class NoticeListAddData(
    val documentId: String,
    val title: String,
    val notificationsListIcon: String,
    val isCheck: Boolean,
)

class NoticeListAddFragment : BottomSheetDialogFragment(), InputTextDialogFragment.CallbackListener {

    private lateinit var listView: ListView
    private lateinit var emptyView: TextView
    private var dataList = ArrayList<NoticeListAddData>()
    private lateinit var root: View

    override fun updateButtonClickFromFragmentDialog(dialog: DialogFragment, text: String) {
        FireStore().addNotificationList(text)
        redrawView()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_notice_list_add, container, false)

        // 下側スワイプの無効化
        if (dialog is BottomSheetDialog) {
            val behaviour = (dialog as BottomSheetDialog).behavior
            behaviour.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                        behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }
            })
        }

        setListData()

        return root
    }

    private fun setListData() {

        dataList.clear() // データリストを初期化

        for (notificationList in FireStore().getNotificationListsDoc()) {
            val data = NoticeListAddData(
                documentId = notificationList.documentId,
                title = notificationList.notificationsListName,
                notificationsListIcon = notificationList.notificationsListIcon,
                isCheck = false
            )
            dataList.add(data)
        }
    }

    private fun setListViewAdapter() {
        val noticeListAdapter = CheckBoxAdapter(requireContext(), dataList)
        listView.adapter = noticeListAdapter
    }

    // カスタムアダプター
    class CheckBoxAdapter(context: Context, private var dataList: ArrayList<NoticeListAddData>) :
        ArrayAdapter<NoticeListAddData>(context, 0, dataList) {

        private val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // 外部から呼び出し可能なマップ
        var checkList: MutableMap<Int, Boolean?> = HashMap()

        init {
            // 初期値を設定する
            for (i in dataList.indices) {
                checkList[i] = dataList[i].isCheck
            }
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            val data = dataList[position] // dataの取得
            var view = convertView

            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.notice_list_item, parent, false)
            }

            view?.findViewById<TextView>(R.id.notice_text)?.text = data.title

            val checkBox = view!!.findViewById<CheckBox>(R.id.notice_checkBox)
            val noticeItem = view.findViewById<LinearLayout>(R.id.notice_item)

            // チェック状態を反映させる
            checkBox.isChecked = checkList[position]!!

            checkBox.setOnClickListener {
                if (checkBox.isChecked) {
                    checkBox.isChecked = true
                    checkList[position] = true
                } else {
                    checkBox.isChecked = false
                    checkList[position] = false
                }
            }

            noticeItem.setOnClickListener {
                if (checkBox.isChecked) {
                    checkBox.isChecked = false
                    checkList[position] = false
                } else {
                    checkBox.isChecked = true
                    checkList[position] = true
                }
            }

            return view
        }
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

    override fun onResume() {
        super.onResume()
        redrawView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val completion = view.findViewById<LinearLayout>(R.id.completion) // 完了ボタン
        val addListBtn = view.findViewById<Button>(R.id.new_playlist)
        listView = view.findViewById(R.id.list_notice)
        emptyView = view.findViewById(R.id.empty_view)

        /* --------------------------------------------------------------- */
        // putXXXXに対応するgetXXXXで値を取得
        val showArgs: MovieData = arguments?.getParcelable("BUNDLE_KEY_MOVIE_DATA")!!
        val movieArgs: Movie = arguments?.getParcelable("BUNDLE_KEY_MOVIE")!!

        // 取得したデータを描画
        view.findViewById<TextView>(R.id.movie_title)
            ?.text = showArgs.title
        view.findViewById<TextView>(R.id.subscriber_name)
            ?.text = showArgs.subscriberName
        view.findViewById<ImageView>(R.id.thumbnail)
            ?.setImageBitmap(showArgs.thumbnail)
        view.findViewById<ImageView>(R.id.subscriber_icon)
            ?.setImageBitmap(showArgs.subscriberIcon)
        view.findViewById<LinearLayout>(R.id.notification_item)
            ?.setBackgroundResource(showArgs.notificationBackgroundColor)
        view.findViewById<ImageView>(R.id.platform_icon)
            ?.setImageResource(showArgs.platformIcon)
        view.findViewById<ImageView>(R.id.movie_style)
            ?.setImageResource(showArgs.movieStyleIcon)

        /*------------------------------------------------------------------*/

        // 新しく通知リストを追加する処理
        addListBtn.setOnClickListener {
            val newFragment = InputTextDialogFragment(_title = "新しいリスト", _success = "作成", _cancel = "キャンセル")
            newFragment.show(childFragmentManager, "test")
        }

        // 完了ボタンの処理
        completion.setOnClickListener {
            listView = view.findViewById(R.id.list_notice)

            for (i in 0 until listView.count) {
                val checkBoxAdapter = listView.adapter as CheckBoxAdapter

                // trueの時にデータを追加する
                if (checkBoxAdapter.checkList[i]!!) {
                    FireStore().addNotificationListItem(movieArgs,dataList[i].documentId) // DBにmovie情報を追加
                    if(dataList[i].notificationsListIcon == "") {
                        //TODO (サムネサップデート)
                        FireStore().updateNotificationListIcon(dataList[i].documentId,movieArgs.thumbnail)
                    }
                }
            }
            this.dismiss() // Bottom sheetを閉じる
        }

        // dataが0件の場合empty_viewを表示
        if (dataList.isEmpty()) {
            listView.visibility = View.GONE     // list_viewを非表示（完全に消す）
            emptyView.visibility = View.VISIBLE // empty_viewを表示
        } else {
            setListViewAdapter()
        }
    }
}