package com.example.omegatracker.di

import com.example.omegatracker.room.IssuesDao
import com.example.omegatracker.room.IssuesDatabase
import com.example.omegatracker.room.TrackingHistoryDao
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
    fun provideChangesDao(database: IssuesDatabase): TrackingHistoryDao {
        return database.getTrackingHistoryDAO()
    }

    @Singleton
    @Provides
    fun provideDatabase(): IssuesDatabase {
        return IssuesDatabase.Dependencies.getDatabase()
    }
}