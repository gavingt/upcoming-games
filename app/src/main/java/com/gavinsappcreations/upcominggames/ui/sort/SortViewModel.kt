package com.gavinsappcreations.upcominggames.ui.sort

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.gavinsappcreations.upcominggames.domain.ReleaseDateType
import com.gavinsappcreations.upcominggames.domain.SortOptions
import com.gavinsappcreations.upcominggames.repository.GameRepository
import com.gavinsappcreations.upcominggames.utilities.Event
import com.gavinsappcreations.upcominggames.utilities.KEY_SAVED_STATE_PLATFORM_INDICES
import com.gavinsappcreations.upcominggames.utilities.PropertyAwareMutableLiveData

// TODO: move API key to a file that isn’t committed to Github
// TODO: clean up code to make it more readable and add comments for everything

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
            // Use SavedStateHandle version if not null. Otherwise create a new set from original.
            savedStatePlatformIndices
                ?: mutableSetOf<Int>().apply { addAll(originalSortOptions.platformIndices) }
        )
    )


    private val _popBackStack = MutableLiveData<Event<Boolean>>()
    val popBackStack: LiveData<Event<Boolean>>
        get() = _popBackStack

    private val _updateSortOptions = MutableLiveData<Event<Boolean>>()
    val updateSortOptions: LiveData<Event<Boolean>>
        get() = _updateSortOptions

    fun saveNewSortOptions() {
        gameRepository.updateSortOptions(unsavedSortOptions.value!!)
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
