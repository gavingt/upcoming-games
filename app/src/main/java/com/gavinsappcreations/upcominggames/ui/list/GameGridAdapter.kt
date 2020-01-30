package com.gavinsappcreations.upcominggames.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gavinsappcreations.upcominggames.databinding.GridViewItemBinding

/*
class GameGridAdapter(val onClickListener: OnClickListener) :
    ListAdapter<GameRelease, GameGridAdapter.GameReleaseViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameReleaseViewHolder {
        return GameReleaseViewHolder(GridViewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: GameReleaseViewHolder, position: Int) {
        val gameRelease = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(gameRelease)
        }
        holder.bind(gameRelease)
    }


    class GameReleaseViewHolder(private var binding: GridViewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(gameRelease: GameRelease) {
            binding.gameRelease = gameRelease
            binding.executePendingBindings()
        }
    }


    companion object DiffCallback : DiffUtil.ItemCallback<GameRelease>() {
        override fun areItemsTheSame(oldItem: GameRelease, newItem: GameRelease): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: GameRelease, newItem: GameRelease): Boolean {
            return oldItem.id == newItem.id
        }
    }

    class OnClickListener(val clickListener: (gameRelease: GameRelease) -> Unit) {
        fun onClick(gameRelease: GameRelease) = clickListener(gameRelease)
    }

}
*/






