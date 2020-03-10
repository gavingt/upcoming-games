package com.gavinsappcreations.upcominggames.ui.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.gavinsappcreations.upcominggames.databinding.FragmentListBinding

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

        binding.gameRecyclerView.itemAnimator = null

        val adapter = GameGridAdapter(GameGridAdapter.OnClickListener {
            // TODO: use SingleLiveEvent to trigger navigation
            findNavController(this).navigate(
                ListFragmentDirections.actionListFragmentToDetailFragment(
                    it.guid
                )
            )
        })


        adapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver(){
            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                Log.d("MYLOG", "onItemRangeRemoved")
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                Log.d("MYLOG", "onItemRangeRemoved")
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                Log.d("MYLOG", "onItemRangeRemoved")
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                super.onItemRangeChanged(positionStart, itemCount)
                Log.d("MYLOG", "onItemRangeRemoved")
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                super.onItemRangeChanged(positionStart, itemCount, payload)
                Log.d("MYLOG", "onItemRangeRemoved")
            }
        })

        binding.gameRecyclerView.adapter = adapter

        binding.filterImageButton.setOnClickListener {
            // TODO: use SingleLiveEvent to trigger navigation
            findNavController(this).navigate(ListFragmentDirections.actionListFragmentToSortFragment())
        }

        binding.searchTextView.setOnClickListener {
            // TODO: use SingleLiveEvent to trigger navigation
            findNavController(this).navigate(ListFragmentDirections.actionListFragmentToSearchFragment())
        }

        viewModel.gameList.observe(viewLifecycleOwner, Observer {
            viewModel.updateNetworkState()
        })

        return binding.root
    }


}


