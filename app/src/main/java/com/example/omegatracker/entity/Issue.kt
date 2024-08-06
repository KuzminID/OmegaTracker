package com.example.omegatracker.entity

import com.example.omegatracker.R
import kotlin.time.Duration

enum class IssueState(
    val stateName: Int
) {
    Finished(R.string.state_finished),
    Open(R.string.state_open),
    OnPause(R.string.state_on_pause),
    OnStop(R.string.state_on_stop),
    OnWork(R.string.state_on_work)
}

data class Issue(
    val id: String,
    val summary: String?,
    val description: String?,
    var spentTime: Duration,
    val estimatedTime: Duration,
    val projectShortName: String?,
    val projectName: String?,
    var isActive: Boolean = false,
    var state: IssueState,
    var startTime: Long = 0L,
)
