package com.gavinsappcreations.upcominggames.ui.screenshot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.children
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.gavinsappcreations.upcominggames.databinding.FragmentScreenshotBinding
import com.ortiz.touchview.TouchImageView

class ScreenshotFragment : Fragment() {

/*    private val viewModel: ImageViewerViewModel by lazy {
        ViewModelProvider(
            this,
            ImageViewerViewModel.Factory(
                ImageViewerFragmentArgs.fromBundle(arguments!!).images,
                ImageViewerFragmentArgs.fromBundle(arguments!!).currentImageIndex
            )
        ).get(
            ImageViewerViewModel::class.java
        )
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentScreenshotBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        val images = ScreenshotFragmentArgs.fromBundle(arguments!!).images.map {
            it.replace("scale_small", "scale_large")
        }
        val currentImageIndex = ScreenshotFragmentArgs.fromBundle(arguments!!).currentImageIndex

        binding.viewPager.adapter = TouchImageAdapter(images)
        binding.viewPager.currentItem = currentImageIndex

        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {}

            override fun onPageSelected(position: Int) {
                /**
                 * If we've previously visited one of the neighboring pages and zoomed in, this
                 * will reset the zoom level before that page is made visible again.
                 */
                var currentView = binding.viewPager.findViewWithTag<TouchImageView>(position - 1)
                currentView?.resetZoom()

                currentView = binding.viewPager.findViewWithTag<TouchImageView>(position + 1)
                currentView?.resetZoom()
            }
        })

/*        viewModel.currentImageIndex.observe(viewLifecycleOwner, Observer {

        })*/


        return binding.root
    }

}
