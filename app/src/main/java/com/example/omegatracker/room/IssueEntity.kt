package com.example.omegatracker.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.omegatracker.entity.Issue

@Entity(tableName = "issues")
data class IssueEntity(
    @PrimaryKey val id: String,
    val summary: String,
    val description: String,
    val spentTime: Long,
    val estimatedTime: Long,
    val projectShortName: String,
    val projectName: String,
    val state: Int,
    val isActive: Boolean,
    val startTime: Long,
    val updateTime : Long
) {
    constructor(issue: Issue) : this(
        issue.id,
        issue.summary ?: "",
        issue.description ?: "",
        issue.spentTime.inWholeMilliseconds,
        issue.estimatedTime.inWholeMilliseconds,
        issue.projectShortName ?: "",
        issue.projectName ?: "",
        issue.state.stateName,
        issue.isActive,
        issue.startTime,
        issue.updateTime
    )
}