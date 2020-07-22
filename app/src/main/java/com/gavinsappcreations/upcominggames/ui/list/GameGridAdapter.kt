package com.gavinsappcreations.upcominggames.ui.list

import android.view.LayoutInflater
import android.view.View
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
        return GameReleaseViewHolder(
            GridListItemBinding.inflate(LayoutInflater.from(parent.context)),
            onClickListener
        )
    }

    override fun onBindViewHolder(holder: GameReleaseViewHolder, position: Int) {
        val gameRelease = getItem(position)

        gameRelease?.let {
            holder.bind(gameRelease)
        }
    }


    class GameReleaseViewHolder(
        private var binding: GridListItemBinding,
        private val onClickListener: OnClickListener
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            binding.game?.let { game ->
                onClickListener.onClick(game)
            }
        }

        fun bind(game: Game) {
            binding.game = game
            binding.executePendingBindings()

            val platforms = game.platforms?.sortPlatformAbbreviationsByRelevance() ?: return

            val flexboxLayout = binding.platformFlexboxLayout

            //Since ViewHolders are reused, we need to remove the previously added Views first.
            flexboxLayout.removeAllViews()

            // Generate a TextView for each platform the game is on.
            for (platform in platforms) {
                val textView = LayoutInflater.from(binding.root.context).inflate(
                    R.layout.platform_chip_layout,
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







