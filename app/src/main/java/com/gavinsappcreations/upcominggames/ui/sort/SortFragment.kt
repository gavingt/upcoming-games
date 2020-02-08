package com.gavinsappcreations.upcominggames.ui.sort

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gavinsappcreations.upcominggames.databinding.FragmentSortBinding

class SortFragment : Fragment() {

    private val viewModel: SortViewModel by lazy {
        ViewModelProvider(this).get(SortViewModel::class.java)
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


        return binding.root
    }

}
