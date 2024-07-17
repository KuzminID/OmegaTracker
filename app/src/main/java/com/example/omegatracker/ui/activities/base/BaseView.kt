package com.example.omegatracker.ui.activities.base

import android.content.Context
import android.content.Intent
import android.widget.ImageView
import com.omegar.mvp.MvpView

interface BaseView : MvpView {
    fun showMessage(msg: Int)
    fun initialization()

    //fun bindService()

    fun setAvatar(url: String?, iv : ImageView)

    //fun createIntentForChildClass(context : Context) : Intent
}