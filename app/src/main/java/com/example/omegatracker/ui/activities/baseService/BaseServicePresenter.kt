package com.example.omegatracker.ui.activities.baseService

import android.util.Log
import com.example.omegatracker.service.IssuesServiceBinder
import com.example.omegatracker.ui.activities.base.BasePresenter
import javax.inject.Singleton

@Singleton
open class BaseServicePresenter<V : BaseServiceView> : BasePresenter<V>() {
    fun printThis() {
        Log.d("BasePresenter",this.toString())
    }
}