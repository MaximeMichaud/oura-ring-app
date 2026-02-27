package com.oura.ring.data.repository

import com.oura.ring.data.api.ApiDailyCardiovascularAge
import com.oura.ring.data.api.ApiDailyResilience
import com.oura.ring.data.api.ApiDailySpo2
import com.oura.ring.data.api.ApiDailyStress
import com.oura.ring.data.api.ApiDailyVo2Max
import com.oura.ring.data.api.OuraApiClient
import com.oura.ring.db.OuraDatabase

class BodyRepository(
    private val db: OuraDatabase,
    private val api: OuraApiClient,
) {
    // --- Sync ---

    suspend fun syncSpo2(
        start: String,
        end: String,
    ): Int {
        val records = api.fetchAll<ApiDailySpo2>("daily_spo2", start, end)
        db.transaction {
            records.forEach { r ->
                db.dailySpo2Queries.upsert(
                    day = r.day,
                    spo2_percentage_average = r.spo2Percentage?.average,
                    breathing_disturbance_index = r.breathingDisturbanceIndex,
                )
            }
        }
        return records.size
    }

    suspend fun syncStress(
        start: String,
        end: String,
    ): Int {
        val records = api.fetchAll<ApiDailyStress>("daily_stress", start, end)
        db.transaction {
            records.forEach { r ->
                db.dailyStressQueries.upsert(
                    day = r.day,
                    stress_high = r.stressHigh?.toLong(),
                    recovery_high = r.recoveryHigh?.toLong(),
                    day_summary = r.daySummary,
                )
            }
        }
        return records.size
    }

    suspend fun syncResilience(
        start: String,
        end: String,
    ): Int {
        val records = api.fetchAll<ApiDailyResilience>("daily_resilience", start, end)
        db.transaction {
            records.forEach { r ->
                db.dailyResilienceQueries.upsert(
                    day = r.day,
                    level = r.level,
                    contributors_sleep_recovery = r.contributors?.sleepRecovery,
                    contributors_daytime_recovery = r.contributors?.daytimeRecovery,
                    contributors_stress = r.contributors?.stress,
                )
            }
        }
        return records.size
    }

    suspend fun syncCardioAge(
        start: String,
        end: String,
    ): Int {
        val records = api.fetchAll<ApiDailyCardiovascularAge>("daily_cardiovascular_age", start, end)
        db.transaction {
            records.forEach { r ->
                db.dailyCardiovascularAgeQueries.upsert(
                    day = r.day,
                    vascular_age = r.vascularAge?.toLong(),
                )
            }
        }
        return records.size
    }

    suspend fun syncVo2Max(
        start: String,
        end: String,
    ): Int {
        val records = api.fetchAll<ApiDailyVo2Max>("vO2_max", start, end)
        db.transaction {
            records.forEach { r ->
                db.dailyVo2MaxQueries.upsert(
                    day = r.day,
                    vo2_max = r.vo2Max,
                )
            }
        }
        return records.size
    }

    // --- Read: SpO2 ---

    fun latestSpo2(end: String) = db.dailySpo2Queries.selectLatest(end).executeAsOneOrNull()

    fun spo2Trend(
        start: String,
        end: String,
    ): List<DayValue> =
        db.dailySpo2Queries
            .selectInRange(start, end)
            .executeAsList()
            .mapNotNull { r -> r.spo2_percentage_average?.let { DayValue(r.day, it) } }

    // --- Read: Stress ---

    fun latestStress(end: String) = db.dailyStressQueries.selectLatest(end).executeAsOneOrNull()

    fun stressTrend(
        start: String,
        end: String,
    ) = db.dailyStressQueries.selectInRange(start, end).executeAsList()

    // --- Read: Resilience ---

    fun latestResilience(end: String) = db.dailyResilienceQueries.selectLatest(end).executeAsOneOrNull()

    fun resilienceTimeline(
        start: String,
        end: String,
    ) = db.dailyResilienceQueries.selectInRange(start, end).executeAsList()

    // --- Read: Cardiovascular Age ---

    fun latestCardioAge(end: String) = db.dailyCardiovascularAgeQueries.selectLatest(end).executeAsOneOrNull()

    fun cardioAgeTrend(
        start: String,
        end: String,
    ): List<DayValue> =
        db.dailyCardiovascularAgeQueries
            .selectInRange(start, end)
            .executeAsList()
            .mapNotNull { r -> r.vascular_age?.let { DayValue(r.day, it.toDouble()) } }

    // --- Read: VO2 Max ---

    fun latestVo2Max(end: String) = db.dailyVo2MaxQueries.selectLatest(end).executeAsOneOrNull()

    fun vo2MaxTrend(
        start: String,
        end: String,
    ): List<DayValue> =
        db.dailyVo2MaxQueries
            .selectInRange(start, end)
            .executeAsList()
            .mapNotNull { r -> r.vo2_max?.let { DayValue(r.day, it) } }

    fun vo2MaxPersonalBest(): Double? =
        db.dailyVo2MaxQueries
            .selectPersonalBest()
            .executeAsOneOrNull()
            ?.personal_best
}
