package com.gavinsappcreations.upcominggames.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DetailViewModel(application: Application) : AndroidViewModel(application) {





    //Factory for constructing DetailViewModel with Application parameter.
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DetailViewModel(
                    application
                ) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}



