package com.gavinsappcreations.upcominggames.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gavinsappcreations.upcominggames.BuildConfig
import com.gavinsappcreations.upcominggames.R

class MainActivity : AppCompatActivity() {


/*    // Create sharedViewModel as an AndroidViewModel, passing in Application to the Factory.
    private val sharedViewModel: SharedViewModel by lazy {
        ViewModelProvider(this, SharedViewModel.Factory(application)).get(SharedViewModel::class.java)
    }*/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
