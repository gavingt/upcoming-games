package com.gavinsappcreations.upcominggames.ui.favorite

import android.app.Application
import androidx.lifecycle.*
import com.gavinsappcreations.upcominggames.domain.Game
import com.gavinsappcreations.upcominggames.repository.GameRepository
import com.gavinsappcreations.upcominggames.utilities.Event

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {

    private val gameRepository = GameRepository.getInstance(application)

    val favoriteList = gameRepository.getFavoriteList()

    private val _popBackStack = MutableLiveData<Event<Boolean>>()
    val popBackStack: LiveData<Event<Boolean>>
        get() = _popBackStack

    private val _navigateToDetailFragment = MutableLiveData<Event<Game>>()
    val navigateToDetailFragment: LiveData<Event<Game>>
        get() = _navigateToDetailFragment

    fun onPopBackStack() {
        _popBackStack.value = Event(true)
    }

    fun onNavigateToDetailFragment(game: Game) {
        _navigateToDetailFragment.value = Event(game)
    }

    //Factory for constructing FavoriteViewModel with Application parameter.
    class Factory(private val application: Application) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FavoriteViewModel(
                    application
                ) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}



