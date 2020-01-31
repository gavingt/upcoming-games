package com.gavinsappcreations.upcominggames.utilities

import com.gavinsappcreations.upcominggames.App
import com.gavinsappcreations.upcominggames.R
import com.gavinsappcreations.upcominggames.domain.Game

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

