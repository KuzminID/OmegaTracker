package com.example.omegatracker.ui.timer

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.example.omegatracker.R
import com.example.omegatracker.data.componentsToString
import com.example.omegatracker.databinding.ActivityIssueTimerBinding
import com.example.omegatracker.entity.Issue
import com.example.omegatracker.entity.IssueButtonsAction
import com.example.omegatracker.service.IssuesService
import com.example.omegatracker.service.IssuesServiceBinder
import com.example.omegatracker.ui.base.BaseActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class IssueStoppedDialogFragment() : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_issue_stopped_dialog,container,false)
    }

    companion object {
        fun showDialog(fragmentManager: FragmentManager) {
            val dialog = IssueStoppedDialogFragment()
            dialog.show(fragmentManager,"dialog")
        }
    }
}

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
        bindService()
        initialization()
    }

    override fun initialization() {
        issueId = intent.getStringExtra("issue_id") ?: ""

        if (issueId != "") {
            getIssuesInfo(issueId)
        }

        //If back btn clicked move to previous activity
        binding.issueTimerBackBtn.setOnClickListener {
            finish()
        }

        binding.issueTimerStartBtn.setOnClickListener {
            timerPresenter.startIssue()
            actionChanged(IssueButtonsAction.StartAction)
        }
        binding.issueTimerStopBtn.setOnClickListener {
            IssueStoppedDialogFragment.showDialog(supportFragmentManager)
            timerPresenter.stopIssue()
            actionChanged(IssueButtonsAction.StopAction)
        }
        binding.issueTimerPauseBtn.setOnClickListener {
            timerPresenter.pauseIssue()
            actionChanged(IssueButtonsAction.PauseAction)
        }

        //Clicking on progress bar switches progress bar state to show spent time or estimated time
        binding.issueTimerProgressbar.setOnClickListener {
            timerEstimatedTimeType = !timerEstimatedTimeType
            if (issueId != "") {
                getIssuesInfo(issueId)
            }
        }
    }

    override fun getIssuesInfo(issueId: String) {
        timerPresenter.getIssueData(issueId)
    }

    override fun setIssuesInfo(issue: Issue) {
        binding.issuesBottomSheetToolbar.descriptionContent.text = issue.description
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
            ((issue.estimatedTime - issue.spentTime) / issue.estimatedTime * 100).toFloat()
        }
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
                    timerPresenter.attachController(controller)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        println("Is new intent")
        super.onNewIntent(intent)

        initialization()
    }

    override fun actionChanged(action: IssueButtonsAction) {
        when (action) {

            IssueButtonsAction.StartAction -> {

                binding.issueTimerStartText.isVisible = false
                binding.issueTimerStartBtn.isVisible = false

                binding.issueTimerPauseBtn.isVisible = true
                binding.issueTimerPauseText.isVisible = true

                binding.issueTimerStopBtn.isVisible = true
                binding.issueTimerStopText.isVisible = true
            }

            IssueButtonsAction.PauseAction -> {

                binding.issueTimerStartText.isVisible = true
                binding.issueTimerStartBtn.isVisible = true

                binding.issueTimerPauseBtn.isVisible = false
                binding.issueTimerPauseText.isVisible = false

                binding.issueTimerStopBtn.isVisible = true
                binding.issueTimerStopText.isVisible = true
            }

            IssueButtonsAction.StopAction -> {

                binding.issueTimerStartText.isVisible = true
                binding.issueTimerStartBtn.isVisible = true

                binding.issueTimerPauseBtn.isVisible = false
                binding.issueTimerPauseText.isVisible = false

                binding.issueTimerStopBtn.isVisible = false
                binding.issueTimerStopText.isVisible = false
            }
        }
    }
}