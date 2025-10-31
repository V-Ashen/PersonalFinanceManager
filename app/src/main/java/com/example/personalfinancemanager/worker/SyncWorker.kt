package com.example.personalfinancemanager.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.personalfinancemanager.data.AppDatabase
import com.example.personalfinancemanager.network.RetrofitInstance

class SyncWorker(appContext: Context, workerParams: WorkerParameters):
    CoroutineWorker(appContext, workerParams) {

    // Get a reference to our database and API
    private val db = AppDatabase.getDatabase(applicationContext)
    private val api = RetrofitInstance.api

    override suspend fun doWork(): Result {
        try {
            // 1. Get unsynced expenses from the local database
            val unsyncedExpenses = db.expenseDao().getUnsyncedExpenses()

            if (unsyncedExpenses.isEmpty()) {
                Log.i("SyncWorker", "No new data to sync. Work finished.")
                return Result.success() // Nothing to do, so it's a success
            }

            Log.i("SyncWorker", "Found ${unsyncedExpenses.size} expenses to sync.")

            // 2. Send the data to the API
            val response = api.syncExpenses(unsyncedExpenses)

            // 3. Handle the response
            return if (response.isSuccessful && response.body()?.status == "success") {
                // If sync was successful, update the local database
                val idsToUpdate = unsyncedExpenses.map { it.expenseId }
                db.expenseDao().updateSyncStatus(idsToUpdate)
                Log.i("SyncWorker", "Sync successful! Updated ${idsToUpdate.size} records.")
                Result.success()
            } else {
                // If the server returned an error, retry later
                Log.e("SyncWorker", "Sync failed. Server returned an error. Retrying.")
                Result.retry()
            }

        } catch (e: Exception) {
            // If there was a network error, retry later
            Log.e("SyncWorker", "Sync failed with exception: ${e.message}. Retrying.")
            return Result.retry()
        }
    }
}