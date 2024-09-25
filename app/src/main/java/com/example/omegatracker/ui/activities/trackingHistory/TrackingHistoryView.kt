package com.example.omegatracker.ui.activities.trackingHistory

import com.example.omegatracker.room.IssuesChangeList
import com.example.omegatracker.ui.activities.base.BaseView

interface TrackingHistoryView : BaseView {
    fun setAdapterData(data: Map<String, List<IssuesChangeList>>)
}