package com.example.omegatracker.di

import android.content.Context
import com.example.omegatracker.data.Repositories.ChangeListRepository
import com.example.omegatracker.data.Repositories.IssueRepository
import com.example.omegatracker.data.Repositories.UserRepository
import com.example.omegatracker.data.UserManager
import com.example.omegatracker.data.YouTrackAPIService
import com.example.omegatracker.room.ChangesDao
import com.example.omegatracker.room.IssuesDao
import com.example.omegatracker.service.TaskManager
import com.google.gson.Gson
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, AppModule::class, CustomDependenciesModule::class, DaoModule::class])
interface AppComponent {

    fun createRetrofitBuilder(): RetrofitComponent.Builder

    fun getContext(): Context
    fun getUserRepository(): UserRepository
    fun getChangeListRepository(): ChangeListRepository
    fun getIssueRepository(): IssueRepository
    fun getGson(): Gson
    fun getUserManager(): UserManager
    fun getYouTrackApiService(): YouTrackAPIService
    fun getTaskManager(): TaskManager
    fun getIssuesDao(): IssuesDao

    fun getChangesDao(): ChangesDao
}