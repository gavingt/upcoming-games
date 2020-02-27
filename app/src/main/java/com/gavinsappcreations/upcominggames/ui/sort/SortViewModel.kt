package com.gavinsappcreations.upcominggames.ui.sort

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gavinsappcreations.upcominggames.R
import com.gavinsappcreations.upcominggames.utilities.*

class SortViewModel(application: Application) : ViewModel() {

    private val prefs =
        application.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val _releaseDateSelection = MutableLiveData<ReleaseDateSelection>()
    val releaseDateSelection: LiveData<ReleaseDateSelection>
        get() = _releaseDateSelection

    private val _sortDirectionSelection = MutableLiveData<SortDirection>()
    val sortDirectionSelection: LiveData<SortDirection>
        get() = _sortDirectionSelection

    init {
        _releaseDateSelection.value =
            enumValueOf<ReleaseDateSelection>(
                prefs.getString(
                    KEY_RELEASE_DATE_SELECTION,
                    ReleaseDateSelection.RecentAndUpcoming.name
                )!!
            )

        _sortDirectionSelection.value =
            enumValueOf<SortDirection>(
                prefs.getString(
                    KEY_SORT_DIRECTION,
                    SortDirection.Ascending.name
                )!!
            )
    }

    fun saveSortOptions() {
        prefs.edit().putString(KEY_RELEASE_DATE_SELECTION, _releaseDateSelection.value!!.name)
            .apply()
        prefs.edit().putString(KEY_SORT_DIRECTION, _sortDirectionSelection.value!!.name)
            .apply()
    }

    fun onSortDirectionChanged() {
        val oldSortDirection: SortDirection = _sortDirectionSelection.value!!
        if (oldSortDirection == SortDirection.Ascending) {
            _sortDirectionSelection.value = SortDirection.Descending
        } else {
            _sortDirectionSelection.value = SortDirection.Ascending
        }
    }

    fun onReleaseDateSelectionChanged(checkedId: Int) {
        _releaseDateSelection.value = when (checkedId) {
            R.id.recent_and_upcoming_releases_radioButton -> ReleaseDateSelection.RecentAndUpcoming
            R.id.past_month_radioButton -> ReleaseDateSelection.PastMonth
            R.id.past_year_radioButton -> ReleaseDateSelection.PastYear
            else -> ReleaseDateSelection.CustomRange
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
