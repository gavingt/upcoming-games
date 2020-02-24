package com.gavinsappcreations.upcominggames.ui.imageViewer

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.net.toUri
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gavinsappcreations.upcominggames.R
import com.gavinsappcreations.upcominggames.utilities.NO_IMG_URL
import com.ortiz.touchview.TouchImageView

class TouchImageAdapter(val images: List<String>) : PagerAdapter() {

    override fun getCount(): Int {
        return images.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): View {
        return TouchImageView(container.context).apply {
            setImageResource(R.drawable.loading_animation)
            container.addView(
                this,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )

            val imageToLoad = images[position]
            imageToLoad.let {
                val imgUri = imageToLoad.toUri().buildUpon().scheme("https").build()
                Glide.with(context)
                    .load(if (imageToLoad == NO_IMG_URL) R.drawable.ic_broken_image else imgUri)
                    .apply(
                        RequestOptions()
                            .placeholder(R.drawable.loading_animation)
                            .error(R.drawable.ic_broken_image)
                    )
                    .into(this)
            }
        }

    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

}