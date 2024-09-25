package com.example.omegatracker.ui.activities.issuesChange

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.example.omegatracker.databinding.ActivityChangesListBinding
import com.example.omegatracker.room.IssuesChangeList
import com.example.omegatracker.ui.activities.base.BaseActivity

interface ChangesActivityCallback {
    fun formatTimeToHMS(time: Long?): String
}

class ChangesListActivity : BaseActivity(), ChangesListView, ChangesActivityCallback {

    private val presenter: ChangesListPresenter by providePresenter {
        ChangesListPresenter()
    }

    private val adapter = ChangesListAdapter()

    private lateinit var binding: ActivityChangesListBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangesListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialization()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initialization() {
        binding.changesRv.adapter = adapter
        adapter.callback = this
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