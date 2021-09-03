package com.gavinsappcreations.upcominggames.ui.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.gavinsappcreations.upcominggames.App
import com.gavinsappcreations.upcominggames.domain.DatabaseState
import com.gavinsappcreations.upcominggames.domain.Game
import com.gavinsappcreations.upcominggames.utilities.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListViewModel(application: Application) : AndroidViewModel(application) {

    private val gameRepository = App.gameRepository

    val updateState = gameRepository.updateState
    val databaseState = gameRepository.databaseState

    init {
        /**
         * By initializing the value of databaseState here rather than in GameRepository, we fix
         * a bug wherein the indeterminate ProgressBar wouldn't appear when user presses the Back
         * button from ListFragment and then immediately re-opens the app. This would occur because
         * GameRepository isn't immediately destroyed when the app process ends, so databaseState
         * wasn't being reinitialized to DatabaseState.Loading.
         *
         * This also solves a problem wherein system-initiated process death occurring in
         * FilterFragment would prevent gameList from appearing in ListFragment.
         */
        gameRepository.updateDatabaseState(DatabaseState.Loading)
    }

    // When the filterOptions LiveData changes, re-fetch gameList with new filterOptions.
    val gameList = Transformations.switchMap(gameRepository.filterOptions) {
        gameRepository.getGameList(it)
    }

    private val _navigateToDetailFragment = MutableLiveData<Event<Game>>()
    val navigateToDetailFragment: LiveData<Event<Game>>
        get() = _navigateToDetailFragment

    private val _navigateToFilterFragment = MutableLiveData<Event<Boolean>>()
    val navigateToFilterFragment: LiveData<Event<Boolean>>
        get() = _navigateToFilterFragment

    private val _navigateToSearchFragment = MutableLiveData<Event<Boolean>>()
    val navigateToSearchFragment: LiveData<Event<Boolean>>
        get() = _navigateToSearchFragment

    private val _navigateToFavoriteFragment = MutableLiveData<Event<Boolean>>()
    val navigateToFavoriteFragment: LiveData<Event<Boolean>>
        get() = _navigateToFavoriteFragment

    private val _requestUpdateDatabase = MutableLiveData<Event<Boolean>>()
    val requestUpdateDatabase: LiveData<Event<Boolean>>
        get() = _requestUpdateDatabase

    fun onNavigateToDetailFragment(game: Game) {
        _navigateToDetailFragment.value = Event(game)
    }

    fun onNavigateToFilterFragment() {
        _navigateToFilterFragment.value = Event(true)
    }

    fun onNavigateToSearchFragment() {
        _navigateToSearchFragment.value = Event(true)
    }

    fun onNavigateToFavoriteFragment() {
        _navigateToFavoriteFragment.value = Event(true)
    }

    fun onRequestUpdateDatabase() {
        _requestUpdateDatabase.value = Event(true)
    }

    // When the database is stale and the user presses the UPDATE button, this updates the database.
    fun updateDatabaseManually() {
        CoroutineScope(Dispatchers.Default).launch {
            gameRepository.updateGameListData(true)
        }
    }

    /**
     * The observer that triggers this method fires once under normal circumstances, but fires
     * twice if the filter options change. When filter options change, the Success state doesn't occur
     * until the second firing. So in this case, DatabaseState transitions from LoadingFilterChange
     * to Loading, and then finally to Success.
     */
    fun updateDatabaseState() {
        when (databaseState.value) {
            DatabaseState.LoadingFilterChange -> gameRepository.updateDatabaseState(DatabaseState.Loading)
            DatabaseState.Loading -> gameRepository.updateDatabaseState(DatabaseState.Success)
            DatabaseState.Success -> return
        }
    }
}



