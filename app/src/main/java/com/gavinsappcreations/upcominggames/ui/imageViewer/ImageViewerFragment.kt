package com.gavinsappcreations.upcominggames.ui.imageViewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.gavinsappcreations.upcominggames.R
import com.gavinsappcreations.upcominggames.databinding.FragmentImageViewerBinding

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

/*        viewModel.currentImageIndex.observe(viewLifecycleOwner, Observer {

        })*/


        return binding.root
    }

}
