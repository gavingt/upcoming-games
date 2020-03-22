package com.gavinsappcreations.upcominggames.utilities

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.net.toUri
import androidx.core.widget.ContentLoadingProgressBar
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gavinsappcreations.upcominggames.App.Companion.applicationContext
import com.gavinsappcreations.upcominggames.R
import com.gavinsappcreations.upcominggames.domain.Game
import com.gavinsappcreations.upcominggames.ui.detail.DetailNetworkState
import com.gavinsappcreations.upcominggames.ui.detail.ScreenshotAdapter
import com.gavinsappcreations.upcominggames.ui.list.GameGridAdapter
import com.gavinsappcreations.upcominggames.ui.search.SearchAdapter
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter("imageUrl")
fun ImageView.bindImage(imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(context)
            .load(if (imgUrl == NO_IMG_URL) R.drawable.ic_broken_image else imgUri)
            .fitCenter()
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
            )
            .into(this)
    }
}


@BindingAdapter("releaseDateInMillis", "dateFormat", "inGameDetailFragment")
fun TextView.formatReleaseDateString(
    releaseDateInMillis: Long?,
    dateFormat: Int,
    inGameDetailFragment: Boolean
) {

    val calendar: Calendar = Calendar.getInstance()
    calendar.timeInMillis = releaseDateInMillis ?: -1

    when (dateFormat) {
        DateFormat.Exact.formatCode -> {
            val desiredPatternFormatter = SimpleDateFormat("MMMM d, yyyy", Locale.US)
            text = desiredPatternFormatter.format(calendar.time)
        }
        DateFormat.Month.formatCode -> {
            val desiredPatternFormatter = SimpleDateFormat("MMMM yyyy", Locale.US)
            text = desiredPatternFormatter.format(calendar.time)
        }
        DateFormat.Quarter.formatCode -> {
            val quarter = (calendar.get(Calendar.MONTH) / 3) + 1
            val desiredPatternFormatter = SimpleDateFormat("yyyy", Locale.US)
            text = context.getString(
                R.string.quarter_release_date,
                quarter,
                desiredPatternFormatter.format(calendar.time)
            )
        }
        DateFormat.Year.formatCode -> {
            val desiredPatternFormatter = SimpleDateFormat("yyyy", Locale.US)
            text = desiredPatternFormatter.format(calendar.time)
        }
        DateFormat.None.formatCode -> {
            text = if (inGameDetailFragment) {
                context.getString(R.string.not_available)
            } else {
                context.getString(R.string.no_release_date)
            }
        }
    }
}


//This BindingAdapter function gets called automatically whenever gameList changes.
@BindingAdapter("gameListData", "databaseState", "updateState")
fun RecyclerView.bindListRecyclerView(
    gameList: PagedList<Game>?,
    databaseState: DatabaseState,
    updateState: UpdateState
) {

    visibility = if (databaseState == DatabaseState.Success &&
        (updateState == UpdateState.Updated || updateState == UpdateState.DataStale)
    ) {
        View.VISIBLE
    } else {
        View.INVISIBLE
    }


    val adapter = adapter as GameGridAdapter

    /**
     * We need to null out the old list or else the old games will briefly appear on screen
     * after the ProgressBar disappears.
     */
    adapter.submitList(null)

    adapter.submitList(gameList) {
        // This Runnable moves the list back to the top when changing sort options
        if (databaseState == DatabaseState.Loading) {
            scrollToPosition(0)
        }
    }
}


@BindingAdapter("gameListProgressBarVisibility")
fun ContentLoadingProgressBar.bindGameListProgressBarVisibility(databaseState: DatabaseState) {
    when (databaseState) {
        DatabaseState.Success -> hide()
        else -> show()
    }

}


@BindingAdapter("dataStaleViewVisibility")
fun View.bindDataStaleViewVisibility(updateState: UpdateState) {
    visibility = when (updateState) {
        UpdateState.DataStale -> View.VISIBLE
        else -> View.GONE
    }
}


@BindingAdapter("dataStaleDateText")
fun TextView.bindDataStaleDateText(updateState: UpdateState) {
    if (updateState == UpdateState.DataStale) {
        val prefs: SharedPreferences =
            applicationContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

        val timeLastUpdatedInMillis =
            prefs.getLong(KEY_TIME_LAST_UPDATED_IN_MILLIS, ORIGINAL_TIME_LAST_UPDATED_IN_MILLIS)

        val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.US)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeLastUpdatedInMillis
        //text = "This data was last updated ${formatter.format(calendar.time)}"
        text = resources.getString(R.string.last_updated, formatter.format(calendar.time))
    }
}


//This BindingAdapter function gets called automatically whenever "data" changes, since BindingAdapters are equivalent to Observers
@BindingAdapter("screenshotData")
fun RecyclerView.bindScreenshotRecyclerView(data: List<String>?) {
    val adapter = adapter as ScreenshotAdapter
    adapter.submitList(data)
}


@BindingAdapter("gameDetailProgressBarVisibility")
fun ContentLoadingProgressBar.bindGameDetailProgressBarVisibility(detailNetworkState: DetailNetworkState) {
    when (detailNetworkState) {
        DetailNetworkState.Loading -> show()
        else -> hide()
    }
}


@BindingAdapter("platformList")
fun TextView.bindPlatformList(platforms: List<String>?) {

    text = if (platforms != null) {
        val builder = StringBuilder()
        for (platformName in platforms) {
            builder.append(platformName).append("\n")
        }
        builder.trim()
    } else {
        context.getString(R.string.not_available)
    }
}


@BindingAdapter("gameDetailList")
fun TextView.bindGameDetailList(items: List<String>?) {
    text = if (items != null) {
        val builder = StringBuilder()
        for (item in items) {
            builder.append(item).append("\n")
        }
        builder.trim()
    } else {
        context.getString(R.string.not_available)
    }
}


@BindingAdapter("customDateVisibility")
fun TextInputLayout.setCustomDateVisibility(releaseDateType: ReleaseDateType) {
    visibility = if (releaseDateType == ReleaseDateType.CustomDate) {
        View.VISIBLE
    } else {
        View.GONE
    }
}


// This runs every time the LiveData value changes, and its job is to change the RadioGroup's checkedId.
@BindingAdapter("releaseDateType")
fun RadioGroup.bindReleaseDateType(type: ReleaseDateType) {

    val isInitializing = checkedRadioButtonId == -1

    val newCheckedId = when (type) {
        ReleaseDateType.RecentAndUpcoming -> R.id.recent_and_upcoming_releases_radioButton
        ReleaseDateType.PastMonth -> R.id.past_month_radioButton
        ReleaseDateType.PastYear -> R.id.past_year_radioButton
        ReleaseDateType.Any -> R.id.any_release_date_radioButton
        ReleaseDateType.CustomDate -> R.id.custom_date_range_radioButton
    }

    // Prevent infinite loops
    if (checkedRadioButtonId != newCheckedId) {
        check(newCheckedId)
    }

    // This prevents the animation from playing when SortFragment first opens
    if (isInitializing) {
        jumpDrawablesToCurrentState()
    }
}


// This runs every time a new RadioButton is selected, and its job is to change the LiveData's value.
@InverseBindingAdapter(attribute = "releaseDateType")
fun RadioGroup.setReleaseDateType(): ReleaseDateType {

    return when (checkedRadioButtonId) {
        R.id.recent_and_upcoming_releases_radioButton -> ReleaseDateType.RecentAndUpcoming
        R.id.past_month_radioButton -> ReleaseDateType.PastMonth
        R.id.past_year_radioButton -> ReleaseDateType.PastYear
        R.id.any_release_date_radioButton -> ReleaseDateType.Any
        else -> ReleaseDateType.CustomDate
    }
}

// This notifies the data binding system that the attribute value has changed.
@BindingAdapter("releaseDateTypeAttrChanged")
fun RadioGroup.setReleaseDateTypeListeners(listener: InverseBindingListener) {

    setOnCheckedChangeListener { _, _ ->
        listener.onChange()
    }
}


// This runs every time the LiveData value changes, and its job is to change the RadioGroup's checkedId.
@BindingAdapter("sortDirection")
fun RadioGroup.bindSortDirection(sortDirection: SortDirection) {

    val isInitializing = checkedRadioButtonId == -1

    val newCheckedId = when (sortDirection) {
        SortDirection.Ascending -> R.id.sort_ascending_radioButton
        SortDirection.Descending -> R.id.sort_descending_radioButton
    }

    // Prevent infinite loops
    if (checkedRadioButtonId != newCheckedId) {
        check(newCheckedId)
    }

    // This prevents the animation from playing when SortFragment first opens
    if (isInitializing) {
        jumpDrawablesToCurrentState()
    }
}


// This runs every time a new RadioButton is selected, and its job is to change the LiveData's value.
@InverseBindingAdapter(attribute = "sortDirection")
fun RadioGroup.setSortDirection(): SortDirection {

    return when (checkedRadioButtonId) {
        R.id.sort_ascending_radioButton -> SortDirection.Ascending
        else -> SortDirection.Descending
    }
}

// This notifies the data binding system that the attribute value has changed.
@BindingAdapter("sortDirectionAttrChanged")
fun RadioGroup.setSortDirectionListeners(listener: InverseBindingListener) {

    setOnCheckedChangeListener { _, _ ->
        listener.onChange()
    }
}


// This runs every time the LiveData value changes, and its job is to change the RadioGroup's checkedId.
@BindingAdapter("platformType")
fun RadioGroup.bindPlatformType(platformType: PlatformType) {

    val isInitializing = checkedRadioButtonId == -1

    val newCheckedId = when (platformType) {
        PlatformType.CurrentGeneration -> R.id.current_generation_radioButton
        PlatformType.All -> R.id.all_platforms_radioButton
        PlatformType.PickFromList -> R.id.custom_platforms_radioButton
    }

    // Prevent infinite loops
    if (checkedRadioButtonId != newCheckedId) {
        check(newCheckedId)
    }

    // This prevents the animation from playing when SortFragment first opens
    if (isInitializing) {
        jumpDrawablesToCurrentState()
    }
}


// This runs every time a new RadioButton is selected, and its job is to change the LiveData's value.
@InverseBindingAdapter(attribute = "platformType")
fun RadioGroup.setPlatformType(): PlatformType {

    return when (checkedRadioButtonId) {
        R.id.current_generation_radioButton -> PlatformType.CurrentGeneration
        R.id.all_platforms_radioButton -> PlatformType.All
        else -> PlatformType.PickFromList
    }
}

// This notifies the data binding system that the attribute value has changed.
@BindingAdapter("platformTypeAttrChanged")
fun RadioGroup.setPlatformTypeListeners(listener: InverseBindingListener) {

    setOnCheckedChangeListener { _, _ ->
        listener.onChange()
    }
}


//This BindingAdapter function gets called automatically whenever searchResults changes.
@BindingAdapter("searchResults")
fun RecyclerView.bindSearchRecyclerView(gameList: PagedList<Game>?) {
    val adapter = adapter as SearchAdapter

    adapter.submitList(gameList) {
        // This Runnable moves the list back to the top when changing sort options
/*        if (databaseState == DatabaseState.Loading) {
            scrollToPosition(0)
        }*/
    }
}