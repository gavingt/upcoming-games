package com.gavinsappcreations.upcominggames.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gavinsappcreations.upcominggames.R
import com.gavinsappcreations.upcominggames.database.DatabaseGame
import com.gavinsappcreations.upcominggames.databinding.GridViewItemBinding
import com.gavinsappcreations.upcominggames.domain.Game
import com.gavinsappcreations.upcominggames.utilities.Platform
import kotlinx.coroutines.newFixedThreadPoolContext

class GameGridAdapter(private val onClickListener: OnClickListener) :
    PagedListAdapter<DatabaseGame, GameGridAdapter.GameReleaseViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameReleaseViewHolder {
        return GameReleaseViewHolder(GridViewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: GameReleaseViewHolder, position: Int) {
        val gameRelease = getItem(position)

        gameRelease?.let {
            holder.itemView.setOnClickListener {
                onClickListener.onClick(gameRelease)
            }
            holder.bind(gameRelease)
        }
    }


    class GameReleaseViewHolder(private var binding: GridViewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(game: DatabaseGame) {
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


    companion object DiffCallback : DiffUtil.ItemCallback<DatabaseGame>() {
        override fun areItemsTheSame(oldItem: DatabaseGame, newItem: DatabaseGame): Boolean {
            return oldItem.releaseId == newItem.releaseId
        }

        override fun areContentsTheSame(oldItem: DatabaseGame, newItem: DatabaseGame): Boolean {
            return oldItem == newItem
        }
    }

    class OnClickListener(val clickListener: (game: DatabaseGame) -> Unit) {
        fun onClick(game: DatabaseGame) = clickListener(game)
    }

}







