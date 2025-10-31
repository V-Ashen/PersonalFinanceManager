package com.example.personalfinancemanager

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.personalfinancemanager.worker.SyncWorker
import java.util.concurrent.TimeUnit

class FinanceApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        schedulePeriodicSync()
    }

    private fun schedulePeriodicSync() {
        // Define constraints: Only run when there's an active network connection.
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Create the request to run every 8 hours.
        // Note: Android may delay this to save battery. Minimum interval is 15 minutes.
        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(8, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        // Schedule the work, ensuring only one instance of this periodic task is running.
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "ExpenseSyncWork", // A unique name for our task
            ExistingPeriodicWorkPolicy.KEEP, // If it's already scheduled, keep the existing one
            syncRequest
        )
    }
}