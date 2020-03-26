package com.gavinsappcreations.upcominggames.domain

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gavinsappcreations.upcominggames.utilities.PlatformType
import com.gavinsappcreations.upcominggames.utilities.ReleaseDateType
import com.gavinsappcreations.upcominggames.utilities.SortDirection

/**
 * Class that holds all the info for a single game. Holds data from the "games" endpoint of the API.
 *
 * This class also defines the Room "game" table. We couldn't define a separate DatabaseGame
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


data class Platform(val abbreviation: String, val fullName: String)

/**
 * This class extends BaseObservable, which lets us wrap it in a PropertyAwareMutableLiveData.
 * This means that any time one of the properties in SortOptions changes, the LiveData's
 * observers will by notified.
 */
class SortOptions(
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
