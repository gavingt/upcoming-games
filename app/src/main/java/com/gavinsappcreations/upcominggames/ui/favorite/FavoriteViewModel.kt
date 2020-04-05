package com.gavinsappcreations.upcominggames.ui.favorite

import android.app.Application
import androidx.lifecycle.*
import com.gavinsappcreations.upcominggames.domain.Game
import com.gavinsappcreations.upcominggames.repository.GameRepository
import com.gavinsappcreations.upcominggames.utilities.Event

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {

    private val gameRepository = GameRepository

    // Stores list of user's favorite games.
    val favoriteList = gameRepository.getFavoriteList()

    // Holds an Event for when the user pops the back stack by hitting Up navigation button.
    private val _popBackStack = MutableLiveData<Event<Boolean>>()
    val popBackStack: LiveData<Event<Boolean>>
        get() = _popBackStack

    // Holds an event for navigating to DetailFragment.
    private val _navigateToDetailFragment = MutableLiveData<Event<Game>>()
    val navigateToDetailFragment: LiveData<Event<Game>>
        get() = _navigateToDetailFragment

    fun onPopBackStack() {
        _popBackStack.value = Event(true)
    }

    fun onNavigateToDetailFragment(game: Game) {
        _navigateToDetailFragment.value = Event(game)
    }
}



