package com.example.omegatracker.entity.repositories

import com.example.omegatracker.entity.Issue
import com.example.omegatracker.entity.IssueFromJson
import com.example.omegatracker.entity.IssuesFilterType
import com.example.omegatracker.room.IssueEntity
import kotlinx.coroutines.flow.Flow

interface IssueRepository {

    suspend fun getIssuesList(): Flow<List<Issue>>

    fun getIssuesHeaderData(): List<IssuesFilterType>

    suspend fun parseIssue(issue: List<IssueFromJson>): List<Issue>
    fun compareIssues(dbIssues: List<Issue>?, serverIssues: List<Issue>)

    suspend fun upsertIssueToDB(issue: Issue)

    suspend fun updateDBIssue(issue: Issue)
    suspend fun getAllIssuesFromDB(): List<Issue>
    suspend fun getIssueByIDFromDB(id: String): Issue?
    suspend fun deleteIssueFromDB(issue: Issue)
    suspend fun activateIssue(issue: Issue)
    suspend fun deactivateIssue(issue: Issue)

    fun checkIsTimeToday(time: Long): Boolean

    suspend fun deactivateAllIssues()
    fun convertFromEntityToIssue(entity: IssueEntity?): Issue?

    suspend fun clearDB()
}