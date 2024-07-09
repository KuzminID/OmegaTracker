package com.example.omegatracker.di

import com.example.omegatracker.data.RequestsApi
import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class RequestScope

@RequestScope
@Subcomponent(modules = [RetrofitModule::class])
interface RetrofitComponent {

    fun getRequestApi(): RequestsApi

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        fun baseUrl(baseUrl: String): Builder

        fun build(): RetrofitComponent
    }
}