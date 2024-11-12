package com.example.omegatracker.di

import android.content.Context
import com.example.omegatracker.data.repositories.IssueRepositoryImpl
import com.example.omegatracker.data.repositories.TrackingHistoryRepositoryImpl
import com.example.omegatracker.data.repositories.UserRepositoryImpl
import com.example.omegatracker.data.UserManager
import com.example.omegatracker.data.YouTrackAPIService
import com.example.omegatracker.room.IssuesDao
import com.example.omegatracker.room.TrackingHistoryDao
import com.example.omegatracker.service.TaskManager
import com.google.gson.Gson
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, AppModule::class, CustomDependenciesModule::class, DaoModule::class])
interface AppComponent {

    fun createRetrofitBuilder(): RetrofitComponent.Builder

    fun getContext(): Context
    fun getUserRepository(): UserRepositoryImpl
    fun getTrackingHistoryRepository(): TrackingHistoryRepositoryImpl
    fun getIssueRepository(): IssueRepositoryImpl
    fun getGson(): Gson
    fun getUserManager(): UserManager
    fun getYouTrackApiService(): YouTrackAPIService
    fun getTaskManager(): TaskManager
    fun getIssuesDao(): IssuesDao

    fun getHistoryDao(): TrackingHistoryDao
}