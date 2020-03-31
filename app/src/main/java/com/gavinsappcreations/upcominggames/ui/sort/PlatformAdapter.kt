package com.gavinsappcreations.upcominggames.ui.sort

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.lifecycle.SavedStateHandle
import androidx.recyclerview.widget.RecyclerView
import com.gavinsappcreations.upcominggames.R
import com.gavinsappcreations.upcominggames.domain.Platform
import com.gavinsappcreations.upcominggames.domain.SortOptions
import com.gavinsappcreations.upcominggames.utilities.KEY_SAVED_STATE_PLATFORM_INDICES
import com.gavinsappcreations.upcominggames.utilities.PropertyAwareMutableLiveData
import com.gavinsappcreations.upcominggames.utilities.allKnownPlatforms

class PlatformAdapter(
    private val unsavedSortOptions: PropertyAwareMutableLiveData<SortOptions>,
    private val state: SavedStateHandle
) :
    RecyclerView.Adapter<PlatformAdapter.ViewHolder>() {

    val data = allKnownPlatforms

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        val checkBox = holder.itemView as CheckBox

        // Set the initial checked state of each platform checkBox
        checkBox.isChecked = unsavedSortOptions.value!!.platformIndices.contains(position)
        checkBox.jumpDrawablesToCurrentState()

        state.set(KEY_SAVED_STATE_PLATFORM_INDICES, mutableSetOf<Int>())

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            val platformIndices = unsavedSortOptions.value!!.platformIndices
            if (isChecked) {
                platformIndices.add(position)
            } else {
                platformIndices.remove(position)
            }
            state.set(KEY_SAVED_STATE_PLATFORM_INDICES, platformIndices)
        }

        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.platform_checkBox)

        fun bind(item: Platform) {
            checkBox.text = item.fullName
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.platform_check_list_item, parent, false)
                return ViewHolder(view)
            }
        }
    }

    class OnCheckedChangeListener(val checkedChangeListener: (platformIndices: MutableSet<Int>) -> Unit) {
        fun onCheck(platformIndices: MutableSet<Int>) = checkedChangeListener(platformIndices)
    }
}