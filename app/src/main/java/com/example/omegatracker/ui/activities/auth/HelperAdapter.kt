package com.example.omegatracker.ui.activities.auth

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.omegatracker.databinding.AuthHelperItemBinding
import com.example.omegatracker.entity.HelperContent

class HelperAdapter : RecyclerView.Adapter<HelperAdapter.HelperHolder>() {

    inner class HelperHolder(binding: AuthHelperItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val helperTextView = binding.helperText
        val helperImageView = binding.helperImage
    }

    var data = emptyList<HelperContent>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelperAdapter.HelperHolder {
        val binding = AuthHelperItemBinding.inflate(LayoutInflater.from(parent.context))
        return HelperHolder(binding)
    }

    override fun onBindViewHolder(
        holder: HelperAdapter.HelperHolder,
        position: Int
    ) {
        val item = data[position]
        holder.helperTextView.setText(item.text ?: 0)
        holder.helperImageView.setImageResource(item.imageId ?: 0)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}