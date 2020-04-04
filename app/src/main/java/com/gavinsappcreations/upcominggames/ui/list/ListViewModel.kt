package com.gavinsappcreations.upcominggames.ui.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.gavinsappcreations.upcominggames.domain.DatabaseState
import com.gavinsappcreations.upcominggames.domain.Game
import com.gavinsappcreations.upcominggames.repository.GameRepository
import com.gavinsappcreations.upcominggames.utilities.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListViewModel(application: Application) : AndroidViewModel(application) {

    private val gameRepository = GameRepository.getInstance(application)

    val updateState = gameRepository.updateState
    val databaseState = gameRepository.databaseState

    init {
        // Fixes a bug involving system-initiated process death.
        checkForProcessDeath()
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


    // This fixes a bug that would prevent gameList from showing on system-initiated process death.
    private fun checkForProcessDeath() {
        /**
         * This condition indicates process death, because the value of @link [databaseState] would not
         * normally be @link [DatabaseState.LoadingFilterChange] upon initialization of ListViewModel
         * (since ListFragment should be on the back stack, and hence already initialized, any
         * time a user changes the filter options).
         */
        val systemInitiatedProcessDeathOccurred =
            databaseState.value == DatabaseState.LoadingFilterChange

        if (systemInitiatedProcessDeathOccurred) {
            /**
             * If system-initiated process death occurs while in FilterFragment and the user then presses
             * APPLY, this ensures that the ProgressBar and RecyclerView visibilities are still set
             * correctly. This is required because @link [gameList] will be null in this case, so the
             * @link [DatabaseState.LoadingFilterChange] state needs to be bypassed (as the observer
             * observing @link [gameList] won't emit a value).
             */
            updateDatabaseState()
        }
    }
}



