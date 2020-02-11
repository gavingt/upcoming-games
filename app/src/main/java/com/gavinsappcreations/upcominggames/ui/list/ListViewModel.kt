package com.gavinsappcreations.upcominggames.ui.list

import android.app.Application
import androidx.lifecycle.*
import com.gavinsappcreations.upcominggames.database.asDomainModel
import com.gavinsappcreations.upcominggames.repository.GamesRepository
import kotlinx.coroutines.launch

class ListViewModel(application: Application) : AndroidViewModel(application) {

    private val gamesRepository = GamesRepository(application)

    init {
        viewModelScope.launch {
            gamesRepository.downloadGameData(0)
        }
    }

    val games = gamesRepository.getGameList()


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



