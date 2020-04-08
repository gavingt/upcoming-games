package com.gavinsappcreations.upcominggames

import android.app.Application
import android.content.Context
import com.gavinsappcreations.upcominggames.repository.GameRepository
import com.gavinsappcreations.upcominggames.work.UpdateGameListWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        _applicationContext = this

        // Initialize the GameRepository object here, so it's ready immediately for Fragments to use.
        _gameRepository = GameRepository
        delayedInit()
    }


    private fun delayedInit() {
        // Set up the WorkManager worker.
        CoroutineScope(Dispatchers.Default).launch {
            UpdateGameListWorker.setUpRecurringWork()
        }
    }


    companion object {
        // Lets us get application Context wherever we need it.
        private lateinit var _applicationContext: Context
        val applicationContext: Context
            get() = _applicationContext

        private lateinit var _gameRepository: GameRepository
        val gameRepository: GameRepository
            get() = _gameRepository
    }
}