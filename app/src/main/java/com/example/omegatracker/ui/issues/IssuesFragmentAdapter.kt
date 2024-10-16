package com.example.omegatracker.ui.issues

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.omegatracker.data.componentsToString
import com.example.omegatracker.databinding.ItemActiveIssueBinding
import com.example.omegatracker.databinding.ItemIssueBinding
import com.example.omegatracker.databinding.ItemIssuesHeaderBinding
import com.example.omegatracker.databinding.ItemRvEmptyTodayListBinding
import com.example.omegatracker.databinding.ItemRvLoadingBinding
import com.example.omegatracker.entity.Issue
import com.example.omegatracker.entity.IssuesFilterType

class IssuesFragmentAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var callback: IssuesCallback

    var issuesList = emptyList<Issue>()
        set(value) {
            field = value
            issuesListFiltered = value
        }

    private var issuesListFiltered = emptyList<Issue>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val activeIssuesType = 0
    private val issuesHeaderType = 1
    private val issuesListType = 2
    private val loadingType = 3
    private val emptyTodayListType = 4

    private var currentFilterType = IssuesFilterType.All
    var filterData = emptyList<IssuesFilterType>()
        set(value) {
            field = value
        }

    fun setCallback(callback: IssuesCallback) {
        this.callback = callback
    }

    fun onIssueTimerUpdated(issue: Issue) {
        val position: Int = issuesListFiltered.indexOfFirst { it.id == issue.id }
        issuesListFiltered[position].spentTime = issue.spentTime
        notifyItemChanged(position)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < issuesListFiltered.count { it.isActive }) {
            activeIssuesType
        } else if (position == issuesListFiltered.count { it.isActive }) {
            issuesHeaderType
        } else if (position < issuesListFiltered.size + 1 && issuesListFiltered.size != issuesListFiltered.count { it.isActive }) {
            issuesListType
        } else if (
            (issuesListFiltered.isEmpty() || issuesListFiltered.size == issuesListFiltered.count { it.isActive })
            && currentFilterType == IssuesFilterType.Today
        ) {
            emptyTodayListType
        } else {
            loadingType
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            activeIssuesType -> {
                val binding = ItemActiveIssueBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ActiveIssueHolder(binding)
            }

            issuesListType -> {
                val binding =
                    ItemIssueBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return IssueHolder(binding)
            }

            issuesHeaderType -> {
                val binding = ItemIssuesHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return IssuesHeaderHolder(binding)
            }

            loadingType -> {
                val binding = ItemRvLoadingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return LoadingHolder(binding)
            }

            else -> {
                val binding = ItemRvEmptyTodayListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return EmptyTodayListHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            activeIssuesType -> {
                val item = issuesListFiltered[position]
                holder as ActiveIssueHolder
                holder.issueTime.text =
                    (item.estimatedTime - item.spentTime).componentsToString('ч', 'м', 'с')
                holder.issueDescription.text = item.projectName
                holder.parent.setOnClickListener {
                    callback.showIssueInfoActivity(item)
                }
            }

            issuesHeaderType -> {
                holder as IssuesHeaderHolder
                holder.filterBtn.setOnClickListener {
                    val item: IssuesFilterType
                    when (currentFilterType) {
                        IssuesFilterType.Today -> {
                            currentFilterType = IssuesFilterType.All
                            item = filterData[0]
                        }

                        IssuesFilterType.All -> {
                            currentFilterType = IssuesFilterType.Today
                            item = filterData[1]
                        }
                    }
                    issuesListFiltered = callback.filterIssuesByType(currentFilterType, issuesList)
                    holder.filterBtn.setText(item.btnText)
                    holder.header.setText(item.headerText)
                }
            }

            issuesListType -> {
                val item = issuesListFiltered[position - 1]
                holder as IssueHolder
                holder.remainingTime.text =
                    (item.estimatedTime - item.spentTime).componentsToString('ч', 'м')
                holder.issueName.text = item.summary
                holder.issueGroup.text = item.projectName
                holder.openedStat.setText(item.state.stateName)

                holder.issueBStart.setOnClickListener {
                    callback.startIssue(issuesList, position - 1)
                }
                holder.parent.setOnClickListener {
                    callback.showIssueInfoActivity(item)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (issuesListFiltered.isNotEmpty()) {
            if (issuesListFiltered.size == issuesListFiltered.count { it.isActive }) {
                issuesListFiltered.size + 2
            } else {
                issuesListFiltered.size + 1
            }
        } else {
            2
        }
    }

    inner class ActiveIssueHolder(binding: ItemActiveIssueBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val issueTime = binding.itemActiveIssueTime
        val issueBShowFull = binding.itemIssueBShowFull
        val issueDescription =
            binding.itemActiveIssueDescription
        val issueIcon = binding.itemActiveIssueIcon
        val parent = binding.rvItemActiveIssue
    }

    inner class IssuesHeaderHolder(binding: ItemIssuesHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val filterBtn = binding.changeIssuesFilter
        val header = binding.activeIssuesListHeader
    }

    inner class IssueHolder(binding: ItemIssueBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val issueName = binding.itemIssueName
        val issueBStart = binding.itemIssueBStart
        val issueGroup = binding.itemIssueProject
        val issueIcon = binding.itemIssueIcon
        val openedStat = binding.itemIssueState
        val remainingTime = binding.itemRemainingTime
        val parent = binding.root
    }

    inner class LoadingHolder(binding: ItemRvLoadingBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class EmptyTodayListHolder(binding: ItemRvEmptyTodayListBinding) :
        RecyclerView.ViewHolder(binding.root)
}