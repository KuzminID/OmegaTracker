package com.example.omegatracker.ui.trackingHistory

import com.example.omegatracker.room.IssuesChangeList
import com.example.omegatracker.ui.base.BaseFragmentView

interface TrackingHistoryFragmentView : BaseFragmentView {
    fun setAdapterData(data: Map<String, List<IssuesChangeList>>)
}