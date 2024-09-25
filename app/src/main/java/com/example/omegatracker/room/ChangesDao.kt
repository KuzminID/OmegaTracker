package com.example.omegatracker.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface ChangesDao {
    @Query("SELECT * FROM changeListTable")
    fun getChangesList(): List<IssuesChangeList>

    @Upsert
    fun insertData(changeList: IssuesChangeList)
}