package com.gavinsappcreations.upcominggames.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gavinsappcreations.upcominggames.databinding.ScreenshotItemBinding

class ScreenshotAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<String, ScreenshotAdapter.ScreenshotViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScreenshotViewHolder {
        return ScreenshotViewHolder(ScreenshotItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ScreenshotViewHolder, position: Int) {
        val gameRelease = getItem(position)

        gameRelease?.let {
            holder.itemView.setOnClickListener {
                onClickListener.onClick(gameRelease)
            }
            holder.bind(gameRelease)
        }
    }


    class ScreenshotViewHolder(private var binding: ScreenshotItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(screenshotUrl: String) {
            binding.screenshotUrl = screenshotUrl
            binding.executePendingBindings()
        }
    }


    companion object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    class OnClickListener(val clickListener: (url: String) -> Unit) {
        fun onClick(screenshotUrl: String) = clickListener(screenshotUrl)
    }

}