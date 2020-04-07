package com.gavinsappcreations.upcominggames.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.gavinsappcreations.upcominggames.databinding.FragmentFavoriteBinding
import com.gavinsappcreations.upcominggames.ui.list.GameGridAdapter

class FavoriteFragment : Fragment() {

    private val viewModel: FavoriteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFavoriteBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        // Sets topHorizontalLineView to visible only if scrollView isn't scrolled to top.
        binding.gameRecyclerView.setOnScrollChangeListener { scrollView, _, _, _, _ ->
            binding.topHorizontalLineView.visibility =
                if (scrollView.canScrollVertically(-1)) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }

        val adapter = GameGridAdapter(GameGridAdapter.OnClickListener {
            viewModel.onNavigateToDetailFragment(it)
        })

        binding.gameRecyclerView.adapter = adapter

        viewModel.popBackStack.observe(viewLifecycleOwner, Observer {
            if (it.getContentIfNotHandled() == true) {
                findNavController().popBackStack()
            }
        })

        viewModel.navigateToDetailFragment.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { game ->
                NavHostFragment.findNavController(this).navigate(
                    FavoriteFragmentDirections.actionFavoriteFragmentToDetailFragment(
                        game.guid
                    )
                )
            }
        })

        return binding.root
    }

}
