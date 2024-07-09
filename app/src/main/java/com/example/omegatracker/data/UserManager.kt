package com.example.omegatracker.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.omegatracker.entity.User
import com.google.gson.Gson
import javax.inject.Inject

private const val userKey = "user"
private const val tokenKey = "token"
private const val urlKey = "serverUrl"

private enum class Keys(val key: String) {
    USER("user"),
    TOKEN("token"),
    URL("serverUrl")
}

class UserManager @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) {

    private fun SharedPreferences.Editor.putString(key: Keys, value: String) {
        putString(key.key, value)
    }

    private fun SharedPreferences.getString(key: Keys, defValue: String?): String? {
        return getString(key.key, defValue)
    }

    private fun SharedPreferences.Editor.remove(key: Keys) {
        remove(key.key)
    }

    fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        sharedPreferences.edit {
            putString(Keys.USER, userJson)
        }
    }

    fun saveToken(token: String) {
        val tokenJson = gson.toJson(token)
        sharedPreferences.edit {
            putString(Keys.TOKEN, tokenJson)
        }
    }

    fun saveUrl(url: String) {
        val urlJson = gson.toJson(url)
        sharedPreferences.edit {
            putString(Keys.URL, urlJson)
        }
    }

    fun getUser(): User? {
        val userJson =
            sharedPreferences.getString(Keys.USER, null)
        return if (userJson != null) {
            gson.fromJson(userJson, User::class.java)
        } else {
            null
        }
    }

    fun getToken(): String? {
        val tokenJson =
            sharedPreferences.getString(Keys.TOKEN, null)
        return if (tokenJson != null) {
            gson.fromJson(tokenJson, String::class.java)
        } else {
            null
        }
    }

    fun getUrl(): String? {
        val urlJson =
            sharedPreferences.getString(Keys.URL, null)
        return if (urlJson != null) {
            gson.fromJson(urlJson, String::class.java)
        } else {
            null
        }
    }

    fun deleteUser() {
        sharedPreferences.edit {
            remove(Keys.USER)
            remove(Keys.TOKEN)
        }
    }
}