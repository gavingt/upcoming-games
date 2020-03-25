package com.gavinsappcreations.upcominggames.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gavinsappcreations.upcominggames.R
import com.gavinsappcreations.upcominggames.databinding.GridListItemBinding
import com.gavinsappcreations.upcominggames.domain.Game
import com.gavinsappcreations.upcominggames.network.sortPlatformAbbreviationsByRelevance

class GameGridAdapter(private val onClickListener: OnClickListener) :
    PagedListAdapter<Game, GameGridAdapter.GameReleaseViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameReleaseViewHolder {
        return GameReleaseViewHolder(GridListItemBinding.inflate(LayoutInflater.from(parent.context)))
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


    class GameReleaseViewHolder(private var binding: GridListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(game: Game) {
            binding.game = game
            binding.executePendingBindings()

            val platforms = game.platforms?.sortPlatformAbbreviationsByRelevance() ?: return

            val flexboxLayout = binding.platformFlexboxLayout

            //Since ViewHolders are reused, we need to remove the previously added ones first.
            flexboxLayout.removeAllViews()

            for (platform in platforms) {
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
            return oldItem.gameId == newItem.gameId
        }

        override fun areContentsTheSame(oldItem: Game, newItem: Game): Boolean {
            return oldItem.gameName == newItem.gameName
                    && oldItem.platforms == newItem.platforms
                    && oldItem.releaseDateInMillis == newItem.releaseDateInMillis
        }
    }

    class OnClickListener(val clickListener: (game: Game) -> Unit) {
        fun onClick(game: Game) = clickListener(game)
    }

}







