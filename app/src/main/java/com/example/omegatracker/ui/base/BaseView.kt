package com.example.omegatracker.ui.base

import android.widget.ImageView
import com.omegar.mvp.MvpView

interface BaseView : MvpView {
    fun showMessage(msg: Int)

    fun showMessageWithLongDuration(msg: Int)

    fun hideMessage()

    fun setAvatar(url: String?, iv: ImageView)
}