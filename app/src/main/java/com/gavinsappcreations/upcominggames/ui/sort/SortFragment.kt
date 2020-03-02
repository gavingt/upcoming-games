package com.gavinsappcreations.upcominggames.ui.sort

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.gavinsappcreations.upcominggames.databinding.FragmentSortBinding
import com.gavinsappcreations.upcominggames.ui.sort.customDateRange.PlatformDialogFragment
import com.gavinsappcreations.upcominggames.utilities.DateInputMask
import com.gavinsappcreations.upcominggames.utilities.ReleaseDateType
import com.google.android.material.textfield.TextInputEditText
import java.util.*


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

        DateInputMask(binding.startDateTextInputEditText).listen()
        DateInputMask(binding.endDateTextInputEditText).listen()

        viewModel.sortOptions.observe(viewLifecycleOwner, Observer {

            //Any time sortOptions change, save the new ones to SharedPrefs.
            viewModel.updateSortOptions()

            if (it.releaseDateType == ReleaseDateType.CustomRange) {
                // TODO: use SingleLiveEvent to trigger showing TextInputLayouts
            }
        })

        return binding.root
    }


    fun showPlatformDialog() {
        val ft: FragmentTransaction = childFragmentManager.beginTransaction()
        val prev: Fragment? = childFragmentManager.findFragmentByTag("dialog")
        // Remove any previous instances of this dialog.
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)

        // Create and show the dialog.
        val newFragment: DialogFragment = PlatformDialogFragment()
        newFragment.show(ft, "dialog")
    }


/*    // If user is leaving the Fragment, update sortOptions so changes are fed to ListFragment.
    override fun onStop() {
        super.onStop()
        viewModel.updateSortOptions()
    }*/

}



