package com.gavinsappcreations.upcominggames

import android.app.Application
import android.content.Context
import androidx.work.*
import com.gavinsappcreations.upcominggames.repository.GameRepository
import com.gavinsappcreations.upcominggames.work.UpdateGameListWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize the GameRepository object here, so it's ready immediately for Fragments to use.
        _gameRepository = GameRepository.getInstance(this)
        delayedInit()
    }


    private fun delayedInit() {
        // Set up the WorkManager worker.
        CoroutineScope(Dispatchers.Default).launch {
            setUpRecurringWork()
        }
    }


    private fun setUpRecurringWork() {
        val constraints = Constraints.Builder()
            .setRequiresStorageNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val repeatingRequest =
            PeriodicWorkRequestBuilder<UpdateGameListWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .setInitialDelay(1, TimeUnit.DAYS)
                .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            UpdateGameListWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }


    companion object {
        private lateinit var _gameRepository: GameRepository
        val gameRepository: GameRepository
            get() = _gameRepository
    }
}