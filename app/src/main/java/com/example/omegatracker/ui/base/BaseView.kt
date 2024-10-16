package com.example.omegatracker.ui.base

import android.widget.ImageView
import com.omegar.mvp.MvpView

interface BaseView : MvpView {
    fun showMessage(msg: Int)

    fun setAvatar(url: String?, iv: ImageView)
}