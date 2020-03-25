package com.gavinsappcreations.upcominggames.ui.sort

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gavinsappcreations.upcominggames.domain.SortOptions
import com.gavinsappcreations.upcominggames.repository.GameRepository
import com.gavinsappcreations.upcominggames.utilities.Event
import com.gavinsappcreations.upcominggames.utilities.PropertyAwareMutableLiveData
import com.gavinsappcreations.upcominggames.utilities.ReleaseDateType

// TODO: all inputs should be maintained across process death.

class SortViewModel(application: Application) : ViewModel() {

    private val gameRepository = GameRepository.getInstance(application)
    private val originalSortOptions = gameRepository.sortOptions.value!!

    val unsavedSortOptions = PropertyAwareMutableLiveData(
        SortOptions(
            originalSortOptions.releaseDateType, originalSortOptions.sortDirection,
            originalSortOptions.customDateStart, originalSortOptions.customDateEnd,
            originalSortOptions.platformType, originalSortOptions.platformIndices
        )
    )

    private val _popBackStack = MutableLiveData<Event<Boolean>>()
    val popBackStack: LiveData<Event<Boolean>>
        get() = _popBackStack

    private val _updateSortOptions = MutableLiveData<Event<Boolean>>()
    val updateSortOptions: LiveData<Event<Boolean>>
        get() = _updateSortOptions

    fun saveNewSortOptions() {
        gameRepository.saveNewSortOptions(unsavedSortOptions.value!!)
    }

    fun onPopBackStack() {
        _popBackStack.value = Event(true)
    }

    fun onUpdateSortOptions(
        startDateError: String?,
        startDateText: String?,
        endDateError: String?,
        endDateText: String?
    ) {
        if (unsavedSortOptions.value!!.releaseDateType == ReleaseDateType.CustomDate) {
            if (startDateError == null && !startDateText.isNullOrBlank()
                && endDateError == null && !endDateText.isNullOrBlank()) {
                _updateSortOptions.value = Event(true)
            } else {
                _updateSortOptions.value = Event(false)
            }
        } else {
            _updateSortOptions.value = Event(true)
        }
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
