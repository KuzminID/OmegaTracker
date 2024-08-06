package com.example.omegatracker.ui.activities.issues

import com.example.omegatracker.entity.Issue
import com.example.omegatracker.entity.Screens
import com.example.omegatracker.ui.activities.base.BaseView

interface IssuesView : BaseView {
    fun setProfileListener()
    fun showScreen(screen: Screens)

    //fun showPopUpMenu(view: View)
    fun setIssuesToRV(issues: List<Issue>)
    fun bindService()
    fun updateIssueTimer(issue: Issue)
}