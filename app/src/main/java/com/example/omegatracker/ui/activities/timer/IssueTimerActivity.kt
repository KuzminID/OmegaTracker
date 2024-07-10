package com.example.omegatracker.ui.activities.timer

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.omegatracker.data.componentsToString
import com.example.omegatracker.databinding.ActivityIssueTimerBinding
import com.example.omegatracker.entity.Issue
import com.example.omegatracker.service.IssuesService
import com.example.omegatracker.service.IssuesServiceBinder
import com.example.omegatracker.ui.activities.base.BaseActivity
import com.example.omegatracker.ui.activities.issues.IssuesActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class IssueTimerActivity : BaseActivity(),IssueTimerView {

    private lateinit var binding : ActivityIssueTimerBinding

    private val timerPresenter : IssueTimerPresenter by providePresenter {
        IssueTimerPresenter()
    }

    private lateinit var issueId : String

    private val _serviceControllerState = MutableStateFlow<IssuesServiceBinder?>(null)

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            service as IssuesServiceBinder
            _serviceControllerState.value = service
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            _serviceControllerState.value = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIssueTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialization()
    }

    override fun initialization() {
        bindService()

        issueId = intent.getStringExtra("issue_id") ?: ""

        Log.d("Issue ID", issueId)

        if (issueId != "") {
            getIssuesInfo(issueId)
        }

        binding.issueTimerBackBtn.setOnClickListener {
            val intent = Intent(this, IssuesActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.issueTimerStartBtn.setOnClickListener {
            timerPresenter.startIssue()
            hideStartBtn()
            showPauseBtn()
            showStopBtn()
        }
        binding.issueTimerStopBtn.setOnClickListener {
            timerPresenter.stopIssue()
            showStartBtn()
            hideStopBtn()
            hidePauseBtn()
        }
        binding.issueTimerPauseBtn.setOnClickListener {
            timerPresenter.pauseIssue()
            showStartBtn()
            hidePauseBtn()
        }
    }

    override fun getIssuesInfo(issueId : String) {
        val data = timerPresenter.getIssueData(issueId)
    }

    override fun setIssuesInfo(issue: Issue) {
        binding.issueTimerDescription.text = issue.description
        binding.issueTimerStatus.text = getString(issue.state.stateName)
        binding.issueTimerSummary.text = issue.summary
        updateTimer(issue)
    }

    override fun updateTimer(issue: Issue) {
        val curTime = issue.estimatedTime - issue.spentTime
        val hours = curTime.inWholeHours
        val minutes = curTime.inWholeMinutes.toInt() % 60
        val seconds = curTime.inWholeSeconds.toInt() % 60
        binding.issueTimerProgressbarTimerTv.text = "$hours:$minutes:$seconds"
    }

    override fun bindService() {
        val intent = Intent(this, IssuesService::class.java)
        bindService(
            intent,
            connection,
            BIND_AUTO_CREATE
        )

        lifecycleScope.launch {
            _serviceControllerState.collect { controller ->
                if (controller != null) {
                    timerPresenter.setController(controller)
                }
            }
        }
    }

    override fun showStartBtn() {
        binding.issueTimerStartBtn.visibility = View.VISIBLE
        binding.issueTimerStartText.visibility = View.VISIBLE
        binding.issueTimerStartText.isVisible = true
    }

    override fun showStopBtn() {
        binding.issueTimerStopBtn.visibility = View.VISIBLE
        binding.issueTimerStopText.visibility = View.VISIBLE
    }

    override fun showPauseBtn() {
        binding.issueTimerPauseBtn.visibility = View.VISIBLE
        binding.issueTimerPauseText.visibility = View.VISIBLE
    }

    override fun hideStartBtn() {
        binding.issueTimerStartBtn.visibility = View.GONE
        binding.issueTimerStartText.visibility = View.GONE
    }

    override fun hideStopBtn() {
        binding.issueTimerStopBtn.visibility = View.GONE
        binding.issueTimerStopText.visibility = View.GONE
    }

    override fun hidePauseBtn() {
        binding.issueTimerPauseBtn.visibility = View.GONE
        binding.issueTimerPauseText.visibility = View.GONE
    }
}