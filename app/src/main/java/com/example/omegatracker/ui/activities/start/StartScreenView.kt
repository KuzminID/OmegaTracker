package com.example.omegatracker.ui.activities.start

import com.example.omegatracker.entity.Screens
import com.example.omegatracker.ui.activities.base.BaseView

interface StartScreenView : BaseView {
    fun showScreen(screens: Screens)

    fun startService()
}