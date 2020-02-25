package com.gavinsappcreations.upcominggames.ui.imageViewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.gavinsappcreations.upcominggames.databinding.FragmentImageViewerBinding
import com.ortiz.touchview.TouchImageView

class ImageViewerFragment : Fragment() {

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

        val binding = FragmentImageViewerBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        val images = ImageViewerFragmentArgs.fromBundle(arguments!!).images.map {
            it.replace("scale_small", "scale_large")
        }
        val currentImageIndex = ImageViewerFragmentArgs.fromBundle(arguments!!).currentImageIndex

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
                var currentView = binding.viewPager.getChildAt(position - 1)
                currentView?.let {
                    (it as TouchImageView).resetZoom()
                }

                currentView = binding.viewPager.getChildAt(position + 1)
                currentView?.let {
                    (it as TouchImageView).resetZoom()
                }
            }
        })

/*        viewModel.currentImageIndex.observe(viewLifecycleOwner, Observer {

        })*/


        return binding.root
    }

}
