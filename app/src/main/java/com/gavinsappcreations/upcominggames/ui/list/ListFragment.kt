package com.gavinsappcreations.upcominggames.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.gavinsappcreations.upcominggames.databinding.FragmentListBinding
import com.gavinsappcreations.upcominggames.domain.SortOptions
import com.gavinsappcreations.upcominggames.utilities.SortDirection

class ListFragment : Fragment() {

    private val viewModel: ListViewModel by lazy {
        ViewModelProvider(this, ListViewModel.Factory(requireActivity().application)).get(
            ListViewModel::class.java
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentListBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the GameListViewModel
        binding.viewModel = viewModel

        binding.gameRecyclerView.adapter = GameGridAdapter(GameGridAdapter.OnClickListener {
            // TODO: use SingleLiveEvent to trigger navigation
            findNavController(this).navigate(ListFragmentDirections.actionListFragmentToDetailFragment(it.guid))
        })

        binding.filterImageButton.setOnClickListener {
            // TODO: use SingleLiveEvent to trigger navigation
            findNavController(this).navigate(ListFragmentDirections.actionListFragmentToSortFragment())
        }

        binding.searchTextView.setOnClickListener {
            // TODO: use SingleLiveEvent to trigger navigation
            findNavController(this).navigate(ListFragmentDirections.actionListFragmentToSearchFragment())
        }

        return binding.root
    }

}


