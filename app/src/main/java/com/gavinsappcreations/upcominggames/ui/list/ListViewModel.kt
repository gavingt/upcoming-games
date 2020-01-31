package com.gavinsappcreations.upcominggames.ui.list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gavinsappcreations.upcominggames.domain.Game
import com.gavinsappcreations.upcominggames.network.GameNetwork
import com.gavinsappcreations.upcominggames.network.asDomainModel
import com.gavinsappcreations.upcominggames.ui.utilities.API_KEY
import com.gavinsappcreations.upcominggames.utilities.removeBadGameData
import kotlinx.coroutines.launch

class ListViewModel : ViewModel() {

    private val _releases = MutableLiveData<List<Game>>()
    val releases: LiveData<List<Game>>
        get() = _releases

    init {
        viewModelScope.launch {
            val gameData = GameNetwork.gameData.getGameData(
                API_KEY,
                "json",
                "original_release_date:asc",
                "original_release_date:2019-03-30|2020-06-01",
                "id,deck,description,name,original_game_rating,image,platforms,original_release_date,expected_release_day,expected_release_month,expected_release_year"
            )

            _releases.value = gameData.body()!!.games.map {
                it.asDomainModel()
            }.removeBadGameData()

            Log.d("LOG", "")
        }

    }
}




