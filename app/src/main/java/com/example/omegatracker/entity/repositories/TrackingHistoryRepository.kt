package com.example.omegatracker.entity.repositories

import com.example.omegatracker.room.IssueAndHistory
import com.example.omegatracker.room.IssuesTrackingHistory

interface TrackingHistoryRepository {
    suspend fun insertChange(trackingHistory: IssuesTrackingHistory)

    suspend fun getAllHistory(): List<IssueAndHistory>
    suspend fun clearDB()
}