package com.example.omegatracker.ui.timer

import android.os.SystemClock
import com.example.omegatracker.OmegaTrackerApplication.Companion.appComponent
import com.example.omegatracker.entity.Issue
import com.example.omegatracker.entity.IssueButtonsAction
import com.example.omegatracker.entity.IssueState
import com.example.omegatracker.room.IssuesChangeList
import com.example.omegatracker.service.IssuesServiceBinder
import com.example.omegatracker.ui.base.BasePresenter
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

class IssueTimerPresenter : BasePresenter<IssueTimerView>() {
    private val issueRepository = appComponent.getIssueRepository()
    private val changesRepository = appComponent.getChangeListRepository()
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
            issueRepository.upsertIssueToDB(issue)
        }
        viewState.setIssuesInfo(issue)
        observeIssueTimer()
    }

    fun stopIssue() {
        issue.isActive = false
        issue.state = IssueState.OnStop
        viewState.setIssuesInfo(issue)
        launch {
            issueRepository.upsertIssueToDB(issue)

            changesRepository.insertChange(
                IssuesChangeList(
                    durationTime = 0,
                    endTime = System.currentTimeMillis(),
                    issueSummary = issue.summary,
                    projectName = issue.projectName,
                    startTime = issue.startTime,
                    time = System.currentTimeMillis()
                )
            )
        }.invokeOnCompletion {
            controller.stopIssue(issue)
        }
    }

    fun pauseIssue() {
        issue.isActive = false
        issue.state = IssueState.OnPause
        viewState.setIssuesInfo(issue)
        launch {
            issueRepository.upsertIssueToDB(issue)
            changesRepository.insertChange(
                IssuesChangeList(
                    durationTime = 0,
                    endTime = System.currentTimeMillis(),
                    issueSummary = issue.summary,
                    projectName = issue.projectName,
                    startTime = issue.startTime,
                    time = System.currentTimeMillis()
                )
            )
        }.invokeOnCompletion {
            controller.pauseIssue(issue)
        }
    }


    fun getIssueData(issueId: String) {
        launch {
            issue = issueRepository.getIssueByIDFromDB(issueId) ?: Issue(
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