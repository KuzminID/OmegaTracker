package com.example.omegatracker.data

import com.example.omegatracker.OmegaTrackerApplication.Companion.retrofitComponent
import com.example.omegatracker.entity.IssueFromJson
import com.example.omegatracker.entity.User

class YouTrackAPIService {
    private lateinit var requestAPI: RequestsApi

    private fun initializeRequestAPI() {
        if (!this::requestAPI.isInitialized) {
            requestAPI = retrofitComponent.getRequestApi()
        }
    }

    suspend fun sendAuthorizationRequest(token: String?): User {
        initializeRequestAPI()
        return requestAPI.getUserProfile("Bearer $token")
    }

    suspend fun getIssuesRequest(token: String?): List<IssueFromJson> {
        initializeRequestAPI()

        // Возвращаем список задач
        return requestAPI.getIssuesList("Bearer $token")
    }

}