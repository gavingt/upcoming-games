package com.gavinsappcreations.upcominggames.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel : ViewModel() {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message



}
