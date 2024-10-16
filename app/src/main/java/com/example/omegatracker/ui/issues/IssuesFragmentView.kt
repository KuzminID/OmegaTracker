package com.example.omegatracker.ui.issues

import com.example.omegatracker.entity.Issue
import com.example.omegatracker.entity.IssuesFilterType
import com.example.omegatracker.ui.base.BaseFragmentView

interface IssuesFragmentView : BaseFragmentView {
    fun setFilterData(data: List<IssuesFilterType>)
    fun setIssuesToRV(issues: List<Issue>)
    fun updateIssueTimer(issue: Issue)
}