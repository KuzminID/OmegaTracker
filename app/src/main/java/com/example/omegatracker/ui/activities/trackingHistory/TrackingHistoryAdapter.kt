package com.example.omegatracker.ui.activities.trackingHistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.omegatracker.databinding.ItemTrackingHistoryEmptyBinding
import com.example.omegatracker.databinding.ItemTrackingHistoryHeaderBinding
import com.example.omegatracker.databinding.ItemTrackingHistoryIssueBinding
import com.example.omegatracker.room.IssuesChangeList

class TrackingHistoryAdapter(private val callback : ChangesActivityCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: Map<String,List<IssuesChangeList>> = mapOf()
        set(value) {
            field = value
            calculateHeadersPositions()
            indices = value.keys.toList()
            notifyDataSetChanged()
        }

    private var headersPosition = mutableListOf<Int>()

    private var indices = listOf<String>()

    companion object {
        const val ITEM_TYPE_EMPTY = 0
        const val ITEM_TYPE_HEADER = 1
        const val ITEM_TYPE_ISSUE = 2
    }

    private fun calculateHeadersPositions() {
        headersPosition = mutableListOf()
        headersPosition.add(0)
        var previousPosition = 0
        items.forEach {
            val newPosition = it.value.size+1 + previousPosition
            headersPosition.add(newPosition)
            previousPosition = newPosition
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (items.isNotEmpty()) {
            if (headersPosition.find { it == position }!=null) {
                ITEM_TYPE_HEADER
            } else {
                ITEM_TYPE_ISSUE
            }
        } else {
            ITEM_TYPE_EMPTY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TYPE_HEADER -> {
                val binding = ItemTrackingHistoryHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return TrackingHistoryHeaderHolder(binding)
            }
            ITEM_TYPE_ISSUE -> {
                val binding = ItemTrackingHistoryIssueBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return TrackingHistoryIssueHolder(binding)
            }
            else -> {
                val binding = ItemTrackingHistoryEmptyBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return EmptyTrackingHistoryHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int {
        var size : Int = 1
        if (items.isNotEmpty()) {
            size+=items.size-1
            items.forEach {
                size+=it.value.size
            }
        }
        return size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            ITEM_TYPE_HEADER -> {
                holder as TrackingHistoryHeaderHolder
                val index = headersPosition.indexOf(position)
                holder.dateTV.text = indices[index]
            }
            ITEM_TYPE_ISSUE -> {
                holder as TrackingHistoryIssueHolder
                var headerIndex : Int = 0
                var prevIndex : Int? = null
                headersPosition.forEachIndexed { index, i ->
                    if (i<position) {
                        prevIndex = index
                    } else {
                        return@forEachIndexed
                    }
                }
                if (prevIndex == null) {
                    headerIndex = 0
                } else {
                    headerIndex = prevIndex as Int
                }
                val i = position - headersPosition[headerIndex] - 1
                val item : IssuesChangeList? = items[indices[headerIndex]]?.get(i)
                if (item!=null) {
                    holder.summary.text = item.issueSummary
                    holder.project.text = item.projectName
                    holder.spentTime.text =
                        callback.formatTimeToHMS((item.startTime.let { item.endTime.minus(it) }))
                    holder.startTime.text = callback.formatTimeToHMS(item.startTime)
                    holder.stopTime.text = callback.formatTimeToHMS(item.endTime)
                }
            }
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

    inner class EmptyTrackingHistoryHolder(binding : ItemTrackingHistoryEmptyBinding)
        : RecyclerView.ViewHolder(binding.root)
//    var indexes: List<String>? = null
//    var items: Map<String, List<IssuesChangeList>> = mapOf()
//        set(value) {
//            field = value
//            indexes = value.keys.toList()
//            notifyDataSetChanged()
//        }
//
//    var callback: ChangesActivityCallback? = null
//        set(value) {
//            field = value
//        }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        val view = ItemChangesRvBinding.inflate(
//            LayoutInflater.from(parent.context),
//            parent,
//            false
//        )
//        return DateItemsViewHolder(view)
//    }
//
//    override fun getItemCount(): Int {
//        println(items.size + indexes.size)
//        return items.size
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val item = items[indexes?.get(position)]
//        holder as DateItemsViewHolder
//        holder.dateTV.text = indexes?.get(position)
//
//        val changedIssuesAdapter = ChangedIssuesAdapter()
//        changedIssuesAdapter.changesList = item
//        holder.change_issues_rv.layoutManager = LinearLayoutManager(holder.itemView.context)
//
//        holder.change_issues_rv.adapter = changedIssuesAdapter
//    }
//
//    inner class DateItemsViewHolder(binding: ItemChangesRvBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//        val dateTV = binding.changesDate
//        val change_issues_rv = binding.changeIssuesRv
//    }
//
//    inner class ChangedIssuesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//
//        var changesList: List<IssuesChangeList>? = listOf()
//            set(value) {
//                field = value
//                notifyDataSetChanged()
//            }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//            val view = ItemChangesRvIssueBinding.inflate(
//                LayoutInflater.from(parent.context),
//                parent,
//                false
//            )
//            return ChangeIssuesViewHolder(view)
//        }
//
//        override fun getItemCount(): Int {
//            return changesList?.size ?: 0
//        }
//
//        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//            val item = changesList?.get(position)
//            holder as ChangeIssuesViewHolder
//
//            holder.summary.text = item?.issueSummary
//            holder.project.text = item?.projectName
//            holder.spentTime.text =
//                callback?.formatTimeToHMS((item?.startTime?.let { item.endTime.minus(it) }))
//            holder.startTime.text = callback?.formatTimeToHMS(item?.startTime)
//            holder.stopTime.text = callback?.formatTimeToHMS(item?.endTime)
//        }
//
//
}