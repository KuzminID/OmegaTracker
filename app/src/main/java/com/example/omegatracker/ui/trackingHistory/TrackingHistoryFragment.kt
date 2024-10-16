package com.example.omegatracker.ui.trackingHistory

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.omegatracker.R
import com.example.omegatracker.databinding.FragmentTrackingHistoryBinding
import com.example.omegatracker.room.IssuesChangeList
import com.example.omegatracker.ui.base.BaseFragment
import kotlinx.coroutines.delay

interface TrackingHistoryCallback {
    fun formatTimeToHMS(time: Long?): String
}

class TrackingHistoryFragment : BaseFragment(), TrackingHistoryFragmentView,
    TrackingHistoryCallback {

    private val presenter: TrackingHistoryFragmentPresenter by providePresenter {
        TrackingHistoryFragmentPresenter()
    }

    private val adapter = TrackingHistoryAdapter(this)

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
        binding.changesRv.adapter = adapter
        presenter.getChangesList()

        binding.issueTimerBackBtn.setOnClickListener {
            navigateUp()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        presenter.getChangesList()
    }

    override fun setAdapterData(data: Map<String, List<IssuesChangeList>>) {
        adapter.items = data
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun formatTimeToHMS(time: Long?): String {
        return presenter.formatTimeToHMS(time)
    }
}