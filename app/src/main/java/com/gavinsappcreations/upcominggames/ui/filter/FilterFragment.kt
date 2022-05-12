package com.gavinsappcreations.upcominggames.ui.filter

import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.gavinsappcreations.upcominggames.databinding.FragmentFilterBinding
import com.gavinsappcreations.upcominggames.utilities.hideKeyboard

class FilterFragment : Fragment() {

    private val viewModel: FilterViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentFilterBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.executePendingBindings()

        // Initialize adapter for picking platforms from list, and inject onCheckedChangeListener.
        binding.platformRecyclerView.adapter =
            PlatformAdapter(
                viewModel.unsavedFilterOptions,
                PlatformAdapter.OnCheckedChangeListener { platformIndex, isChecked ->
                    viewModel.onPlatformCheckedChange(platformIndex, isChecked)
                })

        // Attach DateInputTextWatchers to date editTexts.
        DateInputTextWatcher(binding.startDateTextInputEditText).listen()
        DateInputTextWatcher(binding.endDateTextInputEditText).listen()

        // When applyButton is pressed, save the user's new filter options.
        binding.applyButton.setOnClickListener {
            viewModel.onUpdateFilterOptions(
                binding.startDateTextInputEditText.error?.toString(),
                binding.startDateTextInputEditText.text?.toString(),
                binding.endDateTextInputEditText.error?.toString(),
                binding.endDateTextInputEditText.text?.toString()
            )
        }

        // Sets topHorizontalLineView to visible only if scrollView isn't scrolled to top.
        binding.nestedScrollView.setOnScrollChangeListener { scrollView, _, _, _, _ ->
            binding.topHorizontalLineView.visibility =
                if (scrollView.canScrollVertically(-1)) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }

        viewModel.popBackStack.observe(viewLifecycleOwner) {
            hideKeyboard(binding.startDateTextInputEditText)
            findNavController().popBackStack()
        }

        /**
         * If updateFilterOptions = true, inputs are valid and we save new filter options.
         * Otherwise, we display a Toast alerting the user of the invalid date.
         */
        viewModel.updateFilterOptions.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { updateFilterOptions ->
                if (updateFilterOptions) {
                    viewModel.saveNewFilterOptions()
                    hideKeyboard(binding.startDateTextInputEditText)
                    findNavController().popBackStack()
                } else {
                    displayInvalidDateToast()
                }
            }
        }

        return binding.root
    }


    // Alerts user that they can't apply changes to filter options without entering valid dates.
    private fun displayInvalidDateToast() {
        Toast.makeText(
            requireContext(),
            "Before proceeding, you must enter valid dates in the \"Release date\" section.",
            Toast.LENGTH_LONG
        ).show()

        val vibrator =
            requireActivity().getSystemService(VIBRATOR_SERVICE) as Vibrator?
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator?.vibrate(
                VibrationEffect.createOneShot(
                    200,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            vibrator?.vibrate(200)
        }
    }


}

