package com.example.omegatracker.ui.trackingHistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.omegatracker.databinding.ItemTrackingHistoryEmptyBinding
import com.example.omegatracker.databinding.ItemTrackingHistoryHeaderBinding
import com.example.omegatracker.databinding.ItemTrackingHistoryIssueBinding
import com.example.omegatracker.room.IssueAndHistory
import com.omega_r.libs.omegarecyclerview.sticky_decoration.StickyAdapter

class TrackingHistoryAdapter(private val callback: TrackingHistoryCallback,private val items : List<IssueAndHistory>) :
    RecyclerView.Adapter<TrackingHistoryAdapter.TrackingHistoryIssueHolder>(), StickyAdapter<TrackingHistoryAdapter.TrackingHistoryHeaderHolder> {

    private var headerPositions = mapOf<String,Int>()

    init {
        getHeadersId()
    }

    companion object {
        const val ITEM_TYPE_EMPTY = 0
        const val ITEM_TYPE_HEADER = 1
        const val ITEM_TYPE_ISSUE = 2
    }

    private fun getHeadersId() {
        var headers : List<String> = listOf()
        var positions : MutableList<Int> = mutableListOf()
//        itemsList.forEach {
//            headers[it.historyGroup]
//        }
//        headers.forEach{header->
//            header.value = itemsList.count{it.historyGroup == header.key}
//        }
//        items.forEachIndexed {index,item->
//            if (item is String) {
//                headersID.add(index)
//            }
//        }
//        headers = itemsList.groupBy { it.historyGroup }
//            .mapValues { it.value.size }
//        println(headers)
        headers = items.map { it.history.historyGroup }.distinct()
        var prevCounter : Int = 0
        headers.forEach { header->
            val diff = prevCounter+items.count { it.history.historyGroup == header } + 1
            positions.add(diff)
            prevCounter = diff
        }
        headerPositions = headers.zip(positions).toMap()
//        items.forEach {
//            val historyGroup = it.history.historyGroup
//            headerCounts.getOrDefault(historyGroup,0)+1
//        }
//        println(headerCounts)
    }

    override fun getItemViewType(position: Int): Int {
        return ITEM_TYPE_ISSUE
//        return if (items.isNotEmpty()) {
//            if (items[position] is String) {
//                ITEM_TYPE_HEADER
//            } else {
//                ITEM_TYPE_ISSUE
//            }
//        } else {
//            ITEM_TYPE_EMPTY
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackingHistoryAdapter.TrackingHistoryIssueHolder {
        val binding = ItemTrackingHistoryIssueBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrackingHistoryIssueHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackingHistoryIssueHolder, position: Int) {
        if (items.isNotEmpty()) {
            val item = items[position]
            val history = item.history
            val issue = item.issue
            holder.summary.text = issue.summary
            holder.project.text = issue.projectName
            holder.spentTime.text =
                 callback.formatTimeToHMS((history.issueStartTime.let { history.endTime.minus(it) }))
            holder.startTime.text = callback.formatTimeToHMS(history.issueStartTime)
            holder.stopTime.text = callback.formatTimeToHMS(history.endTime)
        }
    }

    override fun getItemCount(): Int {
        return if (items.isNotEmpty()) {
            items.size
        } else {
            0
        }
    }

    inner class TrackingHistoryHeaderHolder(binding: ItemTrackingHistoryHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val dateTV = binding.changesDate
    }

    inner class TrackingHistoryIssueHolder(binding: ItemTrackingHistoryIssueBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val summary = binding.itemIssueName
        val project = binding.itemIssueProject
        val spentTime = binding.itemSpentTime
        val startTime = binding.itemStartTime
        val stopTime = binding.itemStopTime
        val issueIcon = binding.itemIssueIcon
    }

    inner class EmptyTrackingHistoryHolder(binding: ItemTrackingHistoryEmptyBinding) :
        RecyclerView.ViewHolder(binding.root)

//    fun getKeyForValue(item : IssuesTrackingHistory) : Int {
//        var keyPosition = 0
//        items.forEachIndexed { index, value ->
//            if (value is String) {
//                keyPosition = index
//            }
//            if (value is IssuesTrackingHistory && value == item) {
//                return keyPosition
//            }
//        }
//        return keyPosition
//    }

    override fun getStickyId(position: Int): Long {
//        return if (items.isNotEmpty() && items[position] is IssuesTrackingHistory) {
//            getKeyForValue(items[position] as IssuesTrackingHistory).toLong()
//        } else {
//            0
//        }
        val header = items[position].history.historyGroup
        return headerPositions[header]!!.toLong()
//        val header = itemsList[position].historyGroup
//        return headers[header]!!.toLong()
//        if (headersID.isEmpty()) {
//            return 0L
//        }
//
//        // Binary search to find closest header id
//        var left = 0
//        var right = headersID.size - 1
//        var closestHeaderId = headersID[0]
//
//        while (left <= right) {
//            val mid = (left + right) / 2
//
//            if (headersID[mid] <= position) {
//                closestHeaderId = headersID[mid]
//                left = mid + 1
//            } else {
//                right = mid - 1
//            }
//        }
//        return closestHeaderId.toLong()

//        if (items.isNotEmpty() && items[position] is IssuesTrackingHistory) {
//            return position.toLong()
//        } else {
//           return 0
//        }
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
//        if (items.isNotEmpty()) {
//            val headerId = getStickyId(position).toInt()
//            holder.dateTV.text = items[headerId] as String
//        }
        val header = items[position].history.historyGroup
        holder.dateTV.text = header
    }

    override fun onClickStickyViewHolder(id: Long) {
        callback.scrollToPosition(id)
    }
}