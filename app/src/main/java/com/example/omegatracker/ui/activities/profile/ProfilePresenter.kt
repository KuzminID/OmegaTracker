package com.example.omegatracker.ui.activities.profile

import com.example.omegatracker.OmegaTrackerApplication.Companion.appComponent
import com.example.omegatracker.service.IssuesServiceBinder
import com.example.omegatracker.ui.activities.base.BasePresenter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProfilePresenter : BasePresenter<ProfileView>() {
    private val userManager = appComponent.getUserManager()
    private val repository = appComponent.getUserRepositoryImpl()
    private lateinit var controller : IssuesServiceBinder
    fun setData() {
        val data = userManager.getUser()
        println(data)
    }

    fun setController(controller : IssuesServiceBinder) {
        this.controller = controller
    }

    fun testExit() {
        controller.stopRunningTasks()
        launch {
            delay(10000)
            repository.clearDB() }
    }

    fun exitFromAccount() {
        userManager.deleteUser()
        controller.stopRunningTasks()
        launch {
            repository.clearDB()
        }
    }
}