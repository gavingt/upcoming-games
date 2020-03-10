package com.gavinsappcreations.upcominggames.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.PositionalDataSource
import com.gavinsappcreations.upcominggames.domain.Game


class MyPositionalDataSource: PositionalDataSource<Game>() {

    private val networkState: MutableLiveData<String>? = null

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Game>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Game>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}