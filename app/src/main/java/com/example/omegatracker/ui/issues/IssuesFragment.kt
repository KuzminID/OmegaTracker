package com.example.omegatracker.ui.issues

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.omegatracker.R
import com.example.omegatracker.databinding.FragmentIssuesBinding
import com.example.omegatracker.entity.Issue
import com.example.omegatracker.entity.IssuesFilterType
import com.example.omegatracker.service.IssuesServiceBinder
import com.example.omegatracker.ui.base.BaseFragment
import com.example.omegatracker.ui.profile.ProfileActivity
import com.example.omegatracker.ui.timer.IssueTimerActivity

/**
 * A simple [Fragment] subclass.
 */

interface IssuesCallback {
    fun startIssue(issueEntities: List<Issue>, position: Int)
    fun showIssueInfoActivity(issueEntity: Issue)

    fun filterIssuesByType(filterType: IssuesFilterType, issues: List<Issue>): List<Issue>
}

class IssuesFragment : BaseFragment(), IssuesFragmentView, IssuesCallback {

    private val presenter: IssuesFragmentPresenter by providePresenter {
        IssuesFragmentPresenter()
    }
    private lateinit var binding: FragmentIssuesBinding
    private val adapter = IssuesFragmentAdapter()

    fun setController(controller: IssuesServiceBinder) {
        presenter.setController(controller)
        println(controller)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_issues, container, false)
        binding = FragmentIssuesBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.userAvatar.setOnClickListener {
            startActivity(Intent(requireActivity(), ProfileActivity::class.java))
        }
        val controller = arguments?.getSerializable("Controller") as IssuesServiceBinder
        println("This is controller : $controller")
        presenter.setController(controller)
        binding.rvIssuesList.adapter = adapter
        presenter.getIssuesList()
    }

    override fun onResume() {
        super.onResume()
        presenter.checkIssuesChanged()
    }

    override fun startIssue(issueEntities: List<Issue>, position: Int) {
        presenter.sortIssues(issueEntities, position)
        presenter.startIssue(issueEntities[position])
    }

    override fun showIssueInfoActivity(issueEntity: Issue) {
        val intent = Intent(activity, IssueTimerActivity::class.java)
        intent.putExtra("issue_id", issueEntity.id)
        startActivity(intent)
    }

    override fun filterIssuesByType(
        filterType: IssuesFilterType,
        issues: List<Issue>
    ): List<Issue> {
        return presenter.filterIssuesByType(filterType, issues)
    }

    override fun setFilterData(data: List<IssuesFilterType>) {
        adapter.filterData = data
    }

    override fun setIssuesToRV(issues: List<Issue>) {
        adapter.issuesList = issues
        adapter.setCallback(this)
    }

    override fun updateIssueTimer(issue: Issue) {
        adapter.onIssueTimerUpdated(issue)
    }
}