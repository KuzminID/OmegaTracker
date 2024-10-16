package com.example.omegatracker.entity.repositories

import com.example.omegatracker.entity.HelperContent
import com.example.omegatracker.entity.User

interface UserRepository {
    suspend fun authenticate(token: String, url: String): User?
    fun getHelperData(): List<HelperContent>
}