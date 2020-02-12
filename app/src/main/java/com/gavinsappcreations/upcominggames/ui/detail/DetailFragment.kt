package com.gavinsappcreations.upcominggames.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gavinsappcreations.upcominggames.databinding.FragmentDetailBinding
import com.gavinsappcreations.upcominggames.databinding.FragmentSearchBinding
import com.gavinsappcreations.upcominggames.ui.list.ListViewModel

class DetailFragment : Fragment() {

    private val viewModel: DetailViewModel by lazy {
        ViewModelProvider(this, DetailViewModel.Factory(requireActivity().application)).get(
            DetailViewModel::class.java
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentDetailBinding.inflate(inflater)

        val arguments = DetailFragmentArgs.fromBundle(arguments!!)
        binding.textTextView.text = arguments.game.gameName


        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the DetailListViewModel
        binding.viewModel = viewModel




        return binding.root
    }

}
