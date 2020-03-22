package com.gavinsappcreations.upcominggames.utilities

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.MutableLiveData
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


fun dpToPx(dp: Int): Int {
    return (dp * Resources.getSystem().displayMetrics.density).toInt()
}

//Converts a px measurement to a dp measurement.
fun pxToDp(px: Int, context: Context): Int {
    val displayMetrics = context.resources.displayMetrics
    return (px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}


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



// We can call this to notify observers after we've changed, for example, a single item in a MutableLiveData<List<T>>
fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}