package com.example.omegatracker.service

import android.util.Log
import com.example.omegatracker.entity.Issue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.milliseconds

class TaskManager {

    //private var runningTasks: MutableMap<Issue, TaskRunner> = mutableMapOf()
    private var runningTasks : ConcurrentHashMap<Issue,TaskRunner> = ConcurrentHashMap()

    fun addIssue(issue: Issue) {
        runningTasks.getOrPut(issue) {TaskRunner(issue)}
        startIssue(issue)
    }

    private fun startIssue(issue : Issue) {
        runningTasks.getOrPut(issue) {TaskRunner(issue)}
            .run()
    }

    fun stopIssue(issue: Issue) {
        if (runningTasks.contains(TaskRunner(issue))) {
            runningTasks[issue]?.issue?.isActive = false
            runningTasks.remove(issue)
        } else {
            Log.d("TaskManager", "This issue is not running")
        }
    }

    fun stopRunningTasks() {
        runningTasks.forEach {
            it.value.issue.isActive = false
            runningTasks.remove(it.key)
        }
    }

    fun getIssuesUpdates(issue: Issue): Flow<Issue> {
        return runningTasks.getOrPut(issue) {TaskRunner(issue)}
            .run()
    }

    private inner class TaskRunner(val issue: Issue) {
        val step = 1000.milliseconds

        fun run() : Flow<Issue> = flow {
            Log.d("Task Runner"," for ${issue.summary} is running")
                while (issue.isActive) {
                    /*Время сервера, а не локальное время после запуска приложения*/
                    issue.spentTime = (System.currentTimeMillis() - issue.startTime).milliseconds
                    emit(issue)
                    delay(step)
                }
        }.flowOn(Dispatchers.Default)
    }

}