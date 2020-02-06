package com.gavinsappcreations.upcominggames.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.gavinsappcreations.upcominggames.database.asDomainModel
import com.gavinsappcreations.upcominggames.database.getDatabase
import com.gavinsappcreations.upcominggames.domain.Game
import com.gavinsappcreations.upcominggames.network.GameNetwork
import com.gavinsappcreations.upcominggames.network.asDatabaseModel
import com.gavinsappcreations.upcominggames.utilities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GamesRepository(application: Application) {

    private val database = getDatabase(application)

    /**
     * A list of games that can be shown on the screen.
     */
    val games: LiveData<List<Game>> =
        Transformations.map(database.gameDao.getGames()) {
            it.asDomainModel().removeGamesWithoutReleaseDates()
        }

    suspend fun downloadGameData() {
        val gameList = GameNetwork.gameData.getGameData(
            API_KEY,
            "json",
            "${GameApiSortField.OriginalReleaseDate.sortField}:${GameApiSortDirection.Ascending}",
            "${GameApiSortField.OriginalReleaseDate.sortField}:2019-03-30|2020-06-01," +
                    "${GameApiSortField.Platforms.sortField}:${Platform.PC.platformId}," +
                    "${Platform.XONE.platformId},${Platform.NSW.platformId},${Platform.PS4.platformId}}",
            "id,deck,description,name,original_game_rating,image,platforms," +
                    "original_release_date,expected_release_day,expected_release_month," +
                    "expected_release_year"
        ).body()!!.games


        withContext(Dispatchers.IO) {
            database.gameDao.insertAll(gameList.asDatabaseModel())
        }

    }


}

