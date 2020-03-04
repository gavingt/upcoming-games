package com.gavinsappcreations.upcominggames.ui.sort.platform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.gavinsappcreations.upcominggames.R


class PlatformDialogFragment : DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Pick a style based on the num.
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout: View = inflater.inflate(R.layout.platform_dialog, container, false)



        return layout
    }

/*    companion object {
        *//**
         * Create a new instance of MyDialogFragment, providing "num" as an argument.
         *//*
        fun newInstance(): CustomDateRangeDialogFragment {
            val fragment = CustomDateRangeDialogFragment()
            // Supply num input as an argument.
            val args = Bundle()
            args.putInt("num", num)
            fragment.arguments = args
            return fragment
        }
    }*/
}