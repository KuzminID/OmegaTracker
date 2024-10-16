package com.example.omegatracker.ui.auth

import com.example.omegatracker.entity.HelperContent
import com.example.omegatracker.entity.Screens
import com.example.omegatracker.ui.base.BaseView

interface AuthView : BaseView {
    fun initAuthButtonListener()

    fun initialization()
    fun showScreen(screen: Screens)
    fun setSavedUrl(url: String)
    fun setHelperData(data: List<HelperContent>)
    fun initEditTextAction()
}