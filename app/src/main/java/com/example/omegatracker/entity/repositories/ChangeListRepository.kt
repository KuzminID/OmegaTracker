package com.example.omegatracker.entity.repositories

import com.example.omegatracker.room.IssuesChangeList

interface ChangeListRepository {
    suspend fun getChangeList(): List<IssuesChangeList>
    suspend fun insertChange(changeList: IssuesChangeList)
}