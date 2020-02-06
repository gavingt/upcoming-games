package com.gavinsappcreations.upcominggames.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gavinsappcreations.upcominggames.R
import com.gavinsappcreations.upcominggames.databinding.GridViewItemBinding
import com.gavinsappcreations.upcominggames.domain.Game
import com.gavinsappcreations.upcominggames.utilities.Platform

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
            binding.executePendingBindings()

            if (game.platforms == null) {
                return
            }

            val flexboxLayout = binding.platformFlexboxLayout

            //Since ViewHolders are reused, we need to remove the previously added ones first.
            flexboxLayout.removeAllViews()

            val platformsInCurrentSort = mutableListOf<String>()
            for (platform in Platform.values()) {
                platformsInCurrentSort.add(platform.name)
            }
            val platformsForCurrentGame = game.platforms

            val platformsToShow = platformsInCurrentSort.intersect(platformsForCurrentGame)

            for (platform in platformsToShow) {
                val textView = LayoutInflater.from(binding.root.context).inflate(
                    R.layout.platform_textview_layout,
                    flexboxLayout, false
                ) as TextView

                textView.text = platform
                flexboxLayout.addView(textView)
            }
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






