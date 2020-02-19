package com.gavinsappcreations.upcominggames.utilities

import java.text.SimpleDateFormat
import java.util.*

//TODO: rescrape database, saving date format: exact, month, quarter, year

/**
 * Takes the date returned by the API and turns it into a time in millis
 */
fun fetchReleaseDateInMillis(
    originalReleaseDate: String?, expectedReleaseYear: Int?, expectedReleaseQuarter: Int?,
    expectedReleaseMonth: Int?, expectedReleaseDay: Int?
): List<Any?> {
    val calendar: Calendar = Calendar.getInstance()
    /**
     * When we call Calendar.getInstance(), this sets values for hours, minutes, seconds, and
     * milliseconds that we don't want. We want to create a Calendar object that contains only
     * the day, month, and year. So setting timeInMillis = 0 makes it so the current time doesn't
     * affect the timeInMillis value we're ultimately returning.
     */
    calendar.timeInMillis = 0

    if (originalReleaseDate != null) {
        val apiPatternFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = apiPatternFormatter.parse(originalReleaseDate)
        calendar.time = date!!
        return listOf(calendar.timeInMillis, DateFormat.Exact)
    }

    var releaseMonth = expectedReleaseMonth?.minus(1)
    var releaseDay = expectedReleaseDay

    when {
        releaseDay != null -> {
            calendar.set(expectedReleaseYear!!, releaseMonth!!, releaseDay)
            return listOf(calendar.timeInMillis, DateFormat.Exact)
        }
        releaseMonth != null -> {
            // Temporarily set day to 1
            calendar.set(expectedReleaseYear!!, releaseMonth, 1)
            // Find maximum day in month
            releaseDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            // Re-set calendar with maximum day
            calendar.set(expectedReleaseYear, releaseMonth, releaseDay)
            return listOf(calendar.timeInMillis, DateFormat.Month)
        }
        expectedReleaseQuarter != null -> {
            releaseMonth = (expectedReleaseQuarter * 3) - 1
            calendar.set(expectedReleaseYear!!, releaseMonth, 1)
            releaseDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            calendar.set(expectedReleaseYear, releaseMonth, releaseDay)
            return listOf(calendar.timeInMillis, DateFormat.Quarter)
        }
        expectedReleaseYear != null -> {
            calendar.set(expectedReleaseYear, 11, 31)
            return listOf(calendar.timeInMillis, DateFormat.Year)
        }
        else -> return listOf(null, DateFormat.None)
    }

}