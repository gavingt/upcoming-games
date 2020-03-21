package com.gavinsappcreations.upcominggames.ui.detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.gavinsappcreations.upcominggames.domain.GameDetail
import com.gavinsappcreations.upcominggames.repository.GameRepository
import com.gavinsappcreations.upcominggames.utilities.Event
import kotlinx.coroutines.launch

class DetailViewModel(application: Application, val guid: String) : AndroidViewModel(application) {

    private val gamesRepository = GameRepository.getInstance(application)

    private val _gameDetail = MutableLiveData<GameDetail?>()
    val gameDetail: LiveData<GameDetail?>
        get() = _gameDetail

    private val _networkState = MutableLiveData<DetailNetworkState>()
    val detailNetworkState: LiveData<DetailNetworkState>
        get() = _networkState

    private val _popBackStack = MutableLiveData<Event<Boolean>>()
    val popBackStack: LiveData<Event<Boolean>>
        get() = _popBackStack

    private val _navigateToScreenshotFragment = MutableLiveData<Event<Int>>()
    val navigateToScreenshotFragment: LiveData<Event<Int>>
        get() = _navigateToScreenshotFragment

    init {
        downloadGameDetailData()
    }

    fun downloadGameDetailData() {
        _networkState.value = DetailNetworkState.Loading
        viewModelScope.launch {
            try {
                _gameDetail.value = gamesRepository.downloadGameDetailData(guid)
                _networkState.value = DetailNetworkState.Success
            } catch (e: Exception) {
                Log.d("LOG", "Error: ${e.message ?: "No message"}")
                _networkState.value = DetailNetworkState.Failure
            }
        }
    }

    fun onPopBackStack() {
        _popBackStack.value = Event(true)
    }

    fun onNavigateToScreenshotFragment(imageIndex: Int) {
        _navigateToScreenshotFragment.value = Event(imageIndex)
    }

    //Factory for constructing DetailViewModel with Application parameter.
    class Factory(private val application: Application, private val guid: String) :
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



