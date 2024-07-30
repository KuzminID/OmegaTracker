package com.example.omegatracker.ui.activities.profile

import com.example.omegatracker.entity.User
import com.example.omegatracker.ui.activities.base.BaseView

interface ProfileView : BaseView {
    fun setUserData(data: User?)
    fun bindService()

    fun showIssueScreen()
}