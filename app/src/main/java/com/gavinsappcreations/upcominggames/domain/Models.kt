package com.gavinsappcreations.upcominggames.domain

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Class that holds all the info for a single game.
 *
 * This class also defines the Room "game" table. We couldn't define a separate DatabaseGame
 * class, because Android's paging library requires that the UI consume the same class that is
 * being retrieved from the data source (in this case the Room database).
 */

@Entity
@Parcelize
data class Game(
    @PrimaryKey
    val gameId: Long,
    val deck: String?,
    val description: String?,
    val gameName: String,
    val originalGameRating: String?,
    val imageUrl: String,
    val platforms: List<String>?,
    val releaseDateInMillis: Long?
) : Parcelable