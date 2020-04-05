package com.gavinsappcreations.upcominggames.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gavinsappcreations.upcominggames.databinding.SearchListItemBinding
import com.gavinsappcreations.upcominggames.domain.SearchResult

class SearchAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<SearchResult, SearchAdapter.SearchViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(SearchListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val searchResult = getItem(position)

        searchResult?.let {
            holder.itemView.setOnClickListener {
                onClickListener.onClick(searchResult)
            }
            holder.bind(searchResult)
        }
    }


    class SearchViewHolder(private var binding: SearchListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(searchResult: SearchResult) {
            binding.searchResult = searchResult
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<SearchResult>() {
        override fun areItemsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean =
            oldItem.game.guid == newItem.game.guid

        override fun areContentsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean =
            oldItem == newItem
    }

    class OnClickListener(val clickListener: (searchResult: SearchResult) -> Unit) {
        fun onClick(searchResult: SearchResult) = clickListener(searchResult)
    }
}