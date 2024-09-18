package com.example.omegatracker.service

import android.os.SystemClock
import android.util.Log
import com.example.omegatracker.entity.Issue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class TaskManager {

    private var runningIssues: MutableMap<String, TaskRunner> = mutableMapOf()

    fun addIssue(issue: Issue) {
        if (!runningIssues.containsKey(issue.id)) {
            runningIssues[issue.id] = TaskRunner(issue)
            startIssue(issue)
        }
    }

    private fun startIssue(issue: Issue) {
        runningIssues[issue.id]?.run()
    }

    fun stopIssue(issue: Issue) {
        if (runningIssues.containsKey(issue.id)) {
            runningIssues[issue.id]?.issue?.isActive = false
            runningIssues.remove(issue.id)
        } else {
            Log.d("Task Manager", "Task Manager does not contain this issue")
        }
    }

    fun stopRunningIssues() {
        runningIssues.forEach {
            it.value.issue.isActive = false
            runningIssues.remove(it.key)
        }
    }

    fun getIssuesUpdates(issue: Issue): Flow<Issue> {
        return runningIssues[issue.id]!!.flow
    }

    private inner class TaskRunner(val issue: Issue) {

        private val step = 1000L
        var elapsedTime: Duration = 0.milliseconds
        var issueStartSpentTime = issue.spentTime
        val flow: Flow<Issue> = run()

        fun run(): Flow<Issue> = flow {
            while (issue.isActive) {
                elapsedTime = (SystemClock.elapsedRealtime() - issue.startTime).milliseconds
                issue.spentTime = issueStartSpentTime + elapsedTime
                emit(issue)
                delay(step)
            }
        }.flowOn(Dispatchers.IO)
    }

}