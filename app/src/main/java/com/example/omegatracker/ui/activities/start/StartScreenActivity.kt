package com.example.omegatracker.ui.activities.start

import android.content.Intent
import android.os.Bundle
import com.example.omegatracker.R
import com.example.omegatracker.entity.Screens
import com.example.omegatracker.ui.activities.auth.AuthActivity
import com.example.omegatracker.ui.activities.base.BaseActivity
import com.example.omegatracker.ui.activities.issues.IssuesActivity

class StartScreenActivity : BaseActivity(), StartScreenView {
    private val startScreenPresenter: StartScreenPresenter by providePresenter {
        StartScreenPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun showScreen(screens: Screens) {
        val intent = when (screens) {
            Screens.AuthenticationScreens -> Intent(this, AuthActivity::class.java)
            Screens.IssuesScreens -> Intent(this, IssuesActivity::class.java)
            Screens.StartScreens -> Intent(this, StartScreenActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}