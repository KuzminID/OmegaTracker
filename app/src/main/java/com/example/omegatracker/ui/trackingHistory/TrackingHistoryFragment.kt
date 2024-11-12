package com.example.omegatracker.ui.trackingHistory

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.omegatracker.R
import com.example.omegatracker.databinding.FragmentTrackingHistoryBinding
import com.example.omegatracker.room.IssueAndHistory
import com.example.omegatracker.room.IssueEntity
import com.example.omegatracker.room.IssuesTrackingHistory
import com.example.omegatracker.ui.base.BaseFragment

interface TrackingHistoryCallback {
    fun formatTimeToHMS(time: Long?): String
    fun scrollToPosition(id : Long)
}

class TrackingHistoryFragment : BaseFragment(), TrackingHistoryFragmentView,
    TrackingHistoryCallback {

    private val presenter: TrackingHistoryFragmentPresenter by providePresenter {
        TrackingHistoryFragmentPresenter()
    }

    private lateinit var adapter : TrackingHistoryAdapter

    private lateinit var binding: FragmentTrackingHistoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_tracking_history, container, false)
        binding = FragmentTrackingHistoryBinding.bind(view)
        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.getTrackingHistory()

        binding.issueTimerBackBtn.setOnClickListener {
            navigateUp()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        presenter.getTrackingHistory()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun setAdapterData(trackingHistory: List<IssueAndHistory>) {
       // adapter = TrackingHistoryAdapter(this,data.first,data.second)
        adapter = TrackingHistoryAdapter(this,trackingHistory)
        binding.changesRv.adapter = adapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun formatTimeToHMS(time: Long?): String {
        return presenter.formatTimeToHMS(time)
    }

    override fun scrollToPosition(id: Long) {
        binding.changesRv.smoothScrollToPosition(id.toInt())
    }
}