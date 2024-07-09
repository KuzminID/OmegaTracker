package com.example.omegatracker.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.omegatracker.service.TaskManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(subcomponents = [RetrofitComponent::class])
class AppModule(private val application: Application) {

    @Singleton
    @Provides
    fun provideApp(): Application {
        return application
    }

    @Singleton
    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideTaskManager(): TaskManager {
        return TaskManager(
        )
    }

}