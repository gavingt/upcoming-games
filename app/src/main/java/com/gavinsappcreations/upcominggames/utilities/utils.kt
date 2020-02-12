package com.gavinsappcreations.upcominggames.utilities

import com.gavinsappcreations.upcominggames.App
import com.gavinsappcreations.upcominggames.R
import com.gavinsappcreations.upcominggames.network.NetworkGame
import java.text.SimpleDateFormat
import java.util.*


/**
 * Takes the date returned by the API and turns it into a time in millis
 */
fun NetworkGame.fetchReleaseDateInMillis(): Long? {
    val calendar: Calendar = Calendar.getInstance()
    /**
     * When we call Calendar.getInstance(), this sets values for hours, minutes, seconds, and
     * milliseconds that we don't want. We want to create a Calendar object that contains only
     * the day, month, and year. So setting timeInMillis = 0 makes it so the current time doesn't
     * affect the timeInMillis value we're ultimately returning.
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