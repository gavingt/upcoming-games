package com.gavinsappcreations.upcominggames.ui.screenshot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.gavinsappcreations.upcominggames.databinding.FragmentScreenshotBinding
import com.ortiz.touchview.TouchImageView

class ScreenshotFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding = FragmentScreenshotBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Get list of screenshot URLs from arguments, and change URLs to get type of images we want.
        val images = ScreenshotFragmentArgs.fromBundle(requireArguments()).images.map {
            it.replace("scale_small", "scale_large")
        }
        val currentImageIndex = ScreenshotFragmentArgs.fromBundle(requireArguments()).currentImageIndex

        binding.viewPager.adapter = TouchImageAdapter(images)

        // Set viewPager item to the screenshot the user clicked in DetailFragment.
        binding.viewPager.currentItem = currentImageIndex

        // Allows us to reset zoom level of images once we've paged away from them.
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                /**
                 * If we've previously visited one of the neighboring pages and zoomed in, this
                 * will reset the zoom level before that page is made visible again.
                 */
                var currentView = binding.viewPager.findViewWithTag<TouchImageView>(position - 1)
                currentView?.resetZoom()

                currentView = binding.viewPager.findViewWithTag(position + 1)
                currentView?.resetZoom()
            }
        })

        return binding.root
    }

}
