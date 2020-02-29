package com.gavinsappcreations.upcominggames.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.gavinsappcreations.upcominggames.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private val viewModel: DetailViewModel by lazy {
        ViewModelProvider(
            this,
            DetailViewModel.Factory(
                requireActivity().application,
                DetailFragmentArgs.fromBundle(arguments!!).guid
            )
        ).get(
            DetailViewModel::class.java
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentDetailBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the DetailListViewModel
        binding.viewModel = viewModel

        binding.screenshotRecyclerView.adapter =
            ScreenshotAdapter(ScreenshotAdapter.OnClickListener { currentImageIndex ->
                val images = viewModel.gameDetail.value?.images?.toTypedArray()
                images?.let {
                    // TODO: use SingleLiveEvent to trigger navigation
                    findNavController().navigate(
                        DetailFragmentDirections.actionDetailFragmentToImageViewerFragment(
                            images,
                            currentImageIndex
                        )
                    )
                }
            })

        return binding.root
    }

}
