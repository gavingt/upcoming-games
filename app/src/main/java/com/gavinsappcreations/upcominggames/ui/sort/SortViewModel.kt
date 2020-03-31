package com.gavinsappcreations.upcominggames.ui.sort

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.gavinsappcreations.upcominggames.domain.SortOptions
import com.gavinsappcreations.upcominggames.repository.GameRepository
import com.gavinsappcreations.upcominggames.utilities.Event
import com.gavinsappcreations.upcominggames.utilities.KEY_SAVED_STATE_PLATFORM_INDICES
import com.gavinsappcreations.upcominggames.utilities.PropertyAwareMutableLiveData
import com.gavinsappcreations.upcominggames.utilities.ReleaseDateType

class SortViewModel(application: Application, val state: SavedStateHandle) :
    AndroidViewModel(application) {

    private val gameRepository = GameRepository.getInstance(application)
    private val originalSortOptions = gameRepository.sortOptions.value!!

    private val savedStatePlatformIndices = state.get<MutableSet<Int>>(
        KEY_SAVED_STATE_PLATFORM_INDICES
    )

    val unsavedSortOptions = PropertyAwareMutableLiveData(
        SortOptions(
            originalSortOptions.releaseDateType,
            originalSortOptions.sortDirection,
            originalSortOptions.customDateStart,
            originalSortOptions.customDateEnd,
            originalSortOptions.platformType,
            // Use SavedStateHandle version if not null
            savedStatePlatformIndices ?: originalSortOptions.platformIndices
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
                && endDateError == null && !endDateText.isNullOrBlank()
            ) {
                _updateSortOptions.value = Event(true)
            } else {
                _updateSortOptions.value = Event(false)
            }
        } else {
            _updateSortOptions.value = Event(true)
        }
    }

}
