package com.gavinsappcreations.upcominggames.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gavinsappcreations.upcominggames.App
import com.gavinsappcreations.upcominggames.domain.Game
import com.gavinsappcreations.upcominggames.domain.SearchResult
import com.gavinsappcreations.upcominggames.repository.GameRepository
import com.gavinsappcreations.upcominggames.utilities.Event
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val gameRepository = App.gameRepository

    private val _navigateToDetailFragment = MutableLiveData<Event<Game>>()
    val navigateToDetailFragment: LiveData<Event<Game>>
        get() = _navigateToDetailFragment

    private val _popBackStack = MutableLiveData<Event<Boolean>>()
    val popBackStack: LiveData<Event<Boolean>>
        get() = _popBackStack

    private val _clearSearchText = MutableLiveData<Event<Boolean>>()
    val clearSearchText: LiveData<Event<Boolean>>
        get() = _clearSearchText

    private val _showKeyboard = MutableLiveData<Event<Boolean>>()
    val showKeyboard: LiveData<Event<Boolean>>
        get() = _showKeyboard

    // Holds all the search results corresponding to the current search query typed by the user.
    val searchResults = MutableLiveData<List<SearchResult>>()

    // Search the database for games matching the search query.
    fun searchGameList(searchQuery: String) {
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

    fun onClearSearchText() {
        _clearSearchText.value = Event(true)
    }

    fun onShowKeyboard() {
        _showKeyboard.value = Event(true)
    }
}
