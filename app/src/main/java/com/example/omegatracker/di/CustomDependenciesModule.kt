package com.example.omegatracker.di

import android.content.SharedPreferences
import com.example.omegatracker.data.UserManager
import com.example.omegatracker.data.repositories.IssueRepositoryImpl
import com.example.omegatracker.data.repositories.TrackingHistoryRepositoryImpl
import com.example.omegatracker.data.repositories.UserRepositoryImpl
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class CustomDependenciesModule {

    @Singleton
    @Provides
    fun provideUserRepositoryImpl(): UserRepositoryImpl {
        return UserRepositoryImpl()
    }

    @Singleton
    @Provides
    fun provideTrackingHistoryRepository(): TrackingHistoryRepositoryImpl {
        return TrackingHistoryRepositoryImpl()
    }

    @Singleton
    @Provides
    fun provideIssueRepository(): IssueRepositoryImpl {
        return IssueRepositoryImpl()
    }

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return Gson()
    }

    @Singleton
    @Provides
    fun provideUserManager(
        gson: Gson,
        sharedPreferences: SharedPreferences
    ): UserManager {
        return UserManager(
            sharedPreferences, gson
        )
    }
}