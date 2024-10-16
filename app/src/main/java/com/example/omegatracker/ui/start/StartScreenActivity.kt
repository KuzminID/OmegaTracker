package com.example.omegatracker.ui.start

import android.content.Intent
import android.os.Bundle
import com.example.omegatracker.OmegaTrackerApplication.Companion.appComponent
import com.example.omegatracker.R
import com.example.omegatracker.entity.Screens
import com.example.omegatracker.service.IssuesService
import com.example.omegatracker.ui.auth.AuthActivity
import com.example.omegatracker.ui.base.BaseActivity
import com.example.omegatracker.ui.main.MainActivity

class StartScreenActivity : BaseActivity(), StartScreenView {
    private val startScreenPresenter: StartScreenPresenter by providePresenter {
        StartScreenPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        startService()
    }

    override fun showScreen(screens: Screens) {
        val intent = when (screens) {
            Screens.AuthenticationScreens -> Intent(this, AuthActivity::class.java)
            Screens.IssuesScreens -> Intent(this, MainActivity::class.java)
            Screens.StartScreens -> Intent(this, StartScreenActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

    override fun startService() {
        val serviceIntent = Intent(appComponent.getContext(), IssuesService::class.java)
        startService(serviceIntent)
    }
}