package com.example.omegatracker.ui.auth

import com.example.omegatracker.OmegaTrackerApplication.Companion.appComponent
import com.example.omegatracker.R
import com.example.omegatracker.entity.Screens
import com.example.omegatracker.ui.base.BasePresenter
import kotlinx.coroutines.launch

class AuthPresenter : BasePresenter<AuthView>() {
    private var userRepositoryImpl = appComponent.getUserRepository()
    private val userManager = appComponent.getUserManager()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        val url = userManager.getUrl()
        if (url != null) {
            viewState.setSavedUrl(url)
        }

        viewState.setHelperData(userRepositoryImpl.getHelperData())
    }


    fun onAuthButtonClicked(token: String, url: String) {
        launch {
            try {
                val user = userRepositoryImpl.authenticate(token, url)
                userManager.saveUser(user)
                userManager.saveToken(token)
                userManager.saveUrl(url.removeSuffix("/"))
                viewState.showMessage(R.string.successful_auth)
                viewState.showScreen(Screens.IssuesScreens)
            } catch (e: Exception) {
                throw e
            }
        }
    }
}