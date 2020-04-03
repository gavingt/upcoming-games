package com.gavinsappcreations.upcominggames.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.gavinsappcreations.upcominggames.databinding.FragmentSearchBinding
import com.gavinsappcreations.upcominggames.utilities.hideKeyboard
import com.gavinsappcreations.upcominggames.utilities.showSoftKeyboard


class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSearchBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        // Prevents distracting animation from occurring when new search results come in.
        binding.searchRecyclerView.itemAnimator = null

        binding.searchRecyclerView.adapter = SearchAdapter(SearchAdapter.OnClickListener {
            viewModel.onNavigateToDetailFragment(it)
        })

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // When searchEditText changes, re-query the database.
                viewModel.onSearchQueryChanged(s.toString())
            }
        })

        // We need to call this immediately to show recent search results before user has typed anything.
        viewModel.onSearchQueryChanged("")

        binding.upNavigationImageButton.setOnClickListener {
            viewModel.onPopBackStack()
        }

        // Tell ViewModel to show keyboard immediately upon entering Fragment.
        viewModel.onShowKeyboard()

        // For some reason we need to slightly delay showing the keyboard for it to actually show.
        viewModel.showKeyboard.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                binding.root.postDelayed({
                    showSoftKeyboard(binding.searchEditText)
                }, 50)
                viewModel.onShowKeyboard()
            }
        })

        viewModel.navigateToDetailFragment.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { game ->
                hideKeyboard(binding.searchEditText)
                NavHostFragment.findNavController(this).navigate(
                    SearchFragmentDirections.actionSearchFragmentToDetailFragment(
                        game.guid
                    )
                )
            }
        })

        viewModel.popBackStack.observe(viewLifecycleOwner, Observer {
            hideKeyboard(binding.searchEditText)
            findNavController().popBackStack()
        })

        return binding.root
    }
}
