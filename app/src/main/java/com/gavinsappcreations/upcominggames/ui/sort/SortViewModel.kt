package com.gavinsappcreations.upcominggames.ui.sort

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gavinsappcreations.upcominggames.repository.GameRepository

class SortViewModel(application: Application) : ViewModel() {

    private val gameRepository = GameRepository.getInstance(application)

    val sortOptions = gameRepository.sortOptions

    fun updateSortOptions() {
        // We need to replace the entire value for sortOptions, or else the observer won't be triggered.
        gameRepository.updateSortOptions(sortOptions.value!!)
    }



    //Factory for constructing ListViewModel with Application parameter.
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SortViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SortViewModel(
                    application
                ) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

}
