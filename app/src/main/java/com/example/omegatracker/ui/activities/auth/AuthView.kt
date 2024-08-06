package com.example.omegatracker.ui.activities.auth

import com.example.omegatracker.entity.HelperContent
import com.example.omegatracker.entity.Screens
import com.example.omegatracker.ui.activities.base.BaseView

interface AuthView : BaseView {
    fun initAuthButtonListener()
    fun showScreen(screen: Screens)
    fun setSavedUrl(url: String)
    fun setHelperData(data: List<HelperContent>)
    fun initEditTextAction()
}