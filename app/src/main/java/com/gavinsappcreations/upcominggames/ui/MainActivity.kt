package com.gavinsappcreations.upcominggames.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gavinsappcreations.upcominggames.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)
    }
}
