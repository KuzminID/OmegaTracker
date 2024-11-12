package com.example.omegatracker.ui.issues

import com.example.omegatracker.OmegaTrackerApplication.Companion.appComponent
import com.example.omegatracker.entity.Issue
import com.example.omegatracker.entity.IssuesFilterType
import com.example.omegatracker.service.IssuesServiceBinder
import com.example.omegatracker.ui.base.BaseServicePresenter
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch



class IssuesFragmentPresenter : BaseServicePresenter<IssuesFragmentView>() {

    private val userRepositoryImpl = appComponent.getIssueRepository()

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
            launch {
                activeIssues.forEach { issue ->
                    lateStartIssue(issue)
                }
                _isControllerInit.collect{
                    println("Это было собрано $it")
                    if (it) {
                        println(observableIssuesList)
                        activeIssues.forEach{entry->
                            observeIssueUpdates(entry)
                        }
                    }
                }
            }
        }
    }

    private fun observeIssueUpdates(issue : Issue) {
        launch {
            controller.getResults(issue).collect{
                viewState.updateIssueTimer(it)
            }
        }
    }

    public override fun startIssue(issue : Issue) {
        super.startIssue(issue)
        launch {
            observableIssuesList[issue]?.collect() {
                viewState.updateIssueTimer(it)
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