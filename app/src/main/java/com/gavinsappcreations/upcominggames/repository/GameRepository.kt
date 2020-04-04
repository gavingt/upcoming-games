package com.gavinsappcreations.upcominggames.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.gavinsappcreations.upcominggames.database.buildGameListQuery
import com.gavinsappcreations.upcominggames.database.getDatabase
import com.gavinsappcreations.upcominggames.domain.*
import com.gavinsappcreations.upcominggames.network.GameNetwork
import com.gavinsappcreations.upcominggames.network.NetworkGameContainer
import com.gavinsappcreations.upcominggames.network.asDomainModel
import com.gavinsappcreations.upcominggames.utilities.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class GameRepository private constructor(application: Context) {

    private val database = getDatabase(application)

    private val prefs: SharedPreferences =
        application.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    /**
     * Every time the user changes the search term, we check whether the previous @link [Job] is
     * still active. If it is, we cancel the old @link [Job] and create a new one.
     */
    private var searchJob = Job()

    // Stores the recent games searched for by the user.
    private var recentSearches = fetchRecentSearches()

    // Stores the filter options specified by the user in FilterFragment.
    private val _filterOptions = MutableLiveData<FilterOptions>()
    val filterOptions: LiveData<FilterOptions>
        get() = _filterOptions

    // Stores the state of database operations, allowing us to show a ProgressBar when loading.
    private val _databaseState = MutableLiveData(DatabaseState.Loading)
    val databaseState: LiveData<DatabaseState>
        get() = _databaseState

    /**
     * Stores the state of network operations which update the database, allowing us to show a
     * ProgressBar when downloading data or to warn the user when the database is out-of-date.
     */
    private val _updateState = MutableLiveData<UpdateState>()
    val updateState: LiveData<UpdateState>
        get() = _updateState


    // Initialize filterOptions and updateState by reading from SharedPrefs.
    init {
        initializeFilterOptions()
        initializeUpdateState()
    }


    /**
     * Fetch [filterOptions] from SharedPrefs.
     */
    private fun initializeFilterOptions() {
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

        // Set all filter options fetched from SharedPrefs to _filterOptions at once.
        _filterOptions.value = FilterOptions(
            releaseDateType,
            sortDirection,
            customDateStart,
            customDateEnd,
            platformType,
            platformIndices
        )
    }

    /**
     * Fetch [updateState] from SharedPrefs.
     */
    private fun initializeUpdateState() {
        val timeLastUpdated =
            prefs.getLong(KEY_TIME_LAST_UPDATED_IN_MILLIS, ORIGINAL_TIME_LAST_UPDATED_IN_MILLIS)

        // If user is running app for the first time, we update the database immediately.
        if (timeLastUpdated == ORIGINAL_TIME_LAST_UPDATED_IN_MILLIS) {
            _updateState.value = UpdateState.Updating(0, 0)
            CoroutineScope(Dispatchers.Default).launch {
                updateGameListData(false)
            }
        } else {
            if (isDataStale(timeLastUpdated)) {
                // If database hasn't been updated in over 2 days, this will alert the user.
                _updateState.value = UpdateState.DataStale
            } else {
                // If database is recently updated, this will show the ProgressBar.
                _updateState.value = UpdateState.Updated
            }
        }
    }


    /**
     * Update value of [filterOptions] and also save that value to SharedPrefs.
     */
    fun updateFilterOptions(newFilterOptions: FilterOptions) {
        _databaseState.value = DatabaseState.LoadingFilterChange
        _filterOptions.value = newFilterOptions

        prefs.edit().putString(KEY_RELEASE_DATE_TYPE, newFilterOptions.releaseDateType.name)
            .putString(KEY_SORT_DIRECTION, newFilterOptions.sortDirection.name)
            .putString(KEY_CUSTOM_DATE_START, newFilterOptions.customDateStart)
            .putString(KEY_CUSTOM_DATE_END, newFilterOptions.customDateEnd)
            .putString(KEY_PLATFORM_TYPE, newFilterOptions.platformType.name)
            // Convert MutableSet<Int> to Set<String> so we can store it in SharedPrefs
            .putStringSet(KEY_PLATFORM_INDICES, newFilterOptions.platformIndices.map {
                it.toString()
            }.toSet())
            .apply()
    }


    fun updateDatabaseState(newState: DatabaseState) {
        _databaseState.value = newState
    }


    // Fetch list of games to display in ListFragment, based on filter options specified by user.
    fun getGameList(newFilterOptions: FilterOptions): LiveData<PagedList<Game>> {
        val dateConstraints = fetchDateConstraints(newFilterOptions)

        val query = buildGameListQuery(
            newFilterOptions.sortDirection.direction,
            dateConstraints[0],
            dateConstraints[1],
            fetchPlatformIndices(newFilterOptions)
        )

        val dataSourceFactory: DataSource.Factory<Int, Game> = database.gameDao.getGameList(query)
        return LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
            .build()
    }


    // Fetch list of user's favorite games to display in FavoriteFragment.
    fun getFavoriteList(): LiveData<PagedList<Game>> {
        val dataSourceFactory: DataSource.Factory<Int, Game> = database.gameDao.getFavoriteList()
        return LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
            .build()
    }

    // Gets the isFavorite property of a Game, so we can display the correct star drawable in DetailFragment.
    fun getIsFavorite(guid: String): Boolean {
        return database.gameDao.getIsFavorite(guid)
    }

    /**
     * Updates the isFavorite property of a Game when user clicks the star drawable in DetailFragment.
     * @param isFavorite true if game is currently a favorite, false if not.
     * @param guid guid of current game being updated.
     * @return number of rows updated, which should always be 1.
     */
    fun updateFavorite(isFavorite: Boolean, guid: String): Int {
        return database.gameDao.updateFavorite(isFavorite, guid)
    }


    /**
     * Fetches list of user's recently searched games from SharedPrefs, so we can display them
     * prominently in results in SearchFragment.
     */
    private fun fetchRecentSearches(): ArrayList<SearchResult> {
        val recentSearchesString = prefs.getString(KEY_RECENT_SEARCHES, null)
        return if (recentSearchesString == null) {
            arrayListOf()
        } else {
            val type = object : TypeToken<ArrayList<SearchResult>>() {}.type
            Gson().fromJson(recentSearchesString, type)
        }
    }


    // Updates list of user's recent searches whenever user click on a result from SearchFragment.
    fun updateRecentSearches(newSearch: SearchResult) {
        newSearch.isRecentSearch = true
        if (!recentSearches.contains(newSearch)) {
            recentSearches.add(newSearch)
            val recentSearchesTrimmed = if (recentSearches.size > 4) {
                recentSearches.subList(recentSearches.size - 4, recentSearches.size)
            } else {
                recentSearches
            }
            val jsonText: String = Gson().toJson(recentSearchesTrimmed)
            prefs.edit().putString(KEY_RECENT_SEARCHES, jsonText).apply()
        }
    }


    /**
     * Gets games for SearchFragment, based on the search query typed by the user.
     */
    suspend fun searchGameList(searchString: String): ArrayList<SearchResult> {

        // Clean up search string and prepare it for use in SQLite query.
        val query = if (searchString.trim().isEmpty()) {
            searchString
        } else {
            "%${searchString.replace(' ', '%')}%"
        }

        /**
         * If previous job hasn't been completed, cancel it. This ensures that coroutines don't
         * finish out of order if the user types quickly into the search box, leading to incorrect
         * search results being displayed.
         */
        if (searchJob.isActive) {
            searchJob.cancel()
            // Create a new Job.
            searchJob = Job()
        }

        // Create a new CoroutineScope from Job.
        val searchCoroutineScope = CoroutineScope(searchJob + Dispatchers.IO)

        // Perform SQLite query.
        val searchResults =
            withContext(searchCoroutineScope.coroutineContext) {
                database.gameDao.searchGameList(query).map {
                    SearchResult(it, false)
                } as ArrayList<SearchResult>
            }

        // Fetch user's recent search results.
        val recentSearchResults = fetchRecentSearches()
        recentSearchResults.reverse()
        // if searchString is empty, just show recentSearchResults in reverse order.
        if (searchString.isEmpty()) {
            searchResults.addAll(recentSearchResults)
        } else {
            /**
             * If searchString not empty, check if results contain games in recent searches. If
             * they do, change their isRecentSearch properties to true so the correct drawable
             * will appear to indicate they were recently searched for.
             */
            for (recentResult in recentSearchResults) {
                if (recentResult.game.gameName.contains(searchString, true)) {
                    val matchingSearchResult = searchResults.find {
                        it.game == recentResult.game
                    }
                    matchingSearchResult?.isRecentSearch = true
                }
            }
        }

        return searchResults
    }


    /**
     * For the platforms selected by the user in FilterFragment, this fetches their corresponding
     * indices in the @link [allKnownPlatforms] list.
     */
    private fun fetchPlatformIndices(filterOptions: FilterOptions): Set<Int> {
        val platformIndices = mutableSetOf<Int>()

        return when (filterOptions.platformType) {
            PlatformType.CurrentGeneration -> {
                platformIndices.apply {
                    addAll(currentGenerationPlatformRange)
                }
            }
            PlatformType.All -> platformIndices.apply { addAll(allKnownPlatforms.indices) }
            PlatformType.PickFromList -> filterOptions.platformIndices
        }
    }


    /**
     * Fetch the actual start and end dates to filter the games shown in ListFragment by, based on
     * the filter options specified by the user in FilterFragment.
     */
    private fun fetchDateConstraints(filterOptions: FilterOptions): Array<Long?> {
        var dateStartMillis: Long?
        var dateEndMillis: Long?

        val calendar: Calendar = Calendar.getInstance()
        // Make it so hour, minute, second, and millisecond don't affect the timeInMillis returned.
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val currentTimeMillis = calendar.timeInMillis

        when (filterOptions.releaseDateType) {
            ReleaseDateType.RecentAndUpcoming -> {
                // dateStartMillis is set to one week before current day.
                calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 7)
                dateStartMillis = calendar.timeInMillis

                // dateEndMillis is set to a far-off date so that every future game will be listed.
                calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 100)
                dateEndMillis = calendar.timeInMillis
            }
            ReleaseDateType.Any -> {
                dateStartMillis = null
                dateEndMillis = null
            }
            ReleaseDateType.PastMonth -> {
                // dateStartMillis is set to one month before current day.
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1)
                dateStartMillis = calendar.timeInMillis

                // dateEndMillis is set to current time.
                dateEndMillis = currentTimeMillis
            }
            ReleaseDateType.PastYear -> {
                // dateStartMillis is set to one year before current day.
                calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1)
                dateStartMillis = calendar.timeInMillis

                // dateEndMillis is set to current time.
                dateEndMillis = currentTimeMillis
            }
            ReleaseDateType.CustomDate -> {
                val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.US)

                val startDateString = filterOptions.customDateStart
                calendar.time = formatter.parse(startDateString)!!
                dateStartMillis = calendar.timeInMillis

                val endDateString = filterOptions.customDateEnd
                calendar.time = formatter.parse(endDateString)!!

                // To get the last millisecond of the day, we add a day and subtract a millisecond.
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1)
                calendar.set(Calendar.MILLISECOND, calendar.get(Calendar.MILLISECOND) - 1)

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


    /**
     * We build a query for games that have been updated in the API since the last time we
     * checked. But to reduce the data to be loaded, we further filter the query by games whose
     * release dates are either less than two months old or still upcoming (since we assume that
     * any game that's been out for at least two months already has its release date set correctly
     * in the API). */
    suspend fun updateGameListData(userInvokedUpdate: Boolean) {
        _updateState.postValue(UpdateState.Updating(0, 0))

        var offset = 0

        /**
         * First we assemble the starting and ending dates for the "date_last_updated" filter
         * field of the API. We select the date range starting from the date we last updated our
         * local database and ending on the current date plus two days (to account for any time zone
         * weirdness or possible edge cases).
         */

        val calendar: Calendar = Calendar.getInstance()
        val desiredPatternFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        val timeLastUpdated =
            prefs.getLong(KEY_TIME_LAST_UPDATED_IN_MILLIS, ORIGINAL_TIME_LAST_UPDATED_IN_MILLIS)

        calendar.timeInMillis = timeLastUpdated
        val startingDateLastUpdated = desiredPatternFormatter.format(calendar.time)

        // Set calendar to current time in order to calculate endingDateLastUpdated.
        calendar.timeInMillis = System.currentTimeMillis()
        // Add two days to current day, just to ensure we're getting all the newest data.
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 2)
        val endingDateLastUpdated = desiredPatternFormatter.format(calendar.time)

        /**
         * Next we assemble the starting and ending dates for the "original_release_date" filter
         * field of the API. We somewhat arbitrarily select this range to start at the date we last
         * updated the database minus 2 months, and the ending day to be 100 years in the future
         * (to account for all unreleased games).
         */
        calendar.timeInMillis = timeLastUpdated
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 2)
        val startingOriginalReleaseDate = desiredPatternFormatter.format(calendar.time)

        // endingOriginalReleaseDate is set to a far-off date so that every future game will be returned.
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 100)
        val endingOriginalReleaseDate = desiredPatternFormatter.format(calendar.time)

        // Initially set this to NETWORK_PAGE_SIZE so that the WHILE loop runs at least once.
        var numResultsInCurrentRequest = NETWORK_PAGE_SIZE
        var numTotalResults: Int? = null
        var oldProgress = 0

        try {
            while (numResultsInCurrentRequest == NETWORK_PAGE_SIZE) {

                Log.d("MYLOG", "updated, offset $offset")

                val networkGameContainer = downloadAndSaveGameList(
                    offset,
                    startingDateLastUpdated,
                    endingDateLastUpdated,
                    startingOriginalReleaseDate,
                    endingOriginalReleaseDate
                )

                // Set numTotalResults only on first iteration of WHILE loop.
                if (numTotalResults == null) {
                    numTotalResults = networkGameContainer.numberOfTotalResults
                }
                numResultsInCurrentRequest = networkGameContainer.numberOfPageResults

                offset += NETWORK_PAGE_SIZE

                val currentProgress = if (numTotalResults - offset < NETWORK_PAGE_SIZE) {
                    // If we're downloading the last page from the API, set progress to 100.
                    100
                } else {
                    // Find current progress by taking the ceiling of offset/numTotalResults.
                    ((offset / numTotalResults.toDouble()) * 100).toInt()
                }

                _updateState.postValue(UpdateState.Updating(oldProgress, currentProgress))
                oldProgress = currentProgress
            }

            /**
             * If all updates are successful, save the current time to TIME_LAST_UPDATED_IN_MILLIS.
             */
            prefs.edit().putLong(KEY_TIME_LAST_UPDATED_IN_MILLIS, System.currentTimeMillis())
                .apply()

            _updateState.postValue(UpdateState.Updated)

        } catch (exception: Exception) {
            val timeLastUpdatedInMillis =
                prefs.getLong(KEY_TIME_LAST_UPDATED_IN_MILLIS, ORIGINAL_TIME_LAST_UPDATED_IN_MILLIS)

            /**
             * Check how long it's been since database was updated. If it's been over two days,
             * we consider the data stale. Otherwise, we ignore the error and consider the database
             * to be updated.
             */
            if (isDataStale(timeLastUpdatedInMillis)) {
                if (userInvokedUpdate) {
                    _updateState.postValue(UpdateState.DataStaleUserInvokedUpdate)
                } else {
                    _updateState.postValue(UpdateState.DataStale)
                }
            } else {
                _updateState.postValue(UpdateState.Updated)
            }
        }
    }


    // Downloads games from the "games" API endpoint and saves them to the "Game" SQLite table.
    private suspend fun downloadAndSaveGameList(
        offset: Int,
        dateLastUpdated: String,
        currentDate: String,
        startingOriginalReleaseDate: String,
        endingOriginalReleaseDate: String
    ): NetworkGameContainer {
        val networkGameContainer = GameNetwork.gameData.getGameListData(
            API_KEY,
            ApiField.Json.field,
            "${ApiField.DateLastUpdated.field}:${SortDirection.Ascending.direction}",
            "${ApiField.DateLastUpdated.field}:${dateLastUpdated}|${currentDate}," +
                    "${ApiField.OriginalReleaseDate.field}:" +
                    "${startingOriginalReleaseDate}|${endingOriginalReleaseDate}",
            "${ApiField.Id.field},${ApiField.Guid.field},${ApiField.Name.field}," +
                    "${ApiField.Image.field},${ApiField.Platforms.field}," +
                    "${ApiField.OriginalReleaseDate.field},${ApiField.ExpectedReleaseDay.field}," +
                    "${ApiField.ExpectedReleaseMonth.field},${ApiField.ExpectedReleaseYear.field}," +
                    ApiField.ExpectedReleaseQuarter.field,
            offset
        ).body()!!

        withContext(Dispatchers.IO) {
            database.gameDao.insertAll(networkGameContainer.games.asDomainModel())
        }

        return networkGameContainer
    }


    // Downloads game detail data from the "game" API endpoint, for display in DetailFragment.
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


    // For Singleton instantiation of GameRepository.
    companion object {
        @Volatile
        private var instance: GameRepository? = null

        fun getInstance(application: Context) =
            instance ?: synchronized(this) {
                instance ?: GameRepository(application).also { instance = it }
            }
    }

}

