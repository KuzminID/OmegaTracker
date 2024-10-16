package com.example.omegatracker.ui.base

interface BaseFragmentView : BaseView {
    fun navigateUp()

    fun popBackStack()
}