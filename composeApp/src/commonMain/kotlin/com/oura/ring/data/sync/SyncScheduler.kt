package com.oura.ring.data.sync

expect class SyncScheduler {
    fun schedulePeriodicSync(intervalMinutes: Int)

    fun cancelSync()
}
