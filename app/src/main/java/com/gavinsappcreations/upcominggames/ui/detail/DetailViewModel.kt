package com.gavinsappcreations.upcominggames.ui.detail

import android.app.Application
import androidx.lifecycle.*
import com.gavinsappcreations.upcominggames.domain.DetailNetworkState
import com.gavinsappcreations.upcominggames.domain.GameDetail
import com.gavinsappcreations.upcominggames.repository.GameRepository
import com.gavinsappcreations.upcominggames.utilities.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailViewModel(application: Application, val guid: String) :
    AndroidViewModel(application) {

    private val gameRepository = GameRepository

    // Stores whether currently displayed game is a favorite.
    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean>
        get() = _isFavorite

    // Stores the currently displayed game's details.
    private val _gameDetail = MutableLiveData<GameDetail?>()
    val gameDetail: LiveData<GameDetail?>
        get() = _gameDetail

    // Stores the state of accessing the game details from the network.
    private val _networkState = MutableLiveData<DetailNetworkState>()
    val detailNetworkState: LiveData<DetailNetworkState>
        get() = _networkState

    // Holds an Event for when the user pops the back stack by hitting Up navigation button.
    private val _popBackStack = MutableLiveData<Event<Boolean>>()
    val popBackStack: LiveData<Event<Boolean>>
        get() = _popBackStack

    private val _viewGameLink = MutableLiveData<Event<Boolean>>()
    val viewGameLink: LiveData<Event<Boolean>>
        get() = _viewGameLink

    // Holds an event for navigating to ScreenshotFragment.
    private val _navigateToScreenshotFragment = MutableLiveData<Event<Int>>()
    val navigateToScreenshotFragment: LiveData<Event<Int>>
        get() = _navigateToScreenshotFragment


    init {
        downloadGameDetailData()
        getIsFavorite()
    }


    // Updates the isFavorite value of this game in the "Game" table.
    fun updateFavorite() {
        // Invert value of _isFavorite
        val isFavoriteNewValue = !_isFavorite.value!!

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val rowsUpdated = gameRepository.updateFavorite(isFavoriteNewValue, guid)
                if (rowsUpdated == 1) {
                    _isFavorite.postValue(isFavoriteNewValue)
                } else {
                    throw Exception("Failed to update a favorite.")
                }
            }
        }
    }

    // Downloads the game detail data from the API.
    fun downloadGameDetailData() {
        _networkState.value = DetailNetworkState.Loading
        viewModelScope.launch {
            try {
                _gameDetail.value = gameRepository.downloadGameDetailData(guid)
                _networkState.value = DetailNetworkState.Success
            } catch (e: Exception) {
                _networkState.value = DetailNetworkState.Failure
            }
        }
    }

    // Gets the value is isFavorite for this game.
    private fun getIsFavorite() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val isFavorite = gameRepository.getIsFavorite(guid)
                _isFavorite.postValue(isFavorite)
            }
        }
    }

    fun onPopBackStack() {
        _popBackStack.value = Event(true)
    }

    fun onViewGameLink() {
        _viewGameLink.value = Event(true)
    }

    fun onNavigateToScreenshotFragment(imageIndex: Int) {
        _navigateToScreenshotFragment.value = Event(imageIndex)
    }

    // Factory for constructing DetailViewModel with Application parameter and guid parameters.
    class Factory(
        private val application: Application,
        private val guid: String
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DetailViewModel(
                    application, guid
                ) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}



