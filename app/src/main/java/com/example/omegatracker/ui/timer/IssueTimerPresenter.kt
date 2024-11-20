package com.example.omegatracker.ui.timer

import com.example.omegatracker.OmegaTrackerApplication.Companion.appComponent
import com.example.omegatracker.entity.Issue
import com.example.omegatracker.entity.IssueButtonsAction
import com.example.omegatracker.entity.IssueState
import com.example.omegatracker.ui.base.BaseServicePresenter
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

class IssueTimerPresenter : BaseServicePresenter<IssueTimerView>() {
    private val issueRepository = appComponent.getIssueRepository()
    private val changesRepository = appComponent.getTrackingHistoryRepository()
    private lateinit var issue: Issue

//    fun setController(controller: IssuesServiceBinder) {
//        this.controller = controller
//    }

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
//        launch {
//            issue = issueRepository.getIssueByIDFromDB(issue.id)!!
//            issue.isActive = true
//            issue.state = IssueState.OnWork
//            issue.startTime = System.currentTimeMillis()
//
//            controller.startIssue(issue)
//            issueRepository.upsertIssueToDB(issue)
//            viewState.setIssuesInfo(issue)
//            observeIssueTimer()
//        }
        super.startIssue(issue)
        observeIssueTimer()
    }

    fun stopIssue() {
        super.stopIssue(issue)
    }

    fun pauseIssue() {
//        issue.isActive = false
//        issue.state = IssueState.OnPause
//        viewState.setIssuesInfo(issue)
//        launch {
//            issueRepository.upsertIssueToDB(issue)
//            changesRepository.insertChange(
//                IssuesTrackingHistory(
//                    durationTime = 0,
//                    endTime = System.currentTimeMillis(),
//                    issueSummary = issue.summary,
//                    projectName = issue.projectName,
//                    startTime = issue.startTime,
//                    time = System.currentTimeMillis()
//                )
//            )
//        }.invokeOnCompletion {
//            controller.pauseIssue(issue)
//        }
        super.pauseIssue(issue)
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