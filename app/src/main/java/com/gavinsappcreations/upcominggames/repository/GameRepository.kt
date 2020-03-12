package com.gavinsappcreations.upcominggames.repository

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.gavinsappcreations.upcominggames.database.buildGameListQuery
import com.gavinsappcreations.upcominggames.database.getDatabase
import com.gavinsappcreations.upcominggames.domain.Game
import com.gavinsappcreations.upcominggames.domain.GameDetail
import com.gavinsappcreations.upcominggames.domain.SortOptions
import com.gavinsappcreations.upcominggames.network.GameNetwork
import com.gavinsappcreations.upcominggames.network.asDatabaseModel
import com.gavinsappcreations.upcominggames.network.asDomainModel
import com.gavinsappcreations.upcominggames.utilities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class GameRepository private constructor(application: Application) {

    private val database = getDatabase(application)

    private val prefs: SharedPreferences =
        application.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val _sortOptions = MutableLiveData<SortOptions>()
    val sortOptions: LiveData<SortOptions>
        get() = _sortOptions

    private val _loadingState = MutableLiveData(DatabaseState.Loading)
    val databaseState: LiveData<DatabaseState>
        get() = _loadingState

    init {
        // Fetch sort options from SharedPrefs
        val releaseDateType: ReleaseDateType = enumValueOf(
            prefs.getString(KEY_RELEASE_DATE_TYPE, ReleaseDateType.RecentAndUpcoming.name)!!
        )
        val sortDirection: SortDirection =
            enumValueOf(prefs.getString(KEY_SORT_DIRECTION, SortDirection.Ascending.name)!!)

        val customDateStart = prefs.getString(KEY_CUSTOM_DATE_START, "")!!
        val customDateEnd = prefs.getString(KEY_CUSTOM_DATE_END, "")!!

        val platformType: PlatformType = enumValueOf(
            prefs.getString(KEY_PLATFORM_TYPE, PlatformType.CurrentGeneration.name)!!
        )

        val platformIndices: MutableSet<Int> =
            prefs.getStringSet(KEY_PLATFORM_INDICES, setOf())!!.map {
                it.toInt()
            }.toMutableSet()


        // Set all sort options to _sortOptions at once
        _sortOptions.value = SortOptions(
            releaseDateType,
            sortDirection,
            customDateStart,
            customDateEnd,
            platformType,
            platformIndices
        )
    }

    // Update value of _sortOptions and also save that value to SharedPrefs.
    fun saveNewSortOptions(newSortOptions: SortOptions) {

        _loadingState.value = DatabaseState.LoadingSortChange

        _sortOptions.value = newSortOptions

        prefs.edit().putString(KEY_RELEASE_DATE_TYPE, newSortOptions.releaseDateType.name)
            .putString(KEY_SORT_DIRECTION, newSortOptions.sortDirection.name)
            .putString(KEY_CUSTOM_DATE_START, newSortOptions.customDateStart)
            .putString(KEY_CUSTOM_DATE_END, newSortOptions.customDateEnd)
            .putString(KEY_PLATFORM_TYPE, newSortOptions.platformType.name)
            // Convert MutableSet<Int> to Set<String> so we can store it in SharedPrefs
            .putStringSet(KEY_PLATFORM_INDICES, newSortOptions.platformIndices.map {
                it.toString()
            }.toSet())
            .apply()
    }


    fun updateDatabaseState(newState: DatabaseState) {
        _loadingState.value = newState
    }


    fun getGameList(newSortOptions: SortOptions): LiveData<PagedList<Game>> {

        val dateConstraints = fetchDateConstraints(newSortOptions)

        val query = buildGameListQuery(
            newSortOptions.sortDirection.direction,
            dateConstraints[0],
            dateConstraints[1],
            fetchPlatformIndices(newSortOptions)
        )

        val dataSourceFactory: DataSource.Factory<Int, Game> = database.gameDao.getGameList(query)

        val data: LiveData<PagedList<Game>> =
            LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
                .build()

        return data
    }


    private fun fetchPlatformIndices(sortOptions: SortOptions): Set<Int> {
        val platformIndices = mutableSetOf<Int>()

        return when (sortOptions.platformType) {
            PlatformType.CurrentGeneration -> {
                // TODO: make this a constant or something so it's explicit which consoles are considered current
                platformIndices.apply { addAll(0..14) }
            }
            PlatformType.All -> platformIndices.apply { addAll(allPlatforms.indices) }
            PlatformType.PickFromList -> sortOptions.platformIndices
        }
    }


    private fun fetchDateConstraints(sortOptions: SortOptions): Array<Long?> {

        var dateStartMillis: Long?
        var dateEndMillis: Long?

        val calendar: Calendar = Calendar.getInstance()
        val currentTimeMillis = calendar.timeInMillis

        when (sortOptions.releaseDateType) {
            ReleaseDateType.RecentAndUpcoming -> {
                // dateFilterStart is set to one week before current day.
                calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 7)
                dateStartMillis = calendar.timeInMillis

                // dateFilterEnd is set to a far-off date so that every future game will be listed.
                calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 100)
                dateEndMillis = calendar.timeInMillis
            }
            ReleaseDateType.Any -> {
                dateStartMillis = null
                dateEndMillis = null
            }
            ReleaseDateType.PastMonth -> {
                // dateFilterStart is set to one month before current day.
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1)
                dateStartMillis = calendar.timeInMillis

                // dateFilterEnd is set to current time.
                dateEndMillis = currentTimeMillis
            }
            ReleaseDateType.PastYear -> {
                // dateFilterStart is set to one year before current day.
                calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1)
                dateStartMillis = calendar.timeInMillis

                // dateFilterEnd is set to current time.
                dateEndMillis = currentTimeMillis
            }
            ReleaseDateType.CustomDate -> {
                val df = SimpleDateFormat("MM/dd/yyyy")

                val startDateString = sortOptions.customDateStart
                calendar.time = df.parse(startDateString)!!
                dateStartMillis = calendar.timeInMillis

                val endDateString = sortOptions.customDateEnd
                calendar.time = df.parse(endDateString)!!
                dateEndMillis = calendar.timeInMillis

                // If the end date is before the start date, just flip them.
                if (dateEndMillis < dateStartMillis) {
                    val temp = dateStartMillis
                    dateStartMillis = dateEndMillis
                    dateEndMillis = temp
                }
            }
        }

        return arrayOf(dateStartMillis, dateEndMillis)
    }


    // TODO: modify this to fetch new games with WorkManager (query API using lastUpdated field as filter)
    suspend fun downloadGameListData(offset: Int) {
        val gameList = GameNetwork.gameData.getGameListData(
            API_KEY,
            ApiField.Json.field,
            "${ApiField.OriginalReleaseDate.field}:${SortDirection.Ascending.direction}",
            "",
            "${ApiField.Id.field},${ApiField.Guid.field},${ApiField.Name.field}," +
                    "${ApiField.Image.field},${ApiField.Platforms.field}," +
                    "${ApiField.OriginalReleaseDate.field},${ApiField.ExpectedReleaseDay.field}," +
                    "${ApiField.ExpectedReleaseMonth.field},${ApiField.ExpectedReleaseYear.field}," +
                    ApiField.ExpectedReleaseQuarter.field,
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
            ApiField.Json.field,
            "${ApiField.Id.field},${ApiField.Guid.field},${ApiField.Name.field}," +
                    "${ApiField.Image.field},${ApiField.Images.field},${ApiField.Platforms.field}," +
                    "${ApiField.OriginalReleaseDate.field},${ApiField.ExpectedReleaseDay.field}," +
                    "${ApiField.ExpectedReleaseMonth.field},${ApiField.ExpectedReleaseYear.field}," +
                    "${ApiField.ExpectedReleaseQuarter.field},${ApiField.OriginalGameRating.field}," +
                    "${ApiField.Developers.field},${ApiField.Publishers.field}," +
                    "${ApiField.Genres.field},${ApiField.Deck.field},${ApiField.DetailUrl.field}"
        ).body()!!.gameDetails.asDomainModel()
    }


    companion object {
        // For Singleton instantiation
        @Volatile
        private var instance: GameRepository? = null

        fun getInstance(application: Application) =
            instance ?: synchronized(this) {
                instance ?: GameRepository(application).also { instance = it }
            }
    }

}

