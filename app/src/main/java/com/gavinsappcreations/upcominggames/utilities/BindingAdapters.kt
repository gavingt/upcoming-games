package com.gavinsappcreations.upcominggames.utilities

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gavinsappcreations.upcominggames.domain.GameRelease
import com.gavinsappcreations.upcominggames.ui.list.GameGridAdapter

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
/*            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image))*/
            .into(imgView)
    }
}

//This BindingAdapter function gets called automatically whenever "data" changes, since BindingAdapters are equivalent to Observers
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<GameRelease>?) {
    val adapter = recyclerView.adapter as GameGridAdapter
    adapter.submitList(data)
}