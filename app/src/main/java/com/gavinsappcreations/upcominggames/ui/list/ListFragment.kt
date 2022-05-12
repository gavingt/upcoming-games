package com.gavinsappcreations.upcominggames.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.gavinsappcreations.upcominggames.databinding.FragmentListBinding

// TODO: if connection drops mid-update, the app doesn't notice it and it just shows an endless loading bar

class ListFragment : Fragment() {

    private val viewModel: ListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentListBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        // Prevents jarring animations from occurring when changing filter options.
        binding.gameRecyclerView.itemAnimator = null

        val adapter = GameGridAdapter(GameGridAdapter.OnClickListener {
            viewModel.onNavigateToDetailFragment(it)
        })

        binding.gameRecyclerView.adapter = adapter

        // When gameList changes, we update databaseState to its next state.
        viewModel.gameList.observe(viewLifecycleOwner) {
            viewModel.updateDatabaseState()
        }


        viewModel.navigateToDetailFragment.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { game ->
                findNavController(this).navigate(
                    ListFragmentDirections.actionListFragmentToDetailFragment(
                        game.guid
                    )
                )
            }
        }

        viewModel.navigateToFilterFragment.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                findNavController(this).navigate(ListFragmentDirections.actionListFragmentToFilterFragment())
            }
        }

        viewModel.navigateToSearchFragment.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                findNavController(this).navigate(ListFragmentDirections.actionListFragmentToSearchFragment())
            }
        }

        viewModel.navigateToFavoriteFragment.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                findNavController(this).navigate(ListFragmentDirections.actionListFragmentToFavoriteFragment())
            }
        }

        // When user requests a manual update of the database, process that request.
        viewModel.requestUpdateDatabase.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                viewModel.updateDatabaseManually()
            }
        }

        return binding.root
    }

}



