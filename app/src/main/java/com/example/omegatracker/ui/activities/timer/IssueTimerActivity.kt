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

class IssueTimerActivity : BaseActivity(), IssueTimerView {

    private lateinit var binding: ActivityIssueTimerBinding

    private val timerPresenter: IssueTimerPresenter by providePresenter {
        IssueTimerPresenter()
    }

    private lateinit var issueId: String

    //Variable for check if progress bar and timer shows estimated time or spent time
    private var timerEstimatedTimeType: Boolean = true

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

        if (issueId != "") {
            getIssuesInfo(issueId)
        }

        //If back btn clicked move to previous activity
        binding.issueTimerBackBtn.setOnClickListener {
            val intent = Intent(this, IssuesActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.issueTimerStartBtn.setOnClickListener {
            timerPresenter.startIssue()
            hideBtnContent(IssuesButtons.StartBtn)
            showBtnContent(IssuesButtons.StopBtn,IssuesButtons.PauseBtn)
        }
        binding.issueTimerStopBtn.setOnClickListener {
            timerPresenter.stopIssue()
            hideBtnContent(IssuesButtons.PauseBtn,IssuesButtons.StopBtn)
            showBtnContent(IssuesButtons.StartBtn)
        }
        binding.issueTimerPauseBtn.setOnClickListener {
            timerPresenter.pauseIssue()
            hideBtnContent(IssuesButtons.PauseBtn)
            showBtnContent(IssuesButtons.StartBtn)
        }

        //Clicking on progress bar switches progress bar state to show spent time or estimated time
        binding.issueTimerProgressbar.setOnClickListener {
            timerEstimatedTimeType = !timerEstimatedTimeType
            if (issueId!="") {
                getIssuesInfo(issueId)
            }
        }
    }

    override fun getIssuesInfo(issueId: String) {
        timerPresenter.getIssueData(issueId)
    }

    override fun setIssuesInfo(issue: Issue) {
        binding.issueTimerDescription.text = issue.description
        binding.issueTimerStatus.text = getString(issue.state.stateName)
        binding.issueTimerSummary.text = issue.summary
        updateTimer(issue)
    }

    override fun updateTimer(issue: Issue) {
        val curTime =
            if (timerEstimatedTimeType) {
                issue.estimatedTime - issue.spentTime
            } else {
                issue.spentTime
            }
        updateProgressBar(issue)
        binding.issueTimerProgressbarTimerTv.text = curTime.componentsToString()
    }

    override fun updateProgressBar(issue: Issue) {
        val progress = if (timerEstimatedTimeType) {
                (issue.spentTime / issue.estimatedTime * 100).toFloat()
            } else {
                ((issue.estimatedTime - issue.spentTime)/issue.estimatedTime * 100).toFloat()
            }
        println(progress)
        if (progress < 100) {
            binding.issueTimerProgressbar.setProgress(progress)
        } else {
            binding.issueTimerProgressbar.setProgress(progress = progress)
        }
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

    override fun showBtnContent(vararg buttons: IssuesButtons) {
        buttons.forEach {
            when (it) {
                IssuesButtons.StartBtn -> {
                    binding.issueTimerStartText.isVisible = true
                    binding.issueTimerStartBtn.isVisible = true
                }

                IssuesButtons.PauseBtn -> {
                    binding.issueTimerPauseBtn.isVisible = true
                    binding.issueTimerPauseText.isVisible = true
                }

                IssuesButtons.StopBtn -> {
                    binding.issueTimerStopBtn.isVisible = true
                    binding.issueTimerStopText.isVisible = true
                }
            }
        }
    }

    override fun hideBtnContent(vararg buttons: IssuesButtons) {
        buttons.forEach {
            when (it) {
                IssuesButtons.StartBtn -> {
                    binding.issueTimerStartText.isVisible = false
                    binding.issueTimerStartBtn.isVisible = false
                }

                IssuesButtons.PauseBtn -> {
                    binding.issueTimerPauseBtn.isVisible = false
                    binding.issueTimerPauseText.isVisible = false
                }

                IssuesButtons.StopBtn -> {
                    binding.issueTimerStopBtn.isVisible = false
                    binding.issueTimerStopText.isVisible = false
                }
            }
        }
    }
}