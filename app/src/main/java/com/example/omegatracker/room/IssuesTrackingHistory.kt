package com.example.omegatracker.room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "trackingHistoryTable", foreignKeys = [ForeignKey(
    entity = IssueEntity::class,
    parentColumns = ["id"],
    childColumns = ["issueId"],
    onDelete = ForeignKey.CASCADE
)])
data class IssuesTrackingHistory(
    @PrimaryKey(autoGenerate = true) val historyElementID: Int = 0,
    val issueId : String,
    val issueStartTime: Long,
    val historyGroup : String, //Variable that contains string of date "01.01.2024"
    val endTime: Long
)

data class IssueAndHistory(
    @Embedded
    val issue : IssueEntity,
    @Embedded
    val history : IssuesTrackingHistory // Using Embedded for one to one connection
)
