package com.example.omegatracker.entity.repositories

import com.example.omegatracker.room.IssuesChangeList

interface TrackingHistoryRepository {
    suspend fun getChangeList(): List<IssuesChangeList>
    suspend fun insertChange(changeList: IssuesChangeList)

    suspend fun clearDB()
}