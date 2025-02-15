package com.example.omegatracker.ui.start

import com.example.omegatracker.OmegaTrackerApplication.Companion.appComponent
import com.example.omegatracker.entity.Screens
import com.example.omegatracker.ui.base.BasePresenter

class StartScreenPresenter : BasePresenter<StartScreenView>() {

    private val userManager = appComponent.getUserManager()

    init {
        if (userManager.getUser() != null) {
            viewState.showScreen(Screens.IssuesScreens)
        } else {
            viewState.showScreen(Screens.AuthenticationScreens)
        }
    }
}