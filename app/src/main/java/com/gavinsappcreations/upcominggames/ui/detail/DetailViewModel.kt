package com.gavinsappcreations.upcominggames.ui.detail

import android.app.Application
import androidx.lifecycle.*
import com.gavinsappcreations.upcominggames.domain.GameDetail
import com.gavinsappcreations.upcominggames.repository.GameRepository
import kotlinx.coroutines.launch

class DetailViewModel(application: Application, guid: String) : AndroidViewModel(application) {

    private val gamesRepository = GameRepository(application)

    private val _gameDetail = MutableLiveData<GameDetail?>()
    val gameDetail: LiveData<GameDetail?>
        get() = _gameDetail

    init {
        viewModelScope.launch {
            _gameDetail.value = gamesRepository.downloadGameDetailData(guid)
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



