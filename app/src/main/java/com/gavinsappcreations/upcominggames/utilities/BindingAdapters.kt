package com.gavinsappcreations.upcominggames.utilities

import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
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


//This BindingAdapter function gets called automatically whenever "data" changes, since BindingAdapters are equivalent to Observers
@BindingAdapter("listData")
fun RecyclerView.bindRecyclerView(data: List<Game>?) {
    val adapter = adapter as GameGridAdapter
    // This disables the fade-in animation that would otherwise occur when the database gets updated
    //itemAnimator = null
    adapter.submitList(data)
}

@BindingAdapter("releaseDate")
fun TextView.formatReleaseDateString(dateInMillis: Long) {
    val calendar: Calendar = Calendar.getInstance()
    calendar.timeInMillis = dateInMillis
    val desiredPatternFormatter = SimpleDateFormat("MMMM d, yyyy", Locale.US)
    text = desiredPatternFormatter.format(calendar.time)
}