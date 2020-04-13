package com.gavinsappcreations.upcominggames.domain

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Class that holds all the info for a single game. Holds data from the "games" endpoint of the API.
 *
 * This class also defines the Room "Game" table. We couldn't define a separate DatabaseGame
 * class, because Android's paging library requires that the UI consume the same class that is
 * being retrieved from the data source (in this case the Room database).
 */

@Entity
data class Game(
    @PrimaryKey
    val gameId: Long,
    val gameName: String,
    val mainImageUrl: String,
    val platforms: List<String>?,
    val releaseDateInMillis: Long?,
    val dateFormat: Int,
    val guid: String,
    val isFavorite: Boolean?
)

/**
 * Holds data for the current game in DetailFragment.
 */
data class GameDetail(
    val gameId: Long,
    val guid: String,
    val gameName: String,
    val mainImageUrl: String,
    val images: List<String>?,
    val platforms: List<String>?,
    val releaseDateInMillis: Long?,
    val dateFormat: Int,
    val developers: List<String>?,
    val publishers: List<String>?,
    val genres: List<String>?,
    val gameRating: List<String>?,
    val deck: String?,
    val detailUrl: String?
)


/**
 * Holds a single search result in SearchFragment, including the game and whether or not the user
 * recently searched for the game.
 */
data class SearchResult(
    val game: Game,
    var isRecentSearch: Boolean
)


/**
 * Represents a single game platform, with its full name and abbreviation.
 */
data class Platform(val abbreviation: String, val fullName: String)


/**
 * Represents the user's current filtering choices, as chosen in FilterFragment.
 *
 * This class extends BaseObservable, which lets us wrap it in a PropertyAwareMutableLiveData.
 * This means that any time one of the properties in FilterOptions changes, the LiveData's
 * observers will by notified.
 */
class FilterOptions(
    releaseDateType: ReleaseDateType,
    sortDirection: SortDirection,
    customDateStart: String,
    customDateEnd: String,
    platformType: PlatformType,
    platformIndices: MutableSet<Int>
) : BaseObservable() {

    @Bindable
    var releaseDateType: ReleaseDateType = releaseDateType
        set(value) {
            field = value
            notifyChange()
        }

    @Bindable
    var sortDirection: SortDirection = sortDirection
        set(value) {
            field = value
            notifyChange()
        }

    @Bindable
    var customDateStart: String = customDateStart
        set(value) {
            field = value
            notifyChange()
        }

    @Bindable
    var customDateEnd: String = customDateEnd
        set(value) {
            field = value
            notifyChange()
        }

    @Bindable
    var platformType: PlatformType = platformType
        set(value) {
            field = value
            notifyChange()
        }

    @Bindable
    var platformIndices: MutableSet<Int> = platformIndices
        set(value) {
            field = value
            notifyChange()
        }
}


// Holds the current state of network updates of the local database.
sealed class UpdateState {
    // Database has been updated within last two days.
    object Updated : UpdateState()

    // Database is currently being updated.
    class Updating(val oldProgress: Int, val currentProgress: Int) : UpdateState()

    /**
     * Database hasn't been updated in over two days.
     * [timeLastUpdatedInMillis] Time in millis indicating when database was last updated.
     * [userInvokedUpdate] True if user invoked the update manually, but it failed due to a
     * network error. Thus it allows us to show an error message to the user.
     */
    class DataStale(val timeLastUpdatedInMillis: Long, val userInvokedUpdate: Boolean) : UpdateState()
}

// Holds the current state of loading data from the SQLite database.
enum class DatabaseState {
    /**
     * When the user presses UPDATE button from FilterFragment, we return to ListFragment and the
     * Observer observing gameList emits a value containing the old list data. When this emission
     * occurs, databaseState will change from LoadingFilterChange to Loading.*/
    LoadingFilterChange,

    /**
     * Loading represents the actual loading of the new gameList. When the Observer observing
     * gameList emits while in this state, databaseState will change from Loading to Success and
     * the items in gameList will be displayed to the user.*/
    Loading,

    // In Success state, gameList is displayed and ProgressBar is hidden (assuming network updates
    // aren't also in progress).
    Success
}


// Holds the current state of loading game details from the "game" API endpoint, for display in DetailFragment.
enum class DetailNetworkState {
    Loading,
    Failure,
    Success
}


// Holds the currently selected release date type, as selected by the user in FilterFragment.
enum class ReleaseDateType {
    RecentAndUpcoming,
    PastMonth,
    PastYear,
    Any,
    CustomDate
}

// Holds the currently selected sort direction, as selected by the user in FilterFragment.
enum class SortDirection(val direction: String) {
    Ascending("asc"),
    Descending("desc")
}

/**
 * Holds the currently selected platform type, as selected by the user in FilterFragment. Each of these
 * values represents a different set of platforms that will be visible to the user in ListFragment.
 */
enum class PlatformType {
    CurrentGeneration,
    All,
    PickFromList
}
