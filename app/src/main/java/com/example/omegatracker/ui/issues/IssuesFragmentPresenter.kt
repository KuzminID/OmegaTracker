package com.example.omegatracker.ui.issues

import com.example.omegatracker.OmegaTrackerApplication.Companion.appComponent
import com.example.omegatracker.entity.Issue
import com.example.omegatracker.entity.IssuesFilterType
import com.example.omegatracker.service.IssuesServiceBinder
import com.example.omegatracker.ui.base.BasePresenter
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class IssuesFragmentPresenter : BasePresenter<IssuesFragmentView>() {

    private val userRepositoryImpl = appComponent.getIssueRepository()
    private val observableIssues = mutableMapOf<String, Job>()
    private lateinit var controller: IssuesServiceBinder

    fun setController(controller: IssuesServiceBinder) {
        this.controller = controller
    }

    fun getIssuesList() {
        launch {
            try {
                viewState.setFilterData(userRepositoryImpl.getIssuesHeaderData())
                userRepositoryImpl.getIssuesList().collect {
                    //checkActiveIssues(it)
                    val sortedIssues = sortIssues(it)
                    viewState.setIssuesToRV(sortedIssues)
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }

    fun startIssue(issue: Issue) {
        launch {
            issue.startTime = System.currentTimeMillis()
        }.invokeOnCompletion {
            launch {
                userRepositoryImpl.upsertIssueToDB(issue)
            }
            controller.startIssue(issue)
            observeActiveIssueUpdate(issue)
        }
    }

    private fun checkActiveIssues(issues: List<Issue>) {
        val activeIssues = issues.filter { it.isActive }
        if (activeIssues.isNotEmpty()) {
            restartIssues(activeIssues)
        }
    }

    private fun restartIssues(issues: List<Issue>) {
        issues.forEach {
            controller.startIssue(it)
            observeActiveIssueUpdate(it)
        }
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

    fun checkIssuesChanged() {
        launch {
            val issues = sortIssues(userRepositoryImpl.getAllIssuesFromDB())
            checkActiveIssues(issues)
            viewState.setIssuesToRV(issues)
        }
    }

    fun filterIssuesByType(filterType: IssuesFilterType, issues: List<Issue>): List<Issue> {
        return when (filterType) {
            IssuesFilterType.All -> {
                issues
            }

            IssuesFilterType.Today -> {
                issues.filter {
                    userRepositoryImpl.checkIsTimeToday(it.created) || it.isActive
                }
            }
        }
    }

}