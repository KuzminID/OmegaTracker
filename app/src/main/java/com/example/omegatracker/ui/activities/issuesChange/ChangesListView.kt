package com.example.omegatracker.ui.activities.issuesChange

import com.example.omegatracker.room.IssuesChangeList
import com.example.omegatracker.ui.activities.base.BaseView

interface ChangesListView : BaseView {
    fun setAdapterData(data: Map<String, List<IssuesChangeList>>)
}