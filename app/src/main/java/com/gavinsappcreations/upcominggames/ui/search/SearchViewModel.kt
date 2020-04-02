package com.gavinsappcreations.upcominggames.ui.search

import android.app.Application
import androidx.lifecycle.*
import com.gavinsappcreations.upcominggames.domain.Game
import com.gavinsappcreations.upcominggames.domain.SearchResult
import com.gavinsappcreations.upcominggames.repository.GameRepository
import com.gavinsappcreations.upcominggames.utilities.Event
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    val searchResults = MutableLiveData<ArrayList<SearchResult>>()

    fun onSearchQueryChanged(newSearchQuery: String) {
        searchGameList(newSearchQuery)
    }

    private fun searchGameList(searchQuery: String) {
        viewModelScope.launch {
            searchResults.value = gameRepository.searchGameList(searchQuery)
        }
    }

    fun onNavigateToDetailFragment(searchResult: SearchResult) {
        gameRepository.updateRecentSearches(searchResult)
        _navigateToDetailFragment.value = Event(searchResult.game)
    }

    fun onPopBackStack() {
        _popBackStack.value = Event(true)
    }

    fun onShowKeyboard() {
        _showKeyboard.value = _showKeyboard.value == null
    }
}
