package com.gavinsappcreations.upcominggames.ui.list

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import com.gavinsappcreations.upcominggames.database.getDatabase
import com.gavinsappcreations.upcominggames.domain.SortOptions
import com.gavinsappcreations.upcominggames.network.GameBoundaryCallback
import com.gavinsappcreations.upcominggames.repository.GameRepository
import com.gavinsappcreations.upcominggames.utilities.DATABASE_PAGE_SIZE
import kotlinx.coroutines.launch

class ListViewModel(application: Application) : AndroidViewModel(application) {

    private val gamesRepository = GameRepository.getInstance(application)

    val sortOptions = gamesRepository.sortOptions

    init {
        viewModelScope.launch {
            //TODO: on first load, update database (show loading indicator)
            //gamesRepository.downloadGameListData()
        }
    }


    fun onSortDirectionChanged(sortOptions: SortOptions) {
       gamesRepository.updateSortOptions(sortOptions)
    }


    // TODO: Create my own MutableLiveData called sortOptions. Then whenever that value changes, do a switchMap to return "games".

    val games = Transformations.switchMap(sortOptions) {
        gamesRepository.getGameList()
    }


/*    // TODO: remove after add sortOptions to Repository
    fun checkIfSortOptionsChanged() {
        // If sort options changed, invalidate the data source
        if (gamesRepository.fetchAndCompareSortOptions()) {
            boolTest.value = true
        }
    }*/


    //Factory for constructing ListViewModel with Application parameter.
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ListViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ListViewModel(
                    application
                ) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}



