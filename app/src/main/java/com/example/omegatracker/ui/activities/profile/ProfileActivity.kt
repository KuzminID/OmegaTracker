package com.example.omegatracker.ui.activities.profile

import android.content.Intent
import android.os.Bundle
import com.example.omegatracker.databinding.ActivityProfileBinding
import com.example.omegatracker.entity.User
import com.example.omegatracker.ui.activities.auth.AuthActivity
import com.example.omegatracker.ui.activities.base.BaseActivity

class ProfileActivity : BaseActivity(), ProfileView {
    private lateinit var binding : ActivityProfileBinding
    private val profilePresenter : ProfilePresenter by providePresenter {
        ProfilePresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        profilePresenter.setData()

        binding.profileExitBtn.setOnClickListener {
            val intent = Intent(this,AuthActivity::class.java)

        }
    }

    override fun setUserData(data: User) {
        binding.profileNameTv.text = data.name
        binding.profileEmailTv.text = data.email
    }
}