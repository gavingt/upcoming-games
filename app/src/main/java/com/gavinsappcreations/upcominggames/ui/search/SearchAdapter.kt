package com.gavinsappcreations.upcominggames.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gavinsappcreations.upcominggames.databinding.SearchListItemBinding
import com.gavinsappcreations.upcominggames.domain.Game

class SearchAdapter(private val onClickListener: OnClickListener) :
    PagedListAdapter<Game, SearchAdapter.SearchViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(SearchListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val gameRelease = getItem(position)

        gameRelease?.let {
            holder.itemView.setOnClickListener {
                onClickListener.onClick(gameRelease)
            }
            holder.bind(gameRelease)
        }
    }


    class SearchViewHolder(private var binding: SearchListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(game: Game) {
            binding.game = game
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Game>() {
        override fun areItemsTheSame(oldItem: Game, newItem: Game): Boolean =
            oldItem.guid == newItem.guid

        override fun areContentsTheSame(oldItem: Game, newItem: Game): Boolean =
            oldItem == newItem
    }

    class OnClickListener(val clickListener: (game: Game) -> Unit) {
        fun onClick(game: Game) = clickListener(game)
    }
}