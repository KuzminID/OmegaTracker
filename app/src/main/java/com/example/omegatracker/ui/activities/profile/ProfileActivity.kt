package com.example.omegatracker.ui.activities.profile

import android.os.Bundle
import com.example.omegatracker.databinding.ActivityProfileBinding
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
    }
}