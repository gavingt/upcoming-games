package com.gavinsappcreations.upcominggames.repository

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.paging.PositionalDataSource
import com.gavinsappcreations.upcominggames.database.getDatabase
import com.gavinsappcreations.upcominggames.domain.Game
import com.gavinsappcreations.upcominggames.domain.GameDetail
import com.gavinsappcreations.upcominggames.domain.SortOptions
import com.gavinsappcreations.upcominggames.network.GameBoundaryCallback
import com.gavinsappcreations.upcominggames.network.GameNetwork
import com.gavinsappcreations.upcominggames.network.asDatabaseModel
import com.gavinsappcreations.upcominggames.network.asDomainModel
import com.gavinsappcreations.upcominggames.utilities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GameRepository private constructor (application: Application) {

    private val database = getDatabase(application)

    val prefs =
        application.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val _sortOptions = MutableLiveData<SortOptions>()
    val sortOptions: LiveData<SortOptions>
        get() = _sortOptions

    init {
        // Fetch sort options from SharedPrefs
        val sortDirection: SortDirection = enumValueOf(prefs.getString(KEY_SORT_DIRECTION, SortDirection.Ascending.name)!!)

        // Set all sort options to _sortOptions at once
        _sortOptions.value = SortOptions(sortDirection)
    }

    fun updateSortOptions(newSortOptions: SortOptions) {
        _sortOptions.value = newSortOptions

        // TODO: update value of _sortOptions and also save that value to SharedPrefs
        prefs.edit().putString(KEY_SORT_DIRECTION, _sortOptions.value!!.sortDirectionSelection.name)
            .apply()
    }


    fun getGameList(): LiveData<PagedList<Game>> {

        // Get data source factory from the local cache
        val dataSourceFactory = database.gameDao.getGames(_sortOptions.value!!.sortDirectionSelection.direction)

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

    suspend fun downloadGameListData(offset: Int) {
        val gameList = GameNetwork.gameData.getGameListData(
            API_KEY,
            "${ApiField.Json}",
            "${ApiField.OriginalReleaseDate}:${SortDirection.Ascending}",
            "",
            "${ApiField.Id}," +
                    "${ApiField.Guid}," +
                    "${ApiField.Name}," +
                    "${ApiField.Image}," +
                    "${ApiField.Platforms}," +
                    "${ApiField.OriginalReleaseDate}," +
                    "${ApiField.ExpectedReleaseDay}," +
                    "${ApiField.ExpectedReleaseMonth}," +
                    "${ApiField.ExpectedReleaseYear}," +
                    "${ApiField.ExpectedReleaseQuarter}",
            offset
        ).body()!!.games

        withContext(Dispatchers.IO) {
            database.gameDao.insertAll(gameList.asDatabaseModel())
        }
    }


    suspend fun downloadGameDetailData(guid: String): GameDetail {
        return GameNetwork.gameData.getGameDetailData(
            guid,
            API_KEY,
            "json",
            "id,guid,name,image,images,platforms," +
                    "original_release_date,expected_release_day,expected_release_month," +
                    "expected_release_year,expected_release_quarter,original_game_rating,developers,publishers,genres," +
                    "deck"
        ).body()!!.gameDetails.asDomainModel()
    }


    companion object {

        // For Singleton instantiation
        @Volatile private var instance: GameRepository? = null

        fun getInstance(application: Application) =
            instance ?: synchronized(this) {
                instance ?: GameRepository(application).also { instance = it }
            }
    }

}

