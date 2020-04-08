package com.gavinsappcreations.upcominggames.ui.filter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.gavinsappcreations.upcominggames.App
import com.gavinsappcreations.upcominggames.domain.FilterOptions
import com.gavinsappcreations.upcominggames.domain.ReleaseDateType
import com.gavinsappcreations.upcominggames.repository.GameRepository
import com.gavinsappcreations.upcominggames.utilities.Event
import com.gavinsappcreations.upcominggames.utilities.KEY_SAVED_STATE_PLATFORM_INDICES
import com.gavinsappcreations.upcominggames.utilities.PropertyAwareMutableLiveData

class FilterViewModel(application: Application, private val state: SavedStateHandle) :
    AndroidViewModel(application) {

    private val gameRepository = App.gameRepository
    private val originalFilterOptions = gameRepository.filterOptions.value!!

    // Lets us retrieve the checked platforms after recovering from system initiated process death.
    private val savedStatePlatformIndices = state.get<MutableSet<Int>>(
        KEY_SAVED_STATE_PLATFORM_INDICES
    )

    /**
     * We copy originalFilterOptions, one field at a time.
     */
    val unsavedFilterOptions = PropertyAwareMutableLiveData(
        FilterOptions(
            originalFilterOptions.releaseDateType,
            originalFilterOptions.sortDirection,
            originalFilterOptions.customDateStart,
            originalFilterOptions.customDateEnd,
            originalFilterOptions.platformType,
            /**
             * Use SavedStateHandle version if not null. Otherwise, create a new set from the
             * original set, since we need our new set to have a different reference.
             */
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

    // Add/remove platforms from platformIndices as they are checked/unchecked.
    fun onPlatformCheckedChange(platformIndex: Int, isChecked: Boolean) {
        val platformIndices = unsavedFilterOptions.value!!.platformIndices
        if (isChecked) {
            platformIndices.add(platformIndex)
        } else {
            platformIndices.remove(platformIndex)
        }
        state.set(KEY_SAVED_STATE_PLATFORM_INDICES, platformIndices)
    }

    // Checks validity of inputs, and sets _updateFilterOptions.value to true only if they're valid.
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
                // Custom date range with valid date inputs.
                _updateFilterOptions.value = Event(true)
            } else {
                // Custom date range with invalid date inputs.
                _updateFilterOptions.value = Event(false)
            }
        } else {
            // If not using custom date range, inputs are always valid.
            _updateFilterOptions.value = Event(true)
        }
    }

}
