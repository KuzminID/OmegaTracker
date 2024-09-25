package com.example.omegatracker.ui.activities.issues

import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.omegatracker.OmegaTrackerApplication.Companion.appComponent
import com.example.omegatracker.databinding.ActivityIssuesBinding
import com.example.omegatracker.entity.Issue
import com.example.omegatracker.entity.IssuesFilterType
import com.example.omegatracker.entity.Screens
import com.example.omegatracker.ui.activities.auth.AuthActivity
import com.example.omegatracker.ui.activities.baseService.BaseServiceActivity
import com.example.omegatracker.ui.activities.trackingHistory.ChangesListActivity
import com.example.omegatracker.ui.activities.profile.ProfileActivity
import com.example.omegatracker.ui.activities.start.StartScreenActivity
import com.example.omegatracker.ui.activities.timer.IssueTimerActivity
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

interface IssuesCallback {
    fun startIssue(issueEntities: List<Issue>, position: Int)
    fun showIssueInfoActivity(issueEntity: Issue)

    fun filterIssuesByType(filterType: IssuesFilterType, issues: List<Issue>): List<Issue>
}

class IssuesActivity : BaseServiceActivity(), IssuesView, IssuesCallback {
    private lateinit var binding: ActivityIssuesBinding
    private val issuesPresenter: IssuesPresenter by providePresenter {
        IssuesPresenter()
    }

    private val userManager = appComponent.getUserManager()
    private val adapter = IssuesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIssuesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialization()
    }

    override fun onResume() {
        super.onResume()
        issuesPresenter.checkIssuesChanged()
    }

    override fun initialization() {
        binding.loadingView.isVisible = true
        lifecycleScope.launch{
            super.serviceControllerState.collect() {controller ->
                if (controller != null) {
                    issuesPresenter.setController(controller)
                    this.cancel()
                }
            }
        }.invokeOnCompletion {
            binding.loadingView.isVisible=false
        }

        setProfileListener()
        setAvatar(userManager.getUser()?.avatarUrl, binding.userAvatar)
        binding.rvIssuesList.adapter = adapter
        issuesPresenter.getIssuesList()

        binding.bottomNavBar.buttonChangesList.setOnClickListener {
            val intent = Intent(this, ChangesListActivity::class.java)
            startActivity(intent)
        }
        println(super.printThis())
    }

    override fun updateIssueTimer(issue: Issue) {
        adapter.onIssueTimerUpdated(issue)
    }

    override fun setIssuesToRV(issues: List<Issue>) {
        adapter.issuesList = issues
        adapter.setCallback(this)
    }

    override fun setProfileListener() {
        binding.userAvatar.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    override fun showScreen(screen: Screens) {
        val intent = when (screen) {
            Screens.AuthenticationScreens ->
                Intent(this, AuthActivity::class.java)

            Screens.IssuesScreens ->
                Intent(this, IssuesActivity::class.java)

            Screens.StartScreens ->
                Intent(this, StartScreenActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

    override fun setFilterData(data: List<IssuesFilterType>) {
        adapter.filterData = data
    }

    override fun startIssue(issueEntities: List<Issue>, position: Int) {
        issuesPresenter.sortIssues(issueEntities, position)
        issuesPresenter.startIssue(issueEntities[position])
    }

    override fun showIssueInfoActivity(issueEntity: Issue) {
        val intent = Intent(this, IssueTimerActivity::class.java)
        intent.putExtra("issue_id", issueEntity.id)
        startActivity(intent)
    }

    override fun filterIssuesByType(
        filterType: IssuesFilterType,
        issues: List<Issue>
    ): List<Issue> {
        return issuesPresenter.filterIssuesByType(filterType, issues)
    }
}