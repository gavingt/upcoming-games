package com.gavinsappcreations.upcominggames

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.work.*
import com.gavinsappcreations.upcominggames.work.UpdateGameListWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class App : Application() {

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        _applicationContext = this
        delayedInit()
    }


    private fun delayedInit() {
        applicationScope.launch {
            UpdateGameListWorker.setUpRecurringWork()
        }
    }


    companion object {
        private lateinit var _applicationContext: Context
        val applicationContext: Context
            get() = _applicationContext
    }
}