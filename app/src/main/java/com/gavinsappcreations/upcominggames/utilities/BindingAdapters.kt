package com.gavinsappcreations.upcominggames.utilities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gavinsappcreations.upcominggames.R
import com.gavinsappcreations.upcominggames.domain.Game
import com.gavinsappcreations.upcominggames.ui.detail.ScreenshotAdapter
import com.gavinsappcreations.upcominggames.ui.list.GameGridAdapter
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("imageUrl")
fun ImageView.bindCoverImage(imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(context)
            .load(if (imgUrl == NO_IMG_URL) R.drawable.ic_broken_image else imgUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
            )
            .into(this)
    }
}


@BindingAdapter("screenshotUrl")
fun ImageView.bindScreenshotImage(imgUrl: String?) {
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
}


//This BindingAdapter function gets called automatically whenever "data" changes, since BindingAdapters are equivalent to Observers
@BindingAdapter("screenshotData")
fun RecyclerView.bindScreenshotRecyclerView(data: List<String>?) {
    val adapter = adapter as ScreenshotAdapter
    adapter.submitList(data)
}

//TODO: sort platforms by order they're listed in allPlatforms

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

