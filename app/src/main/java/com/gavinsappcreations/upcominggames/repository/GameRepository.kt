package com.gavinsappcreations.upcominggames.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.gavinsappcreations.upcominggames.database.getDatabase
import com.gavinsappcreations.upcominggames.domain.Game
import com.gavinsappcreations.upcominggames.network.GameBoundaryCallback
import com.gavinsappcreations.upcominggames.network.GameNetwork
import com.gavinsappcreations.upcominggames.network.asDatabaseModel
import com.gavinsappcreations.upcominggames.utilities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GamesRepository(application: Application) {

    companion object {
        private const val DATABASE_PAGE_SIZE = 20
    }

    private val database = getDatabase(application)


    fun getGameList(): LiveData<PagedList<Game>> {
        // Get data source factory from the local cache
        val dataSourceFactory = database.gameDao.getGames()

        // every new query creates a new BoundaryCallback
        // The BoundaryCallback will observe when the user reaches to the edges of
        // the list and update the database with extra data
        val boundaryCallback = GameBoundaryCallback(this)
        //val networkErrors = boundaryCallback.networkErrors

        // Get the paged list
        val data = LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
            .setBoundaryCallback(boundaryCallback)
            .build()

        // Get the network errors exposed by the boundary callback
        //return RepoSearchResult(data, networkErrors)
        return data
    }


    suspend fun downloadGameData(offset: Int) {
        val gameList = GameNetwork.gameData.getGameData(
            API_KEY,
            "json",
            "${GameApiSortField.OriginalReleaseDate.sortField}:${GameApiSortDirection.Ascending}",
            "${GameApiSortField.OriginalReleaseDate.sortField}:2019-03-30|2020-06-01," +
                    "${GameApiSortField.Platforms.sortField}:${Platform.PC.platformId}," +
                    "${Platform.XONE.platformId},${Platform.NSW.platformId},${Platform.PS4.platformId}}",
            "id,deck,description,name,original_game_rating,image,platforms," +
                    "original_release_date,expected_release_day,expected_release_month," +
                    "expected_release_year",
            offset
        ).body()!!.games


        withContext(Dispatchers.IO) {
            Log.d("RepoDatabase", "downloaded ${gameList.size} games")
            database.gameDao.insertAll(gameList.asDatabaseModel())
        }

    }


}

