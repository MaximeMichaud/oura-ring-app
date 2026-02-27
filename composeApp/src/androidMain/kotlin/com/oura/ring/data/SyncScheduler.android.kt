package com.oura.ring.data.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

actual class SyncScheduler(private val context: Context) {
    actual fun schedulePeriodicSync(intervalMinutes: Int) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<OuraSyncWorker>(
            intervalMinutes.toLong(), TimeUnit.MINUTES,
        ).setConstraints(constraints).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "oura_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }

    actual fun cancelSync() {
        WorkManager.getInstance(context).cancelUniqueWork("oura_sync")
    }
}
