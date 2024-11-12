package com.example.omegatracker.ui.base

import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.OmegaTrackerApplication.Companion.appComponent
import com.example.omegatracker.entity.Issue
import com.example.omegatracker.entity.IssueBackstack
import com.example.omegatracker.entity.IssueState
import com.example.omegatracker.entity.IssuesActions
import com.example.omegatracker.room.IssuesTrackingHistory
import com.example.omegatracker.service.IssuesServiceBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

open class BaseServicePresenter<V : BaseView> : BasePresenter<V>() {
    protected val _isControllerInit = MutableStateFlow(false)
    private var isControllerInit = false
    protected open lateinit var controller: IssuesServiceBinder
    private var issueBackstack: MutableList<IssueBackstack> = mutableListOf()

    protected val observableIssuesList : MutableMap<Issue,Flow<Issue>> = mutableMapOf()

    private val issueRepository = OmegaTrackerApplication.appComponent.getIssueRepository()

    private val trackingHistoryRepos = OmegaTrackerApplication.appComponent.getTrackingHistoryRepository()

    fun attachController(controller: IssuesServiceBinder) {
        this.controller = controller
        isControllerInit = true
        onControllerInit()
    }

    fun detachController() {
        isControllerInit = false
    }

    protected fun onControllerInit() {
        issueBackstack.forEach {
            when (it.action) {
                IssuesActions.START -> lateStartIssue(it.issue)
                IssuesActions.PAUSE -> pauseIssue(it.issue)
                IssuesActions.STOP -> stopIssue(it.issue)
            }
            _isControllerInit.value = true
        }
        issueBackstack = mutableListOf()
    }

    protected open fun lateStartIssue(issue: Issue) {
        if (isControllerInit) {
            launch {
                issue.isActive = true
                issueRepository.upsertIssueToDB(issue)
                controller.startIssue(issue)
            }
        } else {
            issueBackstack.add(IssueBackstack(issue, IssuesActions.START))
        }
    }

    protected open fun startIssue(issue: Issue) {
        issue.startTime = System.currentTimeMillis()
        if (isControllerInit) {
            launch {
                issue.isActive=true
                controller.startIssue(issue)
                observableIssuesList[issue] = controller.getResults(issue)
                issueRepository.upsertIssueToDB(issue)
            }
        } else {
            issueBackstack.add(IssueBackstack(issue,IssuesActions.START))
        }
    }

    protected open fun pauseIssue(issue: Issue) {
        if (isControllerInit) {
            launch {
                launch(Dispatchers.IO) {
                    issue.isActive = false
                    issue.state = IssueState.OnPause
                    issueRepository.upsertIssueToDB(issue)
                    appComponent.getHistoryDao().insertData( IssuesTrackingHistory(
                        issueId = issue.id,
                        endTime = System.currentTimeMillis(),
                        issueStartTime = issue.startTime,
                        historyGroup = convertMillisToStringDate(issue.startTime)
                    ))
//                    trackingHistoryRepos.insertChange(
//                        IssuesTrackingHistory(
//                            durationTime = 0,
//                            issueId = issue.id,
//                            endTime = System.currentTimeMillis(),
//                            issueStartTime = issue.startTime,
//                        )
//                    )
                }.invokeOnCompletion {
                    controller.pauseIssue(issue)
                    if (observableIssuesList.containsKey(issue)) {
                        observableIssuesList.remove(issue)
                    }
                }
            }
        }
    }

    private fun convertMillisToStringDate(time : Long) : String {
        val date = java.util.Date(time)
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    protected open fun stopIssue(issue: Issue) {
        if (isControllerInit) {
                CoroutineScope(Dispatchers.IO).launch {
                    issue.isActive = false
                    issue.state = IssueState.OnStop

                    issueRepository.upsertIssueToDB(issue)

                    trackingHistoryRepos.insertChange(
                        IssuesTrackingHistory(
                            issueId = issue.id,
                            endTime = System.currentTimeMillis(),
                            issueStartTime = issue.startTime,
                            historyGroup = convertMillisToStringDate(issue.startTime)
                        )
                    )

                }.invokeOnCompletion {
                    controller.stopIssue(issue)
                    if (observableIssuesList.containsKey(issue)) {
                        observableIssuesList.remove(issue)
                    }
                }
            }
    }


}