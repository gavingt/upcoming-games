package com.gavinsappcreations.upcominggames.ui.screenshot

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/*class ScreenshotViewModel(val images: Array<String>, currentImageIndex: Int) : ViewModel() {

    private val _currentImageIndex = MutableLiveData<Int>()
    val currentImageIndex: LiveData<Int>
        get() = _currentImageIndex

    init {
        _currentImageIndex.value = currentImageIndex
    }

    //Factory for constructing DetailViewModel with Application parameter.
    class Factory(private val images: Array<String>, private val currentImageIndex: Int) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ScreenshotViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ScreenshotViewModel(
                    images, currentImageIndex
                ) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}*/
