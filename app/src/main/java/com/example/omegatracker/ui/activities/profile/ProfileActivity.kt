package com.example.omegatracker.ui.activities.profile

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.lifecycle.lifecycleScope
import com.example.omegatracker.databinding.ActivityProfileBinding
import com.example.omegatracker.entity.User
import com.example.omegatracker.service.IssuesService
import com.example.omegatracker.service.IssuesServiceBinder
import com.example.omegatracker.ui.activities.auth.AuthActivity
import com.example.omegatracker.ui.activities.base.BaseActivity
import com.example.omegatracker.ui.activities.issues.IssuesActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ProfileActivity : BaseActivity(), ProfileView {
    private lateinit var binding: ActivityProfileBinding
    private val profilePresenter: ProfilePresenter by providePresenter {
        ProfilePresenter()
    }

    private val _serviceControllerState = MutableStateFlow<IssuesServiceBinder?>(null)

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            service as IssuesServiceBinder
            _serviceControllerState.value = service
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            _serviceControllerState.value = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindService()
        profilePresenter.setData()

        showIssueScreen()

        binding.profileTestBtn.setOnClickListener {
            profilePresenter.testExit()
        }

        binding.profileExitBtn.setOnClickListener {
            profilePresenter.exitFromAccount()
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun setUserData(data: User?) {
        binding.profileNameTv.text = data?.name
        binding.profileEmailTv.text = data?.email
        setAvatar(data?.avatarUrl, binding.profileAvatarIv)
    }

    override fun bindService() {
        val intent = Intent(this, IssuesService::class.java)
        bindService(
            intent,
            connection,
            BIND_AUTO_CREATE
        )

        lifecycleScope.launch {
            _serviceControllerState.collect { controller ->
                if (controller != null) {
                    profilePresenter.setController(controller)
                }
            }
        }
    }

    override fun showIssueScreen() {
        binding.profileBackBtn.setOnClickListener {
            val intent = Intent(this, IssuesActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}