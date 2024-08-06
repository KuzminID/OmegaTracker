package com.example.omegatracker.data

import com.example.omegatracker.OmegaTrackerApplication.Companion.retrofitComponent
import com.example.omegatracker.entity.IssueFromJson
import com.example.omegatracker.entity.User
import retrofit2.Response

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

    suspend fun getIssuesRequest(token: String?): Pair<List<IssueFromJson>, String?> {
        initializeRequestAPI()

        val response: Response<List<IssueFromJson>> = requestAPI.getIssuesList("Bearer $token")

        val issues = response.body() ?: emptyList()

        // Получаем значение заголовка Date из ответа
        val dateHeader = response.headers()["Date"]

        // Возвращаем список задач и значение заголовка Date
        return Pair(issues, dateHeader)
    }

}