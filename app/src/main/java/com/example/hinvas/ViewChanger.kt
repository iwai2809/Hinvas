package com.example.hinvas

import android.annotation.SuppressLint
import android.view.View

private var viewSizeHeight = 0
private var navigationBarViewHeight = 0

// 機種ごとの画面サイズに合わせて縦の長さを調整する
@SuppressLint("CutPasteId")
fun setWholeViewHeight(
    wholeViewHeight: Int,
    mainViewLayout: View,
) {

    val wholeViewSizeLp = mainViewLayout.layoutParams

    if (wholeViewHeight != 0) {
        viewSizeHeight = wholeViewHeight
    }

    wholeViewSizeLp.height = viewSizeHeight - navigationBarViewHeight
    mainViewLayout.layoutParams = wholeViewSizeLp
}

fun setNavigationBarViewHeight(height : Int) {
    navigationBarViewHeight = height
}

