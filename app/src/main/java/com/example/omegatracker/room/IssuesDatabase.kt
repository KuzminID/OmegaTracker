package com.example.omegatracker.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.omegatracker.OmegaTrackerApplication.Companion.appComponent

@Database(entities = [IssueEntity::class], version = 1)
abstract class IssuesDatabase : RoomDatabase() {

    abstract fun getIssuesDao(): IssuesDao

    object Dependencies {
        private val applicationContext = appComponent.getContext()

        private val appDatabase: IssuesDatabase by lazy {

            Room.databaseBuilder(applicationContext, IssuesDatabase::class.java, DB_NAME)
                .build()
        }

        fun getDatabase(): IssuesDatabase {
            return appDatabase
        }
    }

    companion object {
        const val DB_NAME = "issues.db"
    }
}