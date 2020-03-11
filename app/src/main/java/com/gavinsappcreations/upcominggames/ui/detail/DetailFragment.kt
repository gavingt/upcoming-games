package com.gavinsappcreations.upcominggames.ui.detail

import android.content.Intent
import android.net.Uri
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

        binding.upNavigationImageButton.setOnClickListener {
            // TODO: this should be done in ViewModel
            findNavController().popBackStack()
        }

        binding.gameLinkImageButton.setOnClickListener {
            val url = viewModel.gameDetail.value?.detailUrl
            url?.let {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                startActivity(intent)
            }
        }

        binding.screenshotRecyclerView.adapter =
            ScreenshotAdapter(ScreenshotAdapter.OnClickListener { currentImageIndex ->
                val images = viewModel.gameDetail.value?.images?.toTypedArray()
                images?.let {
                    // TODO: use SingleLiveEvent to trigger navigation
                    findNavController().navigate(
                        DetailFragmentDirections.actionDetailFragmentToScreenshotFragment(
                            images,
                            currentImageIndex
                        )
                    )
                }
            })

        binding.scrollView.setOnScrollChangeListener { scrollView, _, _, _, _ ->
            binding.horizontalLineView.visibility =
                if (scrollView.canScrollVertically(-1)) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }


        return binding.root
    }

}
