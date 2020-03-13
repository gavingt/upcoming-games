package com.gavinsappcreations.upcominggames.ui.list

import android.app.Application
import androidx.lifecycle.*
import com.gavinsappcreations.upcominggames.domain.Game
import com.gavinsappcreations.upcominggames.repository.GameRepository
import com.gavinsappcreations.upcominggames.utilities.DatabaseState
import com.gavinsappcreations.upcominggames.utilities.Event
import kotlinx.coroutines.launch

class ListViewModel(application: Application) : AndroidViewModel(application) {

    private val gameRepository = GameRepository.getInstance(application)

    init {
        viewModelScope.launch {
            //TODO: on first load, update database (show loading indicator)
            //gamesRepository.downloadGameListData()
        }
    }

    val databaseState = gameRepository.databaseState

    // When the sortOptions LiveData changes, switchMap sets gameList = gameRepository.getGameList(it).
    val gameList = Transformations.switchMap(gameRepository.sortOptions) {
        gameRepository.getGameList(it)
    }

    private val _navigateToDetailFragment = MutableLiveData<Event<Game>>()
    val navigateToDetailFragment: LiveData<Event<Game>>
        get() = _navigateToDetailFragment

    private val _navigateToSortFragment = MutableLiveData<Event<Boolean>>()
    val navigateToSortFragment: LiveData<Event<Boolean>>
        get() = _navigateToSortFragment

    private val _navigateToSearchFragment = MutableLiveData<Event<Boolean>>()
    val navigateToSearchFragment: LiveData<Event<Boolean>>
        get() = _navigateToSearchFragment


    fun onNavigateToDetailFragment(game: Game) {
        _navigateToDetailFragment.value = Event(game)
    }

    fun onNavigateToSortFragment() {
        _navigateToSortFragment.value = Event(true)
    }

    fun onNavigateToSearchFragment() {
        _navigateToSearchFragment.value = Event(true)
    }


    /**
     * The observer that triggers this method fires once under normal circumstances, but fires
     * twice if the sort options change. When sort options change, the Success state doesn't occur
     * until the second firing. So in this case, DatabaseState transitions from LoadingSortChange to
     * Loading, and then finally to Success.
     */
    fun updateDatabaseState() {
        when (databaseState.value) {
            DatabaseState.LoadingSortChange -> gameRepository.updateDatabaseState(DatabaseState.Loading)
            DatabaseState.Loading -> gameRepository.updateDatabaseState(DatabaseState.Success)
            DatabaseState.Success -> return
        }
    }



/*    fun getAllGames() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val allGames = gameRepository.getAllGames()

                for (game in allGames) {

                    gameRepository.updateGame(game)
                }

                Log.d("LOG", "hello")
            }
        }
    }*/


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



