package com.gavinsappcreations.upcominggames.utilities

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.MutableLiveData
import com.gavinsappcreations.upcominggames.domain.FilterOptions
import com.gavinsappcreations.upcominggames.domain.PlatformType
import com.gavinsappcreations.upcominggames.domain.ReleaseDateType
import java.text.SimpleDateFormat
import java.util.*

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


fun hideKeyboard(view: View) {
    val inputMethodManager =
        view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun showSoftKeyboard(view: View) {
    if (view.requestFocus()) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
}


/**
 *  Returns true if database hasn't been updated in over two days.
 *  @param: [timeLastUpdatedInMillis] The time in millis when the database was last updated.
 */
fun isDataStale(timeLastUpdatedInMillis: Long): Boolean {
    val calendar: Calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 2)
    val twoDaysAgoInMillis = calendar.timeInMillis
    return twoDaysAgoInMillis > timeLastUpdatedInMillis
}


/**
 * For the platforms selected by the user in FilterFragment, this fetches their corresponding
 * indices in the @link [allKnownPlatforms] list.
 */
fun fetchPlatformIndices(filterOptions: FilterOptions): Set<Int> {
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
fun fetchDateConstraints(filterOptions: FilterOptions): Array<Long?> {
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


// We can call this to notify observers after we've changed, for example, a single item in a MutableLiveData<List<T>>
fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}