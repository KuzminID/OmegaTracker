package com.example.omegatracker.service

import android.os.SystemClock
import android.util.Log
import com.example.omegatracker.entity.Issue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

class TaskManager {

    //private var runningTasks: MutableMap<Issue, TaskRunner> = mutableMapOf()
    private var runningIssues: ConcurrentHashMap<Issue, TaskRunner> = ConcurrentHashMap()

    fun addIssue(issue: Issue) {
        runningIssues.getOrPut(issue) { TaskRunner(issue) }
        startIssue(issue)
    }

    private fun startIssue(issue: Issue) {
        runningIssues.getOrPut(issue) { TaskRunner(issue) }
            .run()
    }

    fun stopIssue(issue: Issue) {
        if (runningIssues.contains(TaskRunner(issue))) {
            runningIssues[issue]?.issue?.isActive = false
            runningIssues.remove(issue)
        } else {
            Log.d("TaskManager", "This issue is not running")
        }
    }

    fun stopRunningIssues() {
        runningIssues.forEach {
            it.value.issue.isActive = false
            runningIssues.remove(it.key)
        }
    }

    fun getIssuesUpdates(issue: Issue): Flow<Issue> {
        return runningIssues.getOrPut(issue) { TaskRunner(issue) }
            .run()
    }

    private inner class TaskRunner(val issue: Issue) {

        private val step = 1000.milliseconds

        fun run(): Flow<Issue> = flow {
            Log.d("Task Runner", " for ${issue.summary} is running")
            var elapsedTime: Duration
            var issueStartSpentTime = issue.spentTime
            while (issue.isActive) {
                /*Время сервера, а не локальное время после запуска приложения*/
                elapsedTime = (SystemClock.elapsedRealtime() - issue.startTime).milliseconds
                issue.spentTime = issueStartSpentTime + elapsedTime
                emit(issue)
                delay(step)
            }
        }.flowOn(Dispatchers.Default)
    }

}