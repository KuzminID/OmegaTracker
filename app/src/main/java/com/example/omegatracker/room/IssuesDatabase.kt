package com.example.omegatracker.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.omegatracker.OmegaTrackerApplication.Companion.appComponent
import java.util.concurrent.Executors

@Database(entities = [IssueEntity::class, IssuesTrackingHistory::class], version = 1)
abstract class IssuesDatabase : RoomDatabase() {

    abstract fun getIssuesDao(): IssuesDao

    abstract fun getTrackingHistoryDAO(): TrackingHistoryDao

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
        const val DB_NAME = "omega_tracker.db"
    }
}