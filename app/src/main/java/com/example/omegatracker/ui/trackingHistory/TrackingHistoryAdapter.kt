package com.example.omegatracker.ui.trackingHistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.omegatracker.databinding.ItemTrackingHistoryHeaderBinding
import com.example.omegatracker.databinding.ItemTrackingHistoryIssueBinding
import com.example.omegatracker.room.IssueAndHistory
import com.example.omegatracker.room.IssuesTrackingHistory
import com.omega_r.libs.omegarecyclerview.sticky_decoration.StickyAdapter

class TrackingHistoryAdapter(
    private val callback: TrackingHistoryCallback,
    private val items: List<IssueAndHistory>
) : RecyclerView.Adapter<TrackingHistoryAdapter.TrackingHistoryIssueHolder>(),
    StickyAdapter<TrackingHistoryAdapter.TrackingHistoryHeaderHolder> {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackingHistoryAdapter.TrackingHistoryIssueHolder {
        val binding = ItemTrackingHistoryIssueBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrackingHistoryIssueHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackingHistoryIssueHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getStickyId(position: Int): Long {
         return items[position].history.historyGroup.hashCode().toLong()
    }

    override fun onCreateStickyViewHolder(parent: ViewGroup?): TrackingHistoryHeaderHolder {
        val binding = ItemTrackingHistoryHeaderBinding.inflate(
            LayoutInflater.from(parent!!.context),
            parent,
            false
        )
        return TrackingHistoryHeaderHolder(binding)
    }

    override fun onBindStickyViewHolder(viewHolder: TrackingHistoryHeaderHolder?, position: Int) {
        val holder = viewHolder as TrackingHistoryHeaderHolder
        holder.bind(items[position].history)
    }

    override fun onClickStickyViewHolder(id: Long) {
        println(id)
        callback.scrollToPosition(items.indexOfFirst { id == it.history.historyGroup.hashCode().toLong()})
    }

    inner class TrackingHistoryHeaderHolder(binding: ItemTrackingHistoryHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val dateTV = binding.changesDate
        fun bind(history: IssuesTrackingHistory) {
            dateTV.text = history.historyGroup
        }
    }

    inner class TrackingHistoryIssueHolder(binding: ItemTrackingHistoryIssueBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val summary = binding.itemIssueName
        private val project = binding.itemIssueProject
        private val spentTime = binding.itemSpentTime
        private val startTime = binding.itemStartTime
        private val stopTime = binding.itemStopTime
        private val issueIcon = binding.itemIssueIcon

        fun bind(item: IssueAndHistory) = with(item) {
            summary.text = issue.summary
            project.text = issue.projectName
            spentTime.text = callback.formatTimeToHMS(history.endTime - history.issueStartTime)
            startTime.text = callback.formatTimeToHMS(history.issueStartTime)
            stopTime.text = callback.formatTimeToHMS(history.endTime)
        }
    }
}