package com.example.omegatracker.ui.activities.base

import com.omegar.mvp.MvpView

interface BaseView : MvpView {
    fun showMessage(msg: Int)
    fun initialization()
}