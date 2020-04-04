package com.gavinsappcreations.upcominggames.ui.filter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.gavinsappcreations.upcominggames.R
import com.gavinsappcreations.upcominggames.domain.Platform
import com.gavinsappcreations.upcominggames.domain.FilterOptions
import com.gavinsappcreations.upcominggames.utilities.PropertyAwareMutableLiveData
import com.gavinsappcreations.upcominggames.utilities.allKnownPlatforms

class PlatformAdapter(
    private val unsavedFilterOptions: PropertyAwareMutableLiveData<FilterOptions>,
    private val onCheckedChangeListener: OnCheckedChangeListener
) :
    RecyclerView.Adapter<PlatformAdapter.ViewHolder>() {

    val data = allKnownPlatforms

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        val checkBox = holder.itemView as CheckBox

        // Set the initial checked state of each platform checkBox
        checkBox.isChecked = unsavedFilterOptions.value!!.platformIndices.contains(position)
        checkBox.jumpDrawablesToCurrentState()

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            onCheckedChangeListener.onCheck(position, isChecked)
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

    class OnCheckedChangeListener(val checkedChangeListener: (platformIndex: Int, isChecked: Boolean) -> Unit) {
        fun onCheck(platformIndex: Int, isChecked: Boolean) = checkedChangeListener(platformIndex, isChecked)
    }
}