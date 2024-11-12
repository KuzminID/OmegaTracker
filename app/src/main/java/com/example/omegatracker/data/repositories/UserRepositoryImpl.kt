package com.example.omegatracker.data.repositories

import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.OmegaTrackerApplication.Companion.appComponent
import com.example.omegatracker.entity.HelperContent
import com.example.omegatracker.entity.User
import com.example.omegatracker.entity.repositories.UserRepository

class UserRepositoryImpl :
    UserRepository {

    private val youTrackApiService = appComponent.getYouTrackApiService()

    override suspend fun authenticate(token: String, url: String): User {
        OmegaTrackerApplication.setBaseUrl(url)
        val user = youTrackApiService.sendAuthorizationRequest(token)
        val avatarUrl = user.avatarUrl
        user.avatarUrl = url + avatarUrl
        return user
    }


    override fun getHelperData(): List<HelperContent> {
        return HelperContent.entries
    }


}