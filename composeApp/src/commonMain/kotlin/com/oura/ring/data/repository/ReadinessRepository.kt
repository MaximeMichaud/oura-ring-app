package com.oura.ring.data.repository

import com.oura.ring.data.api.ApiDailyReadiness
import com.oura.ring.data.api.OuraApiClient
import com.oura.ring.db.OuraDatabase

class ReadinessRepository(
    private val db: OuraDatabase,
    private val api: OuraApiClient,
) {
    suspend fun syncFromApi(start: String, end: String): Int {
        val records = api.fetchAll<ApiDailyReadiness>("daily_readiness", start, end)
        db.transaction {
            records.forEach { r ->
                db.dailyReadinessQueries.upsert(
                    day = r.day,
                    score = r.score?.toLong(),
                    temperature_deviation = r.temperatureDeviation,
                    temperature_trend_deviation = r.temperatureTrendDeviation,
                    contributors_activity_balance = r.contributors?.activityBalance?.toLong(),
                    contributors_body_temperature = r.contributors?.bodyTemperature?.toLong(),
                    contributors_hrv_balance = r.contributors?.hrvBalance?.toLong(),
                    contributors_previous_day_activity = r.contributors?.previousDayActivity?.toLong(),
                    contributors_previous_night = r.contributors?.previousNight?.toLong(),
                    contributors_recovery_index = r.contributors?.recoveryIndex?.toLong(),
                    contributors_resting_heart_rate = r.contributors?.restingHeartRate?.toLong(),
                    contributors_sleep_balance = r.contributors?.sleepBalance?.toLong(),
                    contributors_sleep_regularity = r.contributors?.sleepRegularity?.toLong(),
                )
            }
        }
        return records.size
    }

    fun latest(end: String) =
        db.dailyReadinessQueries.selectLatest(end).executeAsOneOrNull()

    fun trend(start: String, end: String): List<DayValue> =
        db.dailyReadinessQueries.selectInRange(start, end).executeAsList()
            .mapNotNull { r -> r.score?.let { DayValue(r.day, it.toDouble()) } }

    fun temperatureTrend(start: String, end: String): List<DayValue> =
        db.dailyReadinessQueries.selectInRange(start, end).executeAsList()
            .mapNotNull { r -> r.temperature_deviation?.let { DayValue(r.day, it) } }

    fun inRange(start: String, end: String) =
        db.dailyReadinessQueries.selectInRange(start, end).executeAsList()
}
