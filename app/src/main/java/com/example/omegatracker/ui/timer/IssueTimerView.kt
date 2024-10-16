package com.example.omegatracker.ui.timer

import com.example.omegatracker.entity.Issue
import com.example.omegatracker.entity.IssueButtonsAction
import com.example.omegatracker.ui.base.BaseView

interface IssueTimerView : BaseView {
    fun getIssuesInfo(issueId: String)
    fun setIssuesInfo(issue: Issue)
    fun updateTimer(issue: Issue)

    fun updateProgressBar(issue: Issue)
    fun bindService()

    fun initialization()

    fun actionChanged(action: IssueButtonsAction)
}