package com.gavinsappcreations.upcominggames.ui.list

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.gavinsappcreations.upcominggames.App
import com.gavinsappcreations.upcominggames.domain.Game
import com.gavinsappcreations.upcominggames.domain.Platform
import com.gavinsappcreations.upcominggames.repository.GameRepository
import com.gavinsappcreations.upcominggames.utilities.allPlatforms
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListViewModel(application: Application) : AndroidViewModel(application) {

    private val gameRepository = GameRepository.getInstance(application)

    init {
        viewModelScope.launch {
            //TODO: on first load, update database (show loading indicator)
            //gamesRepository.downloadGameListData()
        }
    }


    // When the sortOptions LiveData changes, switchMap sets games = gameRepository.getGameList().
    val games = Transformations.switchMap(gameRepository.sortOptions) {
        gameRepository.getGameList()
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



