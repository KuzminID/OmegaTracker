package com.example.omegatracker.entity.repositories

import com.example.omegatracker.entity.HelperContent
import com.example.omegatracker.entity.Issue
import com.example.omegatracker.entity.IssueFromJson
import com.example.omegatracker.entity.IssuesFilterType
import com.example.omegatracker.entity.User
import com.example.omegatracker.room.IssueEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun authenticate(token: String, url: String): User?

    fun convertStringDateToMilliseconds(date: String?): Long
    suspend fun getIssuesList(): Flow<List<Issue>>

    fun getIssuesHeaderData(): List<IssuesFilterType>
    fun getHelperData(): List<HelperContent>
    suspend fun parseIssue(issue: List<IssueFromJson>): List<Issue>
    fun compareIssues(dbIssues: List<Issue>?, serverIssues: List<Issue>)

    suspend fun upsertIssueToDB(issue: Issue)
    suspend fun getAllIssuesFromDB(): List<Issue>
    suspend fun getIssueByIDFromDB(id: String): Issue?
    suspend fun deleteIssueFromDB(issue: Issue)
    suspend fun activateIssue(issue: Issue)
    suspend fun deactivateIssue(issue: Issue)

    suspend fun clearDB()

    fun checkIsTimeToday(time: Long): Boolean

    suspend fun deactivateAllIssues()
    fun convertFromEntityToIssue(entity: IssueEntity?): Issue?
}