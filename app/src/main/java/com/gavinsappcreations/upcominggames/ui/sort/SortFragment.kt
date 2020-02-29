package com.gavinsappcreations.upcominggames.ui.sort

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.gavinsappcreations.upcominggames.R
import com.gavinsappcreations.upcominggames.databinding.FragmentSortBinding
import com.gavinsappcreations.upcominggames.domain.SortOptions
import com.gavinsappcreations.upcominggames.ui.list.ListViewModel
import com.gavinsappcreations.upcominggames.utilities.SortDirection
import kotlinx.android.synthetic.main.fragment_sort.*

class SortFragment : Fragment() {

    private val viewModel: SortViewModel by lazy {
        ViewModelProvider(this, SortViewModel.Factory(requireActivity().application)).get(
            SortViewModel::class.java
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSortBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the SortViewModel
        binding.viewModel = viewModel

        binding.upNavigationImageButton.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }


    // If user is leaving the Fragment, update sortOptions so changes are fed to ListFragment.
    override fun onStop() {
        super.onStop()
        viewModel.updateSortOptions()
    }

}
