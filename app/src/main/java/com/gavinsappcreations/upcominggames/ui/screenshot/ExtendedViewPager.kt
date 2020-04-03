package com.gavinsappcreations.upcominggames.ui.screenshot

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.ortiz.touchview.TouchImageView

/**
 * A subclass of ViewPager that plays nicely with TouchImageView. Paging between Views won't occur
 * unless an image has been fully scrolled to its edge.
 */
class ExtendedViewPager : ViewPager {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun canScroll(view: View, checkV: Boolean, dx: Int, x: Int, y: Int): Boolean {
        return if (view is TouchImageView) {
            view.canScrollHorizontally(-dx)
        } else {
            super.canScroll(view, checkV, dx, x, y)
        }
    }

}
