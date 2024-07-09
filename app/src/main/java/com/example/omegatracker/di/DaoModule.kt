package com.example.omegatracker.di

import com.example.omegatracker.room.IssuesDao
import com.example.omegatracker.room.IssuesDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DaoModule {

    @Singleton
    @Provides
    fun provideIssuesDao(database: IssuesDatabase): IssuesDao {
        return database.getIssuesDao()
    }

    @Singleton
    @Provides
    fun provideDatabase(): IssuesDatabase {
        return IssuesDatabase.Dependencies.getDatabase()
    }
}