package com.example.omegatracker.ui.activities.timer

import android.os.SystemClock
import com.example.omegatracker.OmegaTrackerApplication.Companion.appComponent
import com.example.omegatracker.entity.Issue
import com.example.omegatracker.entity.IssueState
import com.example.omegatracker.entity.IssueButtonsAction
import com.example.omegatracker.service.IssuesServiceBinder
import com.example.omegatracker.ui.activities.base.BasePresenter
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

class IssueTimerPresenter : BasePresenter<IssueTimerView>() {
    private val repository = appComponent.getUserRepositoryImpl()
    private lateinit var controller: IssuesServiceBinder
    private lateinit var issue: Issue

    fun setController(controller: IssuesServiceBinder) {
        this.controller = controller
    }

    private fun observeIssueTimer() {
        launch {
            controller.getResults(issue).collect {
                issue = it
                viewState.updateTimer(issue)
                viewState.updateProgressBar(issue)
            }
        }
    }

    fun startIssue() {
        issue.isActive = true
        issue.state = IssueState.OnWork
        issue.startTime = SystemClock.elapsedRealtime()

        controller.startIssue(issue)
        launch {
            repository.upsertIssueToDB(issue)
        }
        viewState.setIssuesInfo(issue)
        observeIssueTimer()
    }

    fun stopIssue() {
        issue.isActive = false
        issue.state = IssueState.OnStop
        viewState.setIssuesInfo(issue)
        launch {
            repository.upsertIssueToDB(issue)
        }.invokeOnCompletion {
            controller.stopIssue(issue)
        }
    }

    fun pauseIssue() {
        issue.isActive = false
        issue.state = IssueState.OnPause
        viewState.setIssuesInfo(issue)
        launch {
            repository.upsertIssueToDB(issue)
        }.invokeOnCompletion {
            controller.pauseIssue(issue)
        }
    }


    fun getIssueData(issueId: String) {
        launch {
            issue = repository.getIssueByIDFromDB(issueId) ?: Issue(
                "0-0",
                "",
                "",
                0.minutes,
                0.minutes,
                "",
                "",
                false,
                IssueState.Open,
                0,
                created = 0
            )
            if (issue.isActive) {
                viewState.actionChanged(IssueButtonsAction.StartAction)
                observeIssueTimer()
            }
            viewState.setIssuesInfo(issue)
        }
    }
}