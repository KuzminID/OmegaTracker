package com.example.omegatracker.ui.activities.profile

import com.example.omegatracker.OmegaTrackerApplication.Companion.appComponent
import com.example.omegatracker.ui.activities.base.BasePresenter

class ProfilePresenter : BasePresenter<ProfileView>() {
    private val userManager = appComponent.getUserManager()
    private val repository = appComponent.getUserRepositoryImpl()
    fun setData() {
        val data = userManager.getUser()
        println(data)
    }

    fun exitFromAccount() {
        userManager.deleteUser()

    }
}