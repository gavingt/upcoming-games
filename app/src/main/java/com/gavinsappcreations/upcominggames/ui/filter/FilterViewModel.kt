package com.gavinsappcreations.upcominggames.ui.filter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.gavinsappcreations.upcominggames.domain.ReleaseDateType
import com.gavinsappcreations.upcominggames.domain.FilterOptions
import com.gavinsappcreations.upcominggames.repository.GameRepository
import com.gavinsappcreations.upcominggames.utilities.Event
import com.gavinsappcreations.upcominggames.utilities.KEY_SAVED_STATE_PLATFORM_INDICES
import com.gavinsappcreations.upcominggames.utilities.PropertyAwareMutableLiveData

// TODO: move API key to a file that isn’t committed to Github
// TODO: clean up code to make it more readable and add comments for everything

class FilterViewModel(application: Application, val state: SavedStateHandle) :
    AndroidViewModel(application) {

    private val gameRepository = GameRepository.getInstance(application)
    private val originalFilterOptions = gameRepository.filterOptions.value!!

    private val savedStatePlatformIndices = state.get<MutableSet<Int>>(
        KEY_SAVED_STATE_PLATFORM_INDICES
    )

    val unsavedFilterOptions = PropertyAwareMutableLiveData(
        FilterOptions(
            originalFilterOptions.releaseDateType,
            originalFilterOptions.sortDirection,
            originalFilterOptions.customDateStart,
            originalFilterOptions.customDateEnd,
            originalFilterOptions.platformType,
            // Use SavedStateHandle version if not null. Otherwise create a new set from original.
            savedStatePlatformIndices
                ?: mutableSetOf<Int>().apply { addAll(originalFilterOptions.platformIndices) }
        )
    )


    private val _popBackStack = MutableLiveData<Event<Boolean>>()
    val popBackStack: LiveData<Event<Boolean>>
        get() = _popBackStack

    private val _updateFilterOptions = MutableLiveData<Event<Boolean>>()
    val updateFilterOptions: LiveData<Event<Boolean>>
        get() = _updateFilterOptions

    fun saveNewFilterOptions() {
        gameRepository.updateFilterOptions(unsavedFilterOptions.value!!)
    }


    fun onPopBackStack() {
        _popBackStack.value = Event(true)
    }

    fun onPlatformCheckedChange(platformIndex: Int, isChecked: Boolean) {
        val platformIndices = unsavedFilterOptions.value!!.platformIndices
        if (isChecked) {
            platformIndices.add(platformIndex)
        } else {
            platformIndices.remove(platformIndex)
        }
        state.set(KEY_SAVED_STATE_PLATFORM_INDICES, platformIndices)
    }

    fun onUpdateFilterOptions(
        startDateError: String?,
        startDateText: String?,
        endDateError: String?,
        endDateText: String?
    ) {
        if (unsavedFilterOptions.value!!.releaseDateType == ReleaseDateType.CustomDate) {
            if (startDateError == null && !startDateText.isNullOrBlank()
                && endDateError == null && !endDateText.isNullOrBlank()
            ) {
                _updateFilterOptions.value = Event(true)
            } else {
                _updateFilterOptions.value = Event(false)
            }
        } else {
            _updateFilterOptions.value = Event(true)
        }
    }

}
