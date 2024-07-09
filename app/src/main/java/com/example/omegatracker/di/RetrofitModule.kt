package com.example.omegatracker.di

import com.example.omegatracker.data.RequestsApi
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
class RetrofitModule {

    @RequestScope
    @Provides
    fun provideRequestApi(
        retrofit: Retrofit
    ): RequestsApi {
        return retrofit.create(RequestsApi::class.java)
    }

    @RequestScope
    @Provides
    fun provideRetrofit(
        client: OkHttpClient,
        moshi: Moshi,
        baseUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

}