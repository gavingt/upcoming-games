package com.gavinsappcreations.upcominggames.ui.sort

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gavinsappcreations.upcominggames.domain.SortOptions
import com.gavinsappcreations.upcominggames.repository.GameRepository
import com.gavinsappcreations.upcominggames.utilities.PropertyAwareMutableLiveData

class SortViewModel(application: Application) : ViewModel() {

    private val gameRepository = GameRepository.getInstance(application)
    private val originalSortOptions = gameRepository.sortOptions.value!!

    val unsavedSortOptions = PropertyAwareMutableLiveData(
        SortOptions(
            originalSortOptions.releaseDateType, originalSortOptions.sortDirection,
            originalSortOptions.customDateStart, originalSortOptions.customDateEnd,
            originalSortOptions.platformIndices
        )
    )

    fun updateSortOptions() {
        gameRepository.updateSortOptions(unsavedSortOptions.value!!)
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
