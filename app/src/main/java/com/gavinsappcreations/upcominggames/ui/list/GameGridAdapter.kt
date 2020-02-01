package com.gavinsappcreations.upcominggames.ui.list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gavinsappcreations.upcominggames.databinding.GridViewItemBinding
import com.gavinsappcreations.upcominggames.domain.Game
import com.google.android.material.chip.Chip




class GameGridAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Game, GameGridAdapter.GameReleaseViewHolder>(DiffCallback) {

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

        fun bind(game: Game) {
            binding.game = game

            if (game.platforms != null) {
                val chipGroup = binding.platformChipGroup
                for (platform in game.platforms) {
                    val chip = Chip(binding.root.context)
                    chip.text = platform
                    chipGroup.addView(chip)
                }
            }

            binding.executePendingBindings()
        }
    }


    companion object DiffCallback : DiffUtil.ItemCallback<Game>() {
        override fun areItemsTheSame(oldItem: Game, newItem: Game): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Game, newItem: Game): Boolean {
            return oldItem.releaseId == newItem.releaseId
        }
    }

    class OnClickListener(val clickListener: (game: Game) -> Unit) {
        fun onClick(game: Game) = clickListener(game)
    }

}






