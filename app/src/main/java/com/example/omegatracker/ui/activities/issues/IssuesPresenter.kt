package com.example.omegatracker.ui.activities.issues

import android.os.SystemClock
import com.example.omegatracker.OmegaTrackerApplication.Companion.appComponent
import com.example.omegatracker.entity.Issue
import com.example.omegatracker.service.IssuesServiceBinder
import com.example.omegatracker.ui.activities.base.BasePresenter
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class IssuesPresenter : BasePresenter<IssuesView>() {
    private var userRepositoryImpl = appComponent.getUserRepositoryImpl()
    private lateinit var controller: IssuesServiceBinder
    private val observableIssues = mutableMapOf<String, Job>()

    fun getIssuesList() {
        launch {
            userRepositoryImpl.getIssuesList().collect {
                checkActiveIssues(it)
                val sortedIssues = sortIssues(it)
                viewState.setIssuesToRV(sortedIssues)
            }
        }
    }

    private fun checkActiveIssues(issues: List<Issue>) {
        val activeIssues = issues.filter { it.isActive }
        if (activeIssues.isNotEmpty()) {
            restartIssues(activeIssues)
        }
    }

    fun setController(controller: IssuesServiceBinder) {
        this.controller = controller
    }

    private fun restartIssues(issues: List<Issue>) {
        issues.forEach {
            controller.startIssue(it)
            observeActiveIssueUpdate(it)
        }
    }

    fun startIssue(issue: Issue) {
        launch {
            issue.startTime = SystemClock.elapsedRealtime()
        }.invokeOnCompletion {
            launch {
                userRepositoryImpl.upsertIssueToDB(issue)
            }
            controller.startIssue(issue)
            observeActiveIssueUpdate(issue)
        }
    }

    fun stopTask(issue: Issue) {
        controller.stopIssue(issue)
        observableIssues[issue.id]?.cancel()
        observableIssues.remove(issue.id)
    }

    private fun observeActiveIssueUpdate(issue: Issue) {
        observableIssues[issue.id] = launch {
            controller.getResults(issue).collect { updatedIssue ->
                viewState.updateIssueTimer(updatedIssue)
            }
        }
    }

    fun sortIssues(issues: List<Issue>, position: Int) {
        issues[position].isActive = true
        val sortedIssuesList = issues.sortedByDescending { it.isActive }
        viewState.setIssuesToRV(sortedIssuesList)
    }

    private fun sortIssues(issues: List<Issue>): List<Issue> {
        return issues.sortedByDescending { it.isActive }
    }
}