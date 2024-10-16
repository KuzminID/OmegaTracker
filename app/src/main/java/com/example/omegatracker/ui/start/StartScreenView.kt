package com.example.omegatracker.ui.start

import com.example.omegatracker.entity.Screens
import com.example.omegatracker.ui.base.BaseView

interface StartScreenView : BaseView {
    fun showScreen(screens: Screens)

    fun startService()
}