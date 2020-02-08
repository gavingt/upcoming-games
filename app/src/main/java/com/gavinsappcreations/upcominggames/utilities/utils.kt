package com.gavinsappcreations.upcominggames.utilities

import com.gavinsappcreations.upcominggames.App
import com.gavinsappcreations.upcominggames.R
import com.gavinsappcreations.upcominggames.domain.Game
import com.gavinsappcreations.upcominggames.network.NetworkGame
import java.text.SimpleDateFormat
import java.util.*

/**
 * If a Game doesn't contain an exact release date, just remove the Game from our List<Game> object.
 */
fun List<Game>.removeGamesWithoutReleaseDates(): List<Game> {
    val newList = mutableListOf<Game>()
    for (game in this) {
        if (game.releaseDateInMillis != null) {
            newList.add(game)
        }
    }
    return newList
}



/**
 * Takes the date returned by the API and turns it into a time in millis
 */
fun NetworkGame.fetchReleaseDateInMillis(): Long? {
    val calendar: Calendar = Calendar.getInstance()
    /**
     * Set calendar.timeInMillis to 0 because we're translating a day, month, and year into a time
     * in millis, and we always want to retrieve the same timeInMillis when we do this.
     */
    calendar.timeInMillis = 0

    if (originalReleaseDate != null) {
        val apiPatternFormatter = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US)
        val date = apiPatternFormatter.parse(originalReleaseDate)
        calendar.time = date!!
        return calendar.timeInMillis
    }

    val releaseDay = expectedReleaseDay ?: return null
    val releaseMonth = expectedReleaseMonth?.minus(1) ?: return null
    val releaseYear = expectedReleaseYear ?: return null
    calendar.set(releaseYear, releaseMonth, releaseDay)
    return calendar.timeInMillis
}