package com.gavinsappcreations.upcominggames.utilities

import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gavinsappcreations.upcominggames.R
import com.gavinsappcreations.upcominggames.domain.Game
import com.gavinsappcreations.upcominggames.ui.detail.ScreenshotAdapter
import com.gavinsappcreations.upcominggames.ui.list.GameGridAdapter
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


//This BindingAdapter function gets called automatically whenever "data" changes, since BindingAdapters are equivalent to Observers
@BindingAdapter("listData")
fun RecyclerView.bindListRecyclerView(data: PagedList<Game>?) {
    val adapter = adapter as GameGridAdapter
    adapter.submitList(data)

    // TODO: itemAnimator is making list not scroll to top when sort options are changed. Find out why it does this.
    this.itemAnimator = null
}


//This BindingAdapter function gets called automatically whenever "data" changes, since BindingAdapters are equivalent to Observers
@BindingAdapter("screenshotData")
fun RecyclerView.bindScreenshotRecyclerView(data: List<String>?) {
    val adapter = adapter as ScreenshotAdapter
    adapter.submitList(data)
}


@BindingAdapter("platformList")
fun TextView.formatPlatformList(platforms: List<String>?) {

    text = if (platforms != null) {
        val builder = StringBuilder()
        for (abbreviatedPlatform in platforms) {
            val platformIndex = allPlatforms.indexOfFirst {
                it.abbreviation == abbreviatedPlatform
            }
            builder.append(allPlatforms[platformIndex].fullName).append("\n")
        }
        builder.trim()
    } else {
        context.getString(R.string.not_available)
    }
}


@BindingAdapter("gameDetailList")
fun TextView.formatGameDetailList(items: List<String>?) {
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



// This runs every time the LiveData value changes, and its job is to change the RadioGroup's checkedId.
@BindingAdapter("releaseDateType")
fun RadioGroup.setReleaseDateType(type: ReleaseDateType) {

    val isInitializing = checkedRadioButtonId == -1

    val newCheckedId = when (type) {
        ReleaseDateType.RecentAndUpcoming -> R.id.recent_and_upcoming_releases_radioButton
        ReleaseDateType.PastMonth -> R.id.past_month_radioButton
        ReleaseDateType.PastYear -> R.id.past_year_radioButton
        ReleaseDateType.CustomRange -> R.id.custom_date_range_radioButton
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
fun RadioGroup.getReleaseDateType(): ReleaseDateType {

    return when (checkedRadioButtonId) {
        R.id.recent_and_upcoming_releases_radioButton -> ReleaseDateType.RecentAndUpcoming
        R.id.past_month_radioButton -> ReleaseDateType.PastMonth
        R.id.past_year_radioButton -> ReleaseDateType.PastYear
        else -> ReleaseDateType.CustomRange
    }
}

// This notifies the data binding system that the attribute value has changed.
@BindingAdapter("app:releaseDateTypeAttrChanged")
fun RadioGroup.setReleaseDateTypeListeners(listener: InverseBindingListener) {

    setOnCheckedChangeListener{ _, _ ->
        listener.onChange()
    }
}



// This runs every time the LiveData value changes, and its job is to change the RadioGroup's checkedId.
@BindingAdapter("sortDirection")
fun RadioGroup.setSortDirection(sortDirection: SortDirection) {

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
fun RadioGroup.getSortDirection(): SortDirection {

    return when (checkedRadioButtonId) {
        R.id.sort_ascending_radioButton -> SortDirection.Ascending
        else -> SortDirection.Descending
    }
}

// This notifies the data binding system that the attribute value has changed.
@BindingAdapter("app:sortDirectionAttrChanged")
fun RadioGroup.setSortDirectionListeners(listener: InverseBindingListener) {

    setOnCheckedChangeListener{ _, _ ->
        listener.onChange()
    }
}