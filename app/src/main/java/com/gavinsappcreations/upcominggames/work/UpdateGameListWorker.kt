package com.gavinsappcreations.upcominggames.work

import android.content.Context
import android.util.Log
import androidx.work.*
import com.gavinsappcreations.upcominggames.App.Companion.applicationContext
import com.gavinsappcreations.upcominggames.repository.GameRepository
import retrofit2.HttpException
import java.util.concurrent.TimeUnit

class UpdateGameListWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        private const val WORK_NAME = "UpdateGameListWorker"

        fun setUpRecurringWork() {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresDeviceIdle(true)
                .setRequiresBatteryNotLow(true)
                .build()

            // TODO: change back to DAYS instead of HOURS
            val repeatingRequest =
                PeriodicWorkRequestBuilder<UpdateGameListWorker>(1, TimeUnit.HOURS)
                    .setConstraints(constraints)
                    .setInitialDelay(1, TimeUnit.HOURS)
                    .build()

            WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                repeatingRequest
            )
        }
    }

    override suspend fun doWork(): Result {
        val repository = GameRepository.getInstance(applicationContext)

        return try {
            repository.updateGameListData(false)
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }

}