package com.example.omegatracker.ui.activities.issues

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.omegatracker.data.componentsToString
import com.example.omegatracker.databinding.ItemActiveIssueBinding
import com.example.omegatracker.databinding.ItemIssueBinding
import com.example.omegatracker.databinding.ItemIssuesHeaderBinding
import com.example.omegatracker.entity.Issue
import kotlin.time.Duration.Companion.minutes

class IssuesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var callback: IssuesCallback

    var issuesList = emptyList<Issue>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val activeIssuesType = 0
    private val issuesHeaderType = 1
    private val issuesListType = 2

    fun setCallback(callback: IssuesCallback) {
        this.callback = callback
    }

    fun onIssueTimerUpdated(issueEntity: Issue) {
        val position: Int = issuesList.indexOfFirst { it.id == issueEntity.id }
        issuesList[position].spentTime = issueEntity.spentTime
        notifyItemChanged(position)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < issuesList.count { it.isActive }) {
            activeIssuesType
        } else if (position == issuesList.count { it.isActive }) {
            issuesHeaderType
        } else {
            issuesListType
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

            else -> {
                val binding = ItemIssuesHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return IssuesHeaderHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            activeIssuesType -> {
                val item = issuesList[position]
                holder as ActiveIssueHolder
                if (item.spentTime != null && item.estimatedTime != null) {
                    holder.issueTime.text =
                        (item.estimatedTime - item.spentTime).componentsToString('ч', 'м', 'с')

                    //holder.issueTime.text=((item.estimatedTime-item.spentTime).toComponents { hours, minutes, seconds, _ -> "${hours}h:${minutes}m:${seconds}s" }).toString()
                } else if (item.estimatedTime != null) {
                    holder.issueTime.text = item.estimatedTime.componentsToString('ч', 'м', 'с')
                    //holder.issueTime.text=(item.estimatedTime.toComponents { hours, minutes, seconds, _ -> "${hours}h:${minutes}m:${seconds}s" }).toString()
                } else {
                    holder.issueTime.text = (0.minutes).componentsToString('ч', 'м', 'с')
                    //holder.issueTime.text= (0.minutes.toComponents { hours, minutes, seconds, _ -> "${hours}h:${minutes}m:${seconds}s"}).toString()
                }
                holder.issueDescription.text = item.projectName
                holder.parent.setOnClickListener {
                    callback.showIssueInfoActivity(item)
                }
            }

            issuesListType -> {
                val item = issuesList[position - 1]
                holder as IssueHolder
                if (item.spentTime != null && item.estimatedTime != null) {
                    holder.remainingTime.text =
                        (item.estimatedTime - item.spentTime).componentsToString('ч', 'м')
                } else if (item.estimatedTime != null) {
                    holder.remainingTime.text = item.estimatedTime.componentsToString('ч', 'м')
                } else {
                    holder.remainingTime.text = 0.minutes.componentsToString('ч', 'м')
                }
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
        return if (issuesList.isNotEmpty()) {
            issuesList.size + 1
        } else {
            0
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
        RecyclerView.ViewHolder(binding.root)

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
}