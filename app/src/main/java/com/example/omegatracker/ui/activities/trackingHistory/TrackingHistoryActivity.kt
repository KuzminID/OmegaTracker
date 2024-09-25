package com.example.omegatracker.ui.activities.trackingHistory

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.example.omegatracker.databinding.ActivityTrackingHistoryBinding
import com.example.omegatracker.room.IssuesChangeList
import com.example.omegatracker.ui.activities.base.BaseActivity

interface ChangesActivityCallback {
    fun formatTimeToHMS(time: Long?): String
}

class ChangesListActivity : BaseActivity(), TrackingHistoryView, ChangesActivityCallback {

    private val presenter: TrackingHistoryPresenter by providePresenter {
        TrackingHistoryPresenter()
    }

    private val adapter = TrackingHistoryAdapter(this)

    private lateinit var binding: ActivityTrackingHistoryBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackingHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialization()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initialization() {
        binding.changesRv.adapter = adapter
        presenter.getChangesList()

        binding.issueTimerBackBtn.setOnClickListener {
            finish()
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