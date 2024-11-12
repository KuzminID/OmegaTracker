package com.example.omegatracker.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TrackingHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(trackingHistory: IssuesTrackingHistory): Long

    @Query("DELETE FROM trackingHistoryTable")
    fun clearDB()

    @Query("SELECT * FROM trackingHistoryTable JOIN issuesTable ON trackingHistoryTable.issueId = issuesTable.id")
    fun getAllHistory() : List<IssueAndHistory>

    @Query("SELECT * FROM trackingHistoryTable WHERE historyElementID = :id")
    fun getHistoryById(id : String) : IssuesTrackingHistory
}