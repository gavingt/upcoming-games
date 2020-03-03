package com.gavinsappcreations.upcominggames.ui.sort

import android.app.Application
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gavinsappcreations.upcominggames.domain.SortOptions
import com.gavinsappcreations.upcominggames.repository.GameRepository
import com.gavinsappcreations.upcominggames.utilities.PropertyAwareMutableLiveData
import com.gavinsappcreations.upcominggames.utilities.ReleaseDateType

class SortViewModel(application: Application) : ViewModel() {

    private val gameRepository = GameRepository.getInstance(application)

    val sortOptions = gameRepository.sortOptions

    val unsavedSortOptions = PropertyAwareMutableLiveData<SortOptions>()

    init {
        val originalSortOptions = SortOptions(sortOptions.value!!.releaseDateType, sortOptions.value!!.sortDirection,
            sortOptions.value!!.customDateStart, sortOptions.value!!.customDateEnd)

        unsavedSortOptions.value = originalSortOptions
    }

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
