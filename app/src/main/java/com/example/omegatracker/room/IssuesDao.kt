package com.example.omegatracker.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface IssuesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIssue(issue: IssueEntity)

    @Update
    suspend fun updateIssue(issue: IssueEntity)

    @Delete
    suspend fun deleteIssue(issue: IssueEntity)

    @Query("SELECT * FROM issuesTable WHERE id = :id")
    suspend fun getIssueById(id: String): IssueEntity?

    @Query("SELECT * FROM issuesTable")
    suspend fun getAllIssues(): List<IssueEntity>

    @Query("SELECT * FROM issuesTable WHERE projectShortName = :projectShortName")
    suspend fun getIssuesByProject(projectShortName: String): List<IssueEntity>

    @Query("SELECT * FROM issuesTable WHERE isActive = 1")
    suspend fun getActiveIssues(): List<IssueEntity>

    @Query("UPDATE issuesTable SET isActive = 0 WHERE id = :id")
    suspend fun deactivateIssue(id: String)

    @Query("DELETE FROM issuesTable")
    suspend fun deleteAll()

    @Query("UPDATE issuesTable SET isActive = 0")
    suspend fun deactivateAllIssues()
}