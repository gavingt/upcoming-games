package com.gavinsappcreations.upcominggames.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gavinsappcreations.upcominggames.network.GameNetwork
import com.gavinsappcreations.upcominggames.ui.utilities.API_KEY
import kotlinx.coroutines.launch

class ListViewModel : ViewModel() {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message

    init {
        _message.value = "hello"

        viewModelScope.launch {
            val sunData = GameNetwork.gameData.getGameData(
                API_KEY,
                "json",
                "release_date:asc",
                "release_date:2020-02-06|2020-06-01",
                "id,deck,description,game,game_rating,image,maximum_players,minimum_players,platform,region,release_date,expected_release_day,expected_release_month,expected_release_year,expected_release_quarter"
            )

            val test = 6
        }
    }
}

