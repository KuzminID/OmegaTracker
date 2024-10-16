package com.example.omegatracker.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface HistoryDao {
    @Query("SELECT * FROM changeListTable")
    fun getChangesList(): List<IssuesChangeList>

    @Upsert
    fun insertData(changeList: IssuesChangeList)

    @Query("DELETE FROM issues")
    fun clearDB()
}