package com.example.omegatracker.ui.activities.issues

import com.example.omegatracker.entity.Issue
import com.example.omegatracker.entity.IssuesFilterType
import com.example.omegatracker.entity.Screens
import com.example.omegatracker.ui.activities.baseService.BaseServiceView

interface IssuesView : BaseServiceView {
    fun setProfileListener()
    fun showScreen(screen: Screens)

    fun setFilterData(data: List<IssuesFilterType>)
    fun setIssuesToRV(issues: List<Issue>)
    fun updateIssueTimer(issue: Issue)
}