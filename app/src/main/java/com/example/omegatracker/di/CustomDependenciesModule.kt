package com.example.omegatracker.di

import android.content.SharedPreferences
import com.example.omegatracker.data.Repositories.ChangeListRepository
import com.example.omegatracker.data.Repositories.IssueRepository
import com.example.omegatracker.data.Repositories.UserRepository
import com.example.omegatracker.data.UserManager
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class CustomDependenciesModule {

    @Singleton
    @Provides
    fun provideUserRepositoryImpl(): UserRepository {
        return UserRepository()
    }

    @Singleton
    @Provides
    fun provideChangeListRepository(): ChangeListRepository {
        return ChangeListRepository()
    }

    @Singleton
    @Provides
    fun provideIssueRepository(): IssueRepository {
        return IssueRepository()
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