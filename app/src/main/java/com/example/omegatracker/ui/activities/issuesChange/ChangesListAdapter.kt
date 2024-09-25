package com.example.omegatracker.ui.activities.issuesChange

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.omegatracker.databinding.ItemChangesRvBinding
import com.example.omegatracker.databinding.ItemChangesRvIssueBinding
import com.example.omegatracker.room.IssuesChangeList

class ChangesListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var indexes: List<String>? = null
    var items: Map<String, List<IssuesChangeList>> = mapOf()
        set(value) {
            field = value
            indexes = value.keys.toList()
            notifyDataSetChanged()
        }

    var callback: ChangesActivityCallback? = null
        set(value) {
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = ItemChangesRvBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DateItemsViewHolder(view)
    }

    override fun getItemCount(): Int {
        println(items.size)
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[indexes?.get(position)]
        holder as DateItemsViewHolder
        holder.dateTV.text = indexes?.get(position)

        val changedIssuesAdapter = ChangedIssuesAdapter()
        changedIssuesAdapter.changesList = item
        holder.change_issues_rv.layoutManager = LinearLayoutManager(holder.itemView.context)

        holder.change_issues_rv.adapter = changedIssuesAdapter
    }

    inner class DateItemsViewHolder(binding: ItemChangesRvBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val dateTV = binding.changesDate
        val change_issues_rv = binding.changeIssuesRv
    }

    inner class ChangedIssuesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var changesList: List<IssuesChangeList>? = listOf()
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = ItemChangesRvIssueBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ChangeIssuesViewHolder(view)
        }

        override fun getItemCount(): Int {
            return changesList?.size ?: 0
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = changesList?.get(position)
            holder as ChangeIssuesViewHolder

            holder.summary.text = item?.issueSummary
            holder.project.text = item?.projectName
            holder.spentTime.text =
                callback?.formatTimeToHMS((item?.startTime?.let { item.endTime.minus(it) }))
            holder.startTime.text = callback?.formatTimeToHMS(item?.startTime)
            holder.stopTime.text = callback?.formatTimeToHMS(item?.endTime)
        }

        inner class ChangeIssuesViewHolder(binding: ItemChangesRvIssueBinding) :
            RecyclerView.ViewHolder(binding.root) {
            val summary = binding.itemIssueName
            val project = binding.itemIssueProject
            val spentTime = binding.itemSpentTime
            val startTime = binding.itemStartTime
            val stopTime = binding.itemStopTime
            val issueIcon = binding.itemIssueIcon
        }

    }
}