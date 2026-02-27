package com.oura.ring.data.sync

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

actual class SyncScheduler(private val syncManager: SyncManager) {
    private var job: Job? = null

    actual fun schedulePeriodicSync(intervalMinutes: Int) {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                delay(intervalMinutes * 60 * 1000L)
                syncManager.syncAll()
            }
        }
    }

    actual fun cancelSync() {
        job?.cancel()
        job = null
    }
}
