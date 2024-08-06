package com.example.omegatracker.ui.activities.timer

import com.example.omegatracker.entity.Issue
import com.example.omegatracker.ui.activities.base.BaseView

interface IssueTimerView : BaseView {
    fun getIssuesInfo(issueId: String)
    fun setIssuesInfo(issue: Issue)
    fun updateTimer(issue: Issue)

    fun updateProgressBar(issue: Issue)
    fun bindService()
    fun showStartBtn()
    fun showStopBtn()
    fun showPauseBtn()
    fun hideStartBtn()
    fun hideStopBtn()
    fun hidePauseBtn()
}