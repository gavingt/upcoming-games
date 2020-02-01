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
fun List<Game>.removeBadGameData(): List<Game> {
    val newList = mutableListOf<Game>()
    for (game in this) {
        if (game.releaseDate != App.applicationContext.getString(R.string.unknown_date)) {
            newList.add(game)
        }
    }
    return newList
}


/**
 * Takes the date returned by the API and returns it as a String formatted the way we want it.
 */
fun NetworkGame.formatReleaseDate(): String {
    val calendar: Calendar = Calendar.getInstance()
    val desiredPatternFormatter = SimpleDateFormat("MMMM d, yyyy", Locale.US)

    if (originalReleaseDate != null) {
        val apiPatternFormatter = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US)
        val date = apiPatternFormatter.parse(originalReleaseDate)
        return desiredPatternFormatter.format(date!!)
    }

    val releaseDay =
        expectedReleaseDay ?: return App.applicationContext.getString(R.string.unknown_date)
    val releaseMonth = expectedReleaseMonth?.minus(1)
        ?: return App.applicationContext.getString(R.string.unknown_date)
    val releaseYear =
        expectedReleaseYear ?: return App.applicationContext.getString(R.string.unknown_date)

    calendar.set(releaseYear, releaseMonth, releaseDay)
    return desiredPatternFormatter.format(calendar.time)
}