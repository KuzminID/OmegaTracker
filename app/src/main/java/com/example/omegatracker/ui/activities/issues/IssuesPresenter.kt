package com.example.omegatracker.ui.activities.issues

import com.example.omegatracker.OmegaTrackerApplication.Companion.appComponent
import com.example.omegatracker.entity.Issue
import com.example.omegatracker.entity.IssuesFilterType
import com.example.omegatracker.ui.activities.baseService.BaseServicePresenter
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class IssuesPresenter : BaseServicePresenter<IssuesView>() {
    private var userRepositoryImpl = appComponent.getUserRepository()
    private val observableIssues = mutableMapOf<String, Job>()

    fun getIssuesList() {
        launch {
            try {


                viewState.setFilterData(userRepositoryImpl.getIssuesHeaderData())
                userRepositoryImpl.getIssuesList().collect {
                    checkActiveIssues(it)
                    val sortedIssues = sortIssues(it)
                    viewState.setIssuesToRV(sortedIssues)
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }

    private fun checkActiveIssues(issues: List<Issue>) {
        val activeIssues = issues.filter { it.isActive }
        if (activeIssues.isNotEmpty()) {
            restartIssues(activeIssues)
        }
    }

//    fun setController(controller: IssuesServiceBinder) {
//        this.controller = controller
//    }

    private fun restartIssues(issues: List<Issue>) {
        issues.forEach {
            controller?.startIssue(it)
            observeActiveIssueUpdate(it)
        }
    }

    fun startIssue(issue: Issue) {
        launch {
            issue.startTime = System.currentTimeMillis()
        }.invokeOnCompletion {
            launch {
                userRepositoryImpl.upsertIssueToDB(issue)
            }
            println(controller)
            println(1)
            println(2)
            println(3)
            controller?.startIssue(issue)
            observeActiveIssueUpdate(issue)
        }
    }

    private fun observeActiveIssueUpdate(issue: Issue) {
        observableIssues[issue.id] = launch {
            controller?.getResults(issue)?.collect { updatedIssue ->
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