package com.gavinsappcreations.upcominggames.utilities

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
import com.gavinsappcreations.upcominggames.ui.list.GameGridAdapter
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter("imageUrl")
fun ImageView.bindImage(imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(context)
            .load(imgUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
            )
            .into(this)
    }
}


@BindingAdapter("releaseDateInMillis", "dateFormat", "includePrefix")
fun TextView.formatReleaseDateString(
    releaseDateInMillis: Long?,
    dateFormat: Int,
    includePrefix: Boolean
) {

    var releaseDateString: String? = null

    if (releaseDateInMillis == null) {
        text = context.getString(R.string.no_release_date)
    } else {

        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = releaseDateInMillis

        when (dateFormat) {
            DateFormat.Exact.formatCode -> {
                val desiredPatternFormatter = SimpleDateFormat("MMMM d, yyyy", Locale.US)
                releaseDateString = desiredPatternFormatter.format(calendar.time)
            }
            DateFormat.Month.formatCode -> {
                val desiredPatternFormatter = SimpleDateFormat("MMMM yyyy", Locale.US)
                releaseDateString = desiredPatternFormatter.format(calendar.time)
            }
            DateFormat.Quarter.formatCode -> {
                val quarter = (calendar.get(Calendar.MONTH) / 3) + 1
                val desiredPatternFormatter = SimpleDateFormat("yyyy", Locale.US)
                releaseDateString = context.getString(
                    R.string.quarter_release_date,
                    quarter,
                    desiredPatternFormatter.format(calendar.time)
                )
            }
            DateFormat.Year.formatCode -> {
                val desiredPatternFormatter = SimpleDateFormat("yyyy", Locale.US)
                releaseDateString = desiredPatternFormatter.format(calendar.time)
            }
        }

        text = if (includePrefix) {
            context.getString(R.string.release_date, releaseDateString)
        } else {
            releaseDateString
        }

    }
}


//This BindingAdapter function gets called automatically whenever "data" changes, since BindingAdapters are equivalent to Observers
@BindingAdapter("listData")
fun RecyclerView.bindRecyclerView(data: PagedList<Game>?) {
    val adapter = adapter as GameGridAdapter
    // Don't animate items in grid because they don't play well with paging in games from API.
    adapter.submitList(data)
}




