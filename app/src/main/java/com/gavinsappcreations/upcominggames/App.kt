package com.gavinsappcreations.upcominggames

import android.app.Application
import android.content.Context

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        _applicationContext = this
    }

    companion object {
        private lateinit var _applicationContext: Context
        val applicationContext: Context
            get() = _applicationContext
    }
}