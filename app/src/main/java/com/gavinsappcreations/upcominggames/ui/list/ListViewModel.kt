package com.gavinsappcreations.upcominggames.ui.list

import android.app.Application
import androidx.lifecycle.*
import com.gavinsappcreations.upcominggames.network.NetworkState
import com.gavinsappcreations.upcominggames.repository.GameRepository
import kotlinx.coroutines.launch

class ListViewModel(application: Application) : AndroidViewModel(application) {

    private val gameRepository = GameRepository.getInstance(application)

    init {
        viewModelScope.launch {
            //TODO: on first load, update database (show loading indicator)
            //gamesRepository.downloadGameListData()
        }
    }

    val networkState = gameRepository.networkState

    // When the sortOptions LiveData changes, switchMap sets games = gameRepository.getGameList().
    val gameList = Transformations.switchMap(gameRepository.sortOptions) {
        gameRepository.getGameList()
    }


    /**
     * The observer that triggers this method fires once under normal circumstances, but fires
     * twice if the sort options change. When sort options change, the "success" state doesn't occur
     * until the second firing. So in this case, NetworkState transitions from LoadingSortChange to
     * Loading, and finally to Success.
     */
    fun updateNetworkState() {
        when (networkState.value) {
            NetworkState.LoadingSortChange -> gameRepository.updateNetworkState(NetworkState.Loading)
            NetworkState.Loading -> gameRepository.updateNetworkState(NetworkState.Success)
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



