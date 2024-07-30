package com.example.omegatracker

import android.app.Application
import com.example.omegatracker.data.UserManager
import com.example.omegatracker.di.AppComponent
import com.example.omegatracker.di.AppModule
import com.example.omegatracker.di.CustomDependenciesModule
import com.example.omegatracker.di.DaggerAppComponent
import com.example.omegatracker.di.NetworkModule
import com.example.omegatracker.di.RetrofitComponent
import com.example.omegatracker.room.IssuesDatabase

    class OmegaTrackerApplication : Application() {

        companion object {
            private lateinit var userManager: UserManager
            private const val defaultUrl = "https://example.youtrack.cloud"

            lateinit var appComponent: AppComponent
            lateinit var retrofitComponent: RetrofitComponent
            private lateinit var database: IssuesDatabase

            fun setBaseUrl(url: String) {
                if (userManager.getUrl() != url) {
                    retrofitComponent = appComponent.createRetrofitBuilder()
                        .baseUrl(url)
                        .build()
                }
            }
        }


        override fun onCreate() {
            super.onCreate()

            appComponent = DaggerAppComponent.builder()
                .customDependenciesModule(CustomDependenciesModule())
                .networkModule(NetworkModule())
                .appModule((AppModule(this)))
                .build()

            userManager = appComponent.getUserManager()

            retrofitComponent = if (userManager.getUrl() != null) {
                appComponent.createRetrofitBuilder()
                    .baseUrl(userManager.getUrl()!!)
                    .build()
            } else {
                appComponent.createRetrofitBuilder()
                    .baseUrl(defaultUrl)
                    .build()
            }

        }

    }