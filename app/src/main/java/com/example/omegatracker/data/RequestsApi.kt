package com.example.omegatracker.data

import com.example.omegatracker.entity.IssueFromJson
import com.example.omegatracker.entity.User
import retrofit2.http.GET
import retrofit2.http.Header

interface RequestsApi {
    @GET("/api/users/me?fields=name,avatarUrl,email,login")
    suspend fun getUserProfile(@Header("Authorization") authToken: String?): User

    @GET("/api/issues?fields=id,summary,description,project(name,shortName),customFields(value(minutes,name,isResolved),minutes,name,id),updated")
    suspend fun getIssuesList(@Header("Authorization") authToken: String?): List<IssueFromJson>
}