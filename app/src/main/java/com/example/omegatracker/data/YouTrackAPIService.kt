package com.example.omegatracker.data

import com.example.omegatracker.OmegaTrackerApplication.Companion.retrofitComponent
import com.example.omegatracker.entity.IssueFromJson
import com.example.omegatracker.entity.User

class YouTrackAPIService {
    private lateinit var requestAPI: RequestsApi

    private fun isRequestApiInitialized(): Boolean {
        return this::requestAPI.isInitialized
    }

    suspend fun sendAuthorizationRequest(token: String?): User {
        requestAPI = retrofitComponent.getRequestApi()
        return requestAPI.getUserProfile("Bearer $token")
    }

    suspend fun getIssuesRequest(token: String?): List<IssueFromJson> {
        if (!isRequestApiInitialized()) {
            requestAPI = retrofitComponent.getRequestApi()
        }
        return requestAPI.getIssuesList("Bearer $token")
    }
}