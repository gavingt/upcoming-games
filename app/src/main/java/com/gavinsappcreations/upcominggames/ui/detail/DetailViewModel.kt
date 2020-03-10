package com.gavinsappcreations.upcominggames.ui.detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.gavinsappcreations.upcominggames.domain.GameDetail
import com.gavinsappcreations.upcominggames.repository.GameRepository
import com.gavinsappcreations.upcominggames.utilities.NetworkState
import kotlinx.coroutines.launch

class DetailViewModel(application: Application, guid: String) : AndroidViewModel(application) {

    private val gamesRepository = GameRepository.getInstance(application)

    private val _gameDetail = MutableLiveData<GameDetail?>()
    val gameDetail: LiveData<GameDetail?>
        get() = _gameDetail

    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        get() = _networkState

    init {
        _networkState.value = NetworkState.Loading
        viewModelScope.launch {
            try {
                _gameDetail.value = gamesRepository.downloadGameDetailData(guid)
                _networkState.value = NetworkState.Success
            } catch (e: Exception) {
                Log.d("LOG", "Error: ${e.message ?: "No message"}")
                _networkState.value = NetworkState.Failure
            }
        }
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



