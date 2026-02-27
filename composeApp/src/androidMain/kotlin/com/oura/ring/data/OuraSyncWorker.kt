package com.oura.ring.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.oura.ring.data.sync.SyncManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class OuraSyncWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params), KoinComponent {

    private val syncManager: SyncManager by inject()

    override suspend fun doWork(): Result {
        return when (syncManager.syncAll()) {
            is SyncResult.Success -> Result.success()
            is SyncResult.TokenExpired -> Result.failure()
            is SyncResult.AlreadyRunning -> Result.retry()
            is SyncResult.Error -> Result.retry()
        }
    }
}
