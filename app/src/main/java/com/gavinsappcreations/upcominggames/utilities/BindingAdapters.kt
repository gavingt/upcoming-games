package com.gavinsappcreations.upcominggames.utilities

import android.animation.ObjectAnimator
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.ProgressBar
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
import com.gavinsappcreations.upcominggames.domain.*
import com.gavinsappcreations.upcominggames.ui.detail.ScreenshotAdapter
import com.gavinsappcreations.upcominggames.ui.list.GameGridAdapter
import com.gavinsappcreations.upcominggames.ui.search.SearchAdapter
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

// Use Glide to load images throughout the app.
@BindingAdapter("imageUrl")
fun ImageView.bindImage(imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(context)
            .load(if (imgUrl.contains(NO_IMG_FILE_NAME)) R.drawable.broken_image else imgUri)
            .fitCenter()
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.broken_image)
            )
            .into(this)
    }
}

/**
 * Converts date timestamps into human-readable date strings.
 * @param releaseDateInMillis Unix timestamp representing release date.
 * @param dateFormat represents the level of precision of the release date (day, month, year, etc...).
 * @param inGameDetailFragment if we're displaying date in DetailFragment, we show different text
 *                             when the release date is unknown.
 */
@BindingAdapter("releaseDateInMillis", "dateFormat", "inGameDetailFragment")
fun TextView.formatReleaseDateString(
    releaseDateInMillis: Long?,
    dateFormat: Int,
    inGameDetailFragment: Boolean
) {
    val calendar: Calendar = Calendar.getInstance()
    calendar.timeInMillis = releaseDateInMillis ?: -1

    // Depending on dateFormat, we format release date string differently.
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


//This BindingAdapter function gets called automatically whenever gameList in ListFragment changes.
@BindingAdapter("gameListData", "databaseState", "updateState")
fun RecyclerView.bindGameListRecyclerView(
    gameList: PagedList<Game>?,
    databaseState: DatabaseState,
    updateState: UpdateState
) {
    val adapter = adapter as GameGridAdapter

    // True if we should show the games, false if database is loading or updating.
    val isGameListReady =
        databaseState == DatabaseState.Success && updateState !is UpdateState.Updating

    if (isGameListReady) {
        adapter.submitList(gameList) {
            // Once the submitted list is committed, this callback executes.
            visibility = View.VISIBLE
        }
    } else {
        visibility = View.INVISIBLE
        adapter.submitList(null)
    }
}


//This BindingAdapter function gets called automatically whenever favoriteList in FavoriteFragment changes.
@BindingAdapter("favoriteListData")
fun RecyclerView.bindFavoriteListRecyclerView(
    favoriteList: PagedList<Game>?
) {
    val adapter = adapter as GameGridAdapter
    adapter.submitList(favoriteList)
}


/**
 * We use this BindingAdapter in both ListFragment and FavoriteFragment to set the visibility of
 * the empty View. We use the "requireAll" flag because FavoriteFragment doesn't contain a
 * DatabaseState property while ListFragment does. So in the FavoriteFragment, databaseState will
 * equal null.
 */
@BindingAdapter(value = ["gameList", "databaseState"], requireAll = false)
fun TextView.bindEmptyViewVisibility(gameList: PagedList<Game>?, databaseState: DatabaseState?) {
    gameList?.let {
        visibility =
            if (gameList.isEmpty() && (databaseState == null || databaseState == DatabaseState.Success)) {
                View.VISIBLE
            } else {
                View.GONE
            }
    }
}


/**
 * Sets visibility of indeterminate ProgressBar in ListFragment. This ProgressBar indicates that
 * data is loading from the database.
 */
@BindingAdapter("databaseState", "updateState")
fun ContentLoadingProgressBar.bindIndeterminateProgressBarVisibility(
    databaseState: DatabaseState,
    updateState: UpdateState
) {
    // Show indeterminate ProgressBar if database isn't finished loading and we're not updating.
    if (updateState is UpdateState.Updating) {
        hide()
    } else {
        when (databaseState) {
            DatabaseState.Success -> hide()
            else -> show()
        }
    }
}


/**
 * Sets visibility of determinate ProgressBar. This ProgressBar indicates that we're updating
 * the database from the API.
 */
@BindingAdapter("determinateProgressBarVisibility")
fun ProgressBar.bindDeterminateProgressBarVisibility(updateState: UpdateState) {
    when (updateState) {
        is UpdateState.Updating -> {
            progress = updateState.currentProgress
            val animation = ObjectAnimator.ofInt(
                this,
                "progress",
                updateState.oldProgress,
                updateState.currentProgress
            )
            animation.duration = LOADING_PROGRESS_ANIMATION_TIME
            animation.interpolator = DecelerateInterpolator()
            animation.start()
            visibility = View.VISIBLE
        }
        else -> visibility = View.GONE
    }
}


/**
 * This controls the visibility of Views in ListFragment that show when the data in the database
 * is stale and needs to be updated.
 */
@BindingAdapter("dataStaleViewVisibility")
fun View.bindDataStaleViewVisibility(updateState: UpdateState) {
    visibility = when (updateState) {
        UpdateState.DataStale, UpdateState.DataStaleUserInvokedUpdate -> View.VISIBLE
        else -> View.GONE
    }
}


/**
 * This formats the dateStaleTextView text in ListFragment.
 */
@BindingAdapter("dataStaleDateText")
fun TextView.bindDataStaleDateText(updateState: UpdateState) {
    if (updateState == UpdateState.DataStale || updateState == UpdateState.DataStaleUserInvokedUpdate) {
        val prefs: SharedPreferences =
            applicationContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

        val timeLastUpdatedInMillis =
            prefs.getLong(KEY_TIME_LAST_UPDATED_IN_MILLIS, ORIGINAL_TIME_LAST_UPDATED_IN_MILLIS)

        val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.US)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeLastUpdatedInMillis

        text = if (updateState == UpdateState.DataStale) {
            resources.getString(R.string.last_updated_data_stale, formatter.format(calendar.time))
        } else {
            /**
             * This branch is only relevant if updateState == UpdateState.DataStaleUserInvokedUpdate,
             * since otherwise this TextView isn't visible.
             */
            resources.getString(
                R.string.last_updated_data_stale_user_invoked_update,
                formatter.format(calendar.time)
            )
        }
    }
}


// Binds screenshot data to views in DetailFragment.
@BindingAdapter("screenshotData")
fun RecyclerView.bindScreenshotRecyclerView(data: List<String>?) {
    val adapter = adapter as ScreenshotAdapter
    adapter.submitList(data)
}


// Controls visibility of indeterminate ProgressBar in DetailFragment.
@BindingAdapter("gameDetailProgressBarVisibility")
fun ContentLoadingProgressBar.bindGameDetailProgressBarVisibility(detailNetworkState: DetailNetworkState) {
    when (detailNetworkState) {
        DetailNetworkState.Loading -> show()
        else -> hide()
    }
}


// Formats text showing the list of platforms in DetailFragment.
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


// TODO: continue commenting here -
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
    val newCheckedId = when (sortDirection) {
        SortDirection.Ascending -> R.id.sort_ascending_radioButton
        SortDirection.Descending -> R.id.sort_descending_radioButton
    }

    // Prevent infinite loops
    if (checkedRadioButtonId != newCheckedId) {
        check(newCheckedId)
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
    val newCheckedId = when (platformType) {
        PlatformType.CurrentGeneration -> R.id.current_generation_radioButton
        PlatformType.All -> R.id.all_platforms_radioButton
        PlatformType.PickFromList -> R.id.custom_platforms_radioButton
    }

    // Prevent infinite loops
    if (checkedRadioButtonId != newCheckedId) {
        check(newCheckedId)
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
fun RecyclerView.bindSearchRecyclerView(gameList: List<SearchResult>?) {
    val adapter = adapter as SearchAdapter
    adapter.submitList(gameList) {
        scrollToPosition(0)
    }
}