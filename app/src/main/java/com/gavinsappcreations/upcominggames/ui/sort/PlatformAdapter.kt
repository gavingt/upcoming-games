package com.gavinsappcreations.upcominggames.ui.sort

import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.util.set
import androidx.recyclerview.widget.RecyclerView
import com.gavinsappcreations.upcominggames.R
import com.gavinsappcreations.upcominggames.domain.Game
import com.gavinsappcreations.upcominggames.domain.Platform
import com.gavinsappcreations.upcominggames.utilities.allPlatforms

class PlatformAdapter (private val checkedChangeListener: PlatformAdapter.OnCheckedChangeListener) :
    RecyclerView.Adapter<PlatformAdapter.ViewHolder>() {

    var data = allPlatforms
    val checkedPlatformsList: MutableList<Int> = mutableListOf()

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, position, checkedPlatformsList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val checkBox: CheckBox = itemView.findViewById(R.id.platform_checkBox)

        fun bind(item: Platform, position: Int, checkedPlatformList: MutableList<Int>) {
            checkBox.text = item.fullName
            checkBox.setOnCheckedChangeListener{ _, isChecked ->
                if (isChecked) {
                    checkedPlatformList.add(position)
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.platform_list_item, parent, false)
                return ViewHolder(view)
            }
        }
    }

    class OnCheckedChangeListener (val checkedChangeListener: (platformIndex: Int) -> Unit) {
        fun onCheck(platformIndex: Int) = checkedChangeListener(platformIndex)
    }
}