package com.example.omegatracker.ui.activities.profile

import com.example.omegatracker.OmegaTrackerApplication.Companion.appComponent
import com.example.omegatracker.service.IssuesServiceBinder
import com.example.omegatracker.ui.activities.base.BasePresenter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProfilePresenter : BasePresenter<ProfileView>() {
    private val userManager = appComponent.getUserManager()
    private val repository = appComponent.getUserRepository()
    private lateinit var controller: IssuesServiceBinder
    fun setData() {
        val data = userManager.getUser()
        viewState.setUserData(data)

    }

    fun setController(controller: IssuesServiceBinder) {
        this.controller = controller
    }

    fun testExit() {
        controller.stopRunningIssues()

        launch {
            delay(1000)
            repository.clearDB()
        }
    }

    fun exitFromAccount() {
        userManager.deleteUser()
        controller.stopRunningIssues()
        launch {
            repository.clearDB()
        }
    }
}