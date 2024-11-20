package com.example.omegatracker.data.repositories

import com.example.omegatracker.OmegaTrackerApplication.Companion.appComponent
import com.example.omegatracker.entity.repositories.TrackingHistoryRepository
import com.example.omegatracker.room.IssueAndHistory
import com.example.omegatracker.room.IssuesTrackingHistory
import com.example.omegatracker.room.TrackingHistoryDao

class TrackingHistoryRepositoryImpl :
    TrackingHistoryRepository {

    //private val dao = appComponent.getHistoryDao()

    private val dao: TrackingHistoryDao by lazy {
        appComponent.getHistoryDao()
    }

    override suspend fun insertChange(trackingHistory: IssuesTrackingHistory) {
        dao.insertData(trackingHistory)

    }

    override suspend fun getAllHistory(): List<IssueAndHistory> {
        return dao.getAllHistory()
    }

    override suspend fun clearDB() {
        dao.clearDB()
    }
}