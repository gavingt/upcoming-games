package com.gavinsappcreations.upcominggames.ui.sort

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.gavinsappcreations.upcominggames.R
import com.gavinsappcreations.upcominggames.databinding.FragmentSortBinding
import com.gavinsappcreations.upcominggames.ui.list.ListViewModel
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

        binding.releaseDateRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            viewModel.onReleaseDateSelectionChanged(checkedId)
        }

        binding.applyButton.setOnClickListener {
            viewModel.saveSortOptions()
            findNavController().popBackStack()
        }

        binding.testButton.setOnClickListener {
            viewModel.onSortDirectionChanged()
        }

        return binding.root
    }

}
