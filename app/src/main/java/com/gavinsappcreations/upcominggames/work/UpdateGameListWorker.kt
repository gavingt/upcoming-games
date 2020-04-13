package com.gavinsappcreations.upcominggames.work

import android.content.Context
import androidx.work.*
import com.gavinsappcreations.upcominggames.App
import retrofit2.HttpException

/**
 * Sets up a worker that periodically queries the API in order to update the database.
 */

class UpdateGameListWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        // Uniquely identifies our worker.
        const val WORK_NAME = "UpdateGameListWorker"
    }

    override suspend fun doWork(): Result {
        val gameRepository = App.gameRepository

        return try {
            gameRepository.updateGameListData(false)
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }

}