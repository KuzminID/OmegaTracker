package com.example.omegatracker.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "changeListTable")
data class IssuesChangeList(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var startTime: Long,
    val issueSummary: String?,
    val projectName: String?,
    val endTime: Long,
    val durationTime: Long,
    val time: Long
)
