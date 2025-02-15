package com.example.omegatracker.ui.trackingHistory

import com.example.omegatracker.room.IssueAndHistory
import com.example.omegatracker.ui.base.BaseFragmentView

interface TrackingHistoryFragmentView : BaseFragmentView {
    fun setAdapterData(trackingHistory: List<IssueAndHistory>)
}