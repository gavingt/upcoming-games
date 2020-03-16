package com.gavinsappcreations.upcominggames.ui.search

import android.app.Application
import androidx.lifecycle.*
import com.gavinsappcreations.upcominggames.domain.Game
import com.gavinsappcreations.upcominggames.repository.GameRepository
import com.gavinsappcreations.upcominggames.utilities.Event

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val _navigateToDetailFragment = MutableLiveData<Event<Game>>()
    val navigateToDetailFragment: LiveData<Event<Game>>
        get() = _navigateToDetailFragment

    private val _popBackStack = MutableLiveData<Event<Boolean>>()
    val popBackStack: LiveData<Event<Boolean>>
        get() = _popBackStack

    private val _showKeyboard = MutableLiveData<Boolean>()
    val showKeyboard: LiveData<Boolean>
        get() = _showKeyboard

    private val gameRepository = GameRepository.getInstance(application)

    private val searchQuery = MutableLiveData<String>()

    val searchResults = Transformations.switchMap(searchQuery) {
        gameRepository.searchGameList(it)
    }

    fun onSearchQueryChanged(newSearchQuery: String) {
        searchQuery.value = newSearchQuery
    }


    fun onNavigateToDetailFragment(game: Game) {
        _navigateToDetailFragment.value = Event(game)
    }

    fun onPopBackStack() {
        _popBackStack.value = Event(true)
    }

    fun onShowKeyboard() {
        _showKeyboard.value = _showKeyboard.value == null
    }

    //Factory for constructing ListViewModel with Application parameter.
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SearchViewModel(
                    application
                ) as T
            }
            throw IllegalArgumentException("Unable to construct viewModel")
        }
    }

}
