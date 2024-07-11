package com.example.omegatracker.entity

import com.example.omegatracker.room.IssueEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun authenticate(token: String, url: String): User?
    suspend fun getIssuesList(): Flow<List<Issue>>
    fun getHelperData(): List<HelperContent>
    suspend fun parseIssue(issue: List<IssueFromJson>): List<Issue>
    fun compareIssues(dbIssues : List<Issue>?, serverIssues : List<Issue>)

    suspend fun upsertIssueToDB(issue: Issue)
    suspend fun getAllIssuesFromDB() : List<Issue>
    suspend fun getIssueByIDFromDB(id : String) : Issue?
    suspend fun deleteIssueFromDB(issue:Issue)
    suspend fun activateIssue(issue: Issue)
    suspend fun deactivateIssue(issue: Issue)

    suspend fun clearDB()

    suspend fun deactivateAllIssues()
    fun convertFromEntityToIssue(entity: IssueEntity?) : Issue?
}