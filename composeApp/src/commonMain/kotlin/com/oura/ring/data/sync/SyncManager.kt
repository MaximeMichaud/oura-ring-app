package com.oura.ring.data.sync

import com.oura.ring.data.api.ALL_ENDPOINTS
import com.oura.ring.data.api.TokenExpiredException
import com.oura.ring.data.repository.ActivityRepository
import com.oura.ring.data.repository.BodyRepository
import com.oura.ring.data.repository.ReadinessRepository
import com.oura.ring.data.repository.SleepRepository
import com.oura.ring.data.repository.SyncRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

sealed class SyncResult {
    data class Success(
        val counts: Map<String, Int>,
    ) : SyncResult()

    data object AlreadyRunning : SyncResult()

    data object TokenExpired : SyncResult()

    data class Error(
        val message: String,
    ) : SyncResult()
}

class SyncManager(
    private val sleepRepo: SleepRepository,
    private val readinessRepo: ReadinessRepository,
    private val activityRepo: ActivityRepository,
    private val bodyRepo: BodyRepository,
    private val syncRepo: SyncRepository,
) {
    private val mutex = Mutex()

    companion object {
        const val OVERLAP_DAYS = 2
        const val DEFAULT_HISTORY_START = "2020-01-01"
    }

    suspend fun syncAll(): SyncResult {
        if (!mutex.tryLock()) return SyncResult.AlreadyRunning

        try {
            val today =
                Clock.System
                    .now()
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .date
                    .toString()

            val results = mutableMapOf<String, Int>()

            for (endpoint in ALL_ENDPOINTS) {
                try {
                    val startDate = getStartDate(endpoint.name)
                    val count = syncEndpoint(endpoint.name, startDate, today)
                    syncRepo.recordSuccess(endpoint.name, today, count)
                    results[endpoint.name] = count
                } catch (e: TokenExpiredException) {
                    return SyncResult.TokenExpired
                } catch (e: Exception) {
                    syncRepo.recordFailure(endpoint.name, e.message ?: "Unknown error")
                }
            }
            return SyncResult.Success(results)
        } catch (e: Exception) {
            return SyncResult.Error(e.message ?: "Unknown error")
        } finally {
            mutex.unlock()
        }
    }

    private fun getStartDate(endpointName: String): String {
        val lastSync = syncRepo.getLastSyncDate(endpointName)
        return if (lastSync != null) {
            LocalDate
                .parse(lastSync)
                .minus(OVERLAP_DAYS, DateTimeUnit.DAY)
                .toString()
        } else {
            DEFAULT_HISTORY_START
        }
    }

    private suspend fun syncEndpoint(
        name: String,
        start: String,
        end: String,
    ): Int =
        when (name) {
            "sleep" -> sleepRepo.syncSleep(start, end)
            "daily_sleep" -> sleepRepo.syncDailySleep(start, end)
            "daily_readiness" -> readinessRepo.syncFromApi(start, end)
            "daily_activity" -> activityRepo.syncActivity(start, end)
            "daily_spo2" -> bodyRepo.syncSpo2(start, end)
            "daily_stress" -> bodyRepo.syncStress(start, end)
            "daily_resilience" -> bodyRepo.syncResilience(start, end)
            "daily_cardiovascular_age" -> bodyRepo.syncCardioAge(start, end)
            "daily_vo2_max" -> bodyRepo.syncVo2Max(start, end)
            "workout" -> activityRepo.syncWorkouts(start, end)
            "sleep_time" -> sleepRepo.syncSleepTime(start, end)
            else -> 0
        }
}
