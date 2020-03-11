package com.gavinsappcreations.upcominggames.ui.list

import android.app.Application
import androidx.lifecycle.*
import com.gavinsappcreations.upcominggames.repository.GameRepository
import com.gavinsappcreations.upcominggames.utilities.DatabaseState
import kotlinx.coroutines.launch

class ListViewModel(application: Application) : AndroidViewModel(application) {

    private val gameRepository = GameRepository.getInstance(application)

    init {
        viewModelScope.launch {
            //TODO: on first load, update database (show loading indicator)
            //gamesRepository.downloadGameListData()
        }
    }

    val databaseState = gameRepository.databaseState

    // When the sortOptions LiveData changes, switchMap sets gameList = gameRepository.getGameList(it).
    val gameList = Transformations.switchMap(gameRepository.sortOptions) {
        gameRepository.getGameList(it)
    }


    /**
     * The observer that triggers this method fires once under normal circumstances, but fires
     * twice if the sort options change. When sort options change, the Success state doesn't occur
     * until the second firing. So in this case, DatabaseState transitions from LoadingSortChange to
     * Loading, and then finally to Success.
     */
    fun updateDatabaseState() {
        when (databaseState.value) {
            DatabaseState.LoadingSortChange -> gameRepository.updateDatabaseState(DatabaseState.Loading)
            DatabaseState.Loading -> gameRepository.updateDatabaseState(DatabaseState.Success)
            DatabaseState.Success -> return
        }
    }


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



