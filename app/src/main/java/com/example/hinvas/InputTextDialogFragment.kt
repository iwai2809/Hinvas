package com.example.hinvas

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment

class InputTextDialogFragment constructor(_title : String, _success : String, _cancel : String) :DialogFragment() {
    //Listenerをセットする変数
    private lateinit var listener: CallbackListener
    private val title: String
    private val success: String
    private val cancel: String

    init {
        title = _title
        success = _success
        cancel = _cancel
    }

    //実行するメソッドを手議したInterface
    interface CallbackListener {
        fun updateButtonClickFromFragmentDialog(dialog: DialogFragment,text: String)
    }

    //この中でListenerにセットする
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            //parentFragment（呼び出し元）をListenerに変換する
            val fragment = parentFragment
            listener = fragment as CallbackListener
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString() +
                    " must implement NoticeDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog, null)

            //dialogViewにレイアウトをセット
            builder.setTitle(title)
                .setView(dialogView)
                .setPositiveButton(success,
                    //もともとは引数dialog, id
                    DialogInterface.OnClickListener { _, _ ->
                        //作成を押したときの処理
                        val text = dialogView?.findViewById<EditText>(R.id.editText)?.text//EditTextのテキストを取得
                        if (!text.isNullOrEmpty()){//textが空でなければ
                            listener.updateButtonClickFromFragmentDialog(this,text.toString())
                        }
                    })
                .setNegativeButton(cancel,
                    DialogInterface.OnClickListener { _, _ ->
                        //キャンセル処理
                        dialog?.cancel()
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }
}