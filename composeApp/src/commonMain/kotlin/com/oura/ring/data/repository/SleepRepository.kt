package com.oura.ring.data.repository

import com.oura.ring.data.api.ApiDailySleep
import com.oura.ring.data.api.ApiSleep
import com.oura.ring.data.api.ApiSleepTime
import com.oura.ring.data.api.OuraApiClient
import com.oura.ring.data.api.OuraPageResponse
import com.oura.ring.db.OuraDatabase
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

data class DayValue(val day: String, val value: Double)
data class SleepPhaseBreakdown(val deep: Double, val light: Double, val rem: Double, val awake: Double)

class SleepRepository(
    private val db: OuraDatabase,
    private val api: OuraApiClient,
    private val json: Json,
) {
    // --- Sync ---

    suspend fun syncSleep(start: String, end: String): Int {
        val records = api.fetchAll<ApiSleep>("sleep", start, end)
        db.transaction {
            records.forEach { r ->
                val day = r.day ?: return@forEach
                db.sleepQueries.upsert(
                    id = r.id,
                    day = day,
                    bedtime_start = r.bedtimeStart,
                    bedtime_end = r.bedtimeEnd,
                    duration = r.timeInBed?.toLong(),
                    total_sleep = r.totalSleepDuration?.toLong(),
                    awake_time = r.awakeTime?.toLong(),
                    light_sleep = r.lightSleepDuration?.toLong(),
                    deep_sleep = r.deepSleepDuration?.toLong(),
                    rem_sleep = r.remSleepDuration?.toLong(),
                    restless_periods = r.restlessPeriods?.toLong(),
                    efficiency = r.efficiency?.toLong(),
                    latency = r.latency?.toLong(),
                    type = r.type,
                    readiness_score_delta = r.readinessScoreDelta?.toLong(),
                    average_breath = r.averageBreath,
                    average_heart_rate = r.averageHeartRate,
                    average_hrv = r.averageHrv,
                    lowest_heart_rate = r.lowestHeartRate?.toLong(),
                    heart_rate = r.heartRate?.let { json.encodeToString(it) },
                    hrv = r.hrv?.let { json.encodeToString(it) },
                    sleep_phase_5_min = r.sleepPhase5Min,
                    movement_30_sec = r.movement30Sec,
                    sleep_score_delta = r.sleepScoreDelta,
                    period = r.period?.toLong(),
                    low_battery_alert = if (r.lowBatteryAlert == true) 1L else 0L,
                )
            }
        }
        return records.size
    }

    suspend fun syncDailySleep(start: String, end: String): Int {
        val records = api.fetchAll<ApiDailySleep>("daily_sleep", start, end)
        db.transaction {
            records.forEach { r ->
                db.dailySleepQueries.upsert(
                    day = r.day,
                    score = r.score?.toLong(),
                    contributors_deep_sleep = r.contributors?.deepSleep?.toLong(),
                    contributors_efficiency = r.contributors?.efficiency?.toLong(),
                    contributors_latency = r.contributors?.latency?.toLong(),
                    contributors_rem_sleep = r.contributors?.remSleep?.toLong(),
                    contributors_restfulness = r.contributors?.restfulness?.toLong(),
                    contributors_timing = r.contributors?.timing?.toLong(),
                    contributors_total_sleep = r.contributors?.totalSleep?.toLong(),
                )
            }
        }
        return records.size
    }

    suspend fun syncSleepTime(start: String, end: String): Int {
        val records = api.fetchAll<ApiSleepTime>("sleep_time", start, end)
        db.transaction {
            records.forEach { r ->
                val day = r.day ?: return@forEach
                db.sleepTimeQueries.upsert(
                    id = r.id,
                    day = day,
                    optimal_bedtime_start = r.optimalBedtime?.startOffset?.toLong(),
                    optimal_bedtime_end = r.optimalBedtime?.endOffset?.toLong(),
                    optimal_bedtime_tz = r.optimalBedtime?.dayTz?.toLong(),
                    recommendation = r.recommendation,
                    status = r.status,
                )
            }
        }
        return records.size
    }

    // --- Read queries ---

    fun availableNights(start: String, end: String): List<String> =
        db.sleepQueries.availableNights(start, end).executeAsList()

    fun sleepSession(night: String) =
        db.sleepQueries.selectPrimaryByDay(night).executeAsOneOrNull()

    fun sleepPhaseBreakdown(night: String): SleepPhaseBreakdown? {
        val s = sleepSession(night) ?: return null
        return SleepPhaseBreakdown(
            deep = (s.deep_sleep ?: 0) / 60.0,
            light = (s.light_sleep ?: 0) / 60.0,
            rem = (s.rem_sleep ?: 0) / 60.0,
            awake = (s.awake_time ?: 0) / 60.0,
        )
    }

    fun sleepDurationBreakdown(start: String, end: String): List<SleepDurationPoint> {
        val rows = db.sleepQueries.selectPrimaryInRange(start, end).executeAsList()
        val seen = mutableSetOf<String>()
        return rows.filter { seen.add(it.day) }.map { s ->
            SleepDurationPoint(
                day = s.day,
                deep = (s.deep_sleep ?: 0) / 3600.0,
                light = (s.light_sleep ?: 0) / 3600.0,
                rem = (s.rem_sleep ?: 0) / 3600.0,
                awake = (s.awake_time ?: 0) / 3600.0,
            )
        }
    }

    fun hrvTrend(start: String, end: String): List<DayValue> {
        val rows = db.sleepQueries.selectPrimaryInRange(start, end).executeAsList()
        val seen = mutableSetOf<String>()
        return rows.filter { seen.add(it.day) }
            .mapNotNull { s -> s.average_hrv?.let { DayValue(s.day, it) } }
    }

    fun restingHrTrend(start: String, end: String): List<DayValue> {
        val rows = db.sleepQueries.selectPrimaryInRange(start, end).executeAsList()
        val seen = mutableSetOf<String>()
        return rows.filter { seen.add(it.day) }
            .mapNotNull { s -> s.lowest_heart_rate?.let { DayValue(s.day, it.toDouble()) } }
    }

    fun efficiencyTrend(start: String, end: String): List<DayValue> {
        val rows = db.sleepQueries.selectPrimaryInRange(start, end).executeAsList()
        val seen = mutableSetOf<String>()
        return rows.filter { seen.add(it.day) }
            .mapNotNull { s -> s.efficiency?.let { DayValue(s.day, it.toDouble()) } }
    }

    fun latencyTrend(start: String, end: String): List<DayValue> {
        val rows = db.sleepQueries.selectPrimaryInRange(start, end).executeAsList()
        val seen = mutableSetOf<String>()
        return rows.filter { seen.add(it.day) }
            .mapNotNull { s -> s.latency?.let { DayValue(s.day, it / 60.0) } }
    }

    fun breathingTrend(start: String, end: String): List<DayValue> {
        val rows = db.sleepQueries.selectPrimaryInRange(start, end).executeAsList()
        val seen = mutableSetOf<String>()
        return rows.filter { seen.add(it.day) }
            .mapNotNull { s -> s.average_breath?.let { DayValue(s.day, it) } }
    }

    fun dailySleepInRange(start: String, end: String) =
        db.dailySleepQueries.selectInRange(start, end).executeAsList()

    fun latestDailySleep(end: String) =
        db.dailySleepQueries.selectLatest(end).executeAsOneOrNull()

    fun latestSleepTime(end: String) =
        db.sleepTimeQueries.selectLatest(end).executeAsOneOrNull()

    fun napFrequency(start: String, end: String) =
        db.sleepQueries.napFrequency(start, end).executeAsList()
}

data class SleepDurationPoint(
    val day: String,
    val deep: Double,
    val light: Double,
    val rem: Double,
    val awake: Double,
)
