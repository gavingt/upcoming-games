package com.gavinsappcreations.upcominggames.ui.detail

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.ScrollView
import androidx.annotation.Nullable

/**
 * This class is a workaround for when a parent ScrollView with vertical scroll
 * is a bit too sensitive and steals onTouchEvents from horizontally scrolling child views.
 */
class NestedScrollingParentScrollView : ScrollView {
    private var mChildIsScrolling = false
    private var mTouchSlop = 0
    private var mOriginalX = 0f
    private var mOriginalY = 0f

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, @Nullable attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, @Nullable attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(context)
    }

    private fun init(context: Context) {
        val vc = ViewConfiguration.get(context)
        mTouchSlop = vc.scaledTouchSlop
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.action
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) { // Release the scroll
            mChildIsScrolling = false
            return false // Let child handle touch event
        }
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                run {
                    mChildIsScrolling = false
                    setOriginalMotionEvent(ev)
                }
                run {
                    if (mChildIsScrolling) { // Child is scrolling so let child handle touch event
                        return false
                    }
                    // If the user has dragged his finger horizontally more than
                    // the touch slop, then child view is scrolling
                    val xDiff = calculateDistanceX(ev)
                    val yDiff = calculateDistanceY(ev)
                    // Touch slop should be calculated using ViewConfiguration constants.
                    if (xDiff > mTouchSlop && xDiff > yDiff) {
                        mChildIsScrolling = true
                        return false
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (mChildIsScrolling) {
                    return false
                }
                val xDiff = calculateDistanceX(ev)
                val yDiff = calculateDistanceY(ev)
                if (xDiff > mTouchSlop && xDiff > yDiff) {
                    mChildIsScrolling = true
                    return false
                }
            }
        }
         /**
          * In general, we don't want to intercept touch events. They should be
          * handled by the child view.  Be safe and leave it up to the original definition.
          */
        return super.onInterceptTouchEvent(ev)
    }

    private fun setOriginalMotionEvent(ev: MotionEvent) {
        mOriginalX = ev.x
        mOriginalY = ev.y
    }

    private fun calculateDistanceX(ev: MotionEvent): Int {
        return Math.abs(mOriginalX - ev.x).toInt()
    }

    private fun calculateDistanceY(ev: MotionEvent): Int {
        return Math.abs(mOriginalY - ev.y).toInt()
    }
}