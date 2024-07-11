package com.example.omegatracker.ui.activities.timer

import com.example.omegatracker.OmegaTrackerApplication.Companion.appComponent
import com.example.omegatracker.entity.Issue
import com.example.omegatracker.entity.IssueState
import com.example.omegatracker.service.IssuesServiceBinder
import com.example.omegatracker.ui.activities.base.BasePresenter
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

class IssueTimerPresenter : BasePresenter<IssueTimerView>() {
    private val repository = appComponent.getUserRepositoryImpl()
    private lateinit var controller: IssuesServiceBinder
    private lateinit var issue : Issue

    fun setController(controller : IssuesServiceBinder) {
        this.controller = controller
    }

    fun observeIssueTimer() {
        launch {
            controller.getResults(issue).collect {
                viewState.updateTimer(it)
            }
        }
    }

    fun startIssue() {
        issue.isActive = true
        issue.startTime = System.currentTimeMillis()
        controller.startIssue(issue)
        launch {
            repository.upsertIssueToDB(issue)
        }
        issue.state = IssueState.OnWork
        viewState.setIssuesInfo(issue)
        observeIssueTimer()
    }

    fun stopIssue() {
        issue.isActive = false
        issue.state = IssueState.OnStop
        viewState.setIssuesInfo(issue)
        launch {
            issue.updateTime = repository.getServerTime().unixtime *1000L
            repository.upsertIssueToDB(issue)
        }
        controller.stopIssue(issue)
    }

    fun pauseIssue() {
        issue.isActive = false
        issue.state = IssueState.OnPause
        viewState.setIssuesInfo(issue)
        launch {
            repository.upsertIssueToDB(issue)
        }
        controller.pauseIssue(issue)
    }

    fun getIssueData(issueId : String) {
        launch {
            issue = repository.getIssueByIDFromDB(issueId) ?: Issue("0-0","","" , 0.minutes,0.minutes,"","",false,IssueState.Open,0,0)
            if (issue.isActive) {
                viewState.hideStartBtn()
                viewState.showStopBtn()
                viewState.showPauseBtn()
                observeIssueTimer()
            }
            viewState.setIssuesInfo(issue)
        }
    }
}