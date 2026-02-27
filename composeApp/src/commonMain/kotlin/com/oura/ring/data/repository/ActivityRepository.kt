package com.oura.ring.data.repository

import com.oura.ring.data.api.ApiDailyActivity
import com.oura.ring.data.api.ApiWorkout
import com.oura.ring.data.api.OuraApiClient
import com.oura.ring.db.OuraDatabase

class ActivityRepository(
    private val db: OuraDatabase,
    private val api: OuraApiClient,
) {
    suspend fun syncActivity(start: String, end: String): Int {
        val records = api.fetchAll<ApiDailyActivity>("daily_activity", start, end)
        db.transaction {
            records.forEach { r ->
                db.dailyActivityQueries.upsert(
                    day = r.day,
                    score = r.score?.toLong(),
                    active_calories = r.activeCalories?.toLong(),
                    total_calories = r.totalCalories?.toLong(),
                    steps = r.steps?.toLong(),
                    equivalent_walking_distance = r.equivalentWalkingDistance?.toLong(),
                    low_activity_time = r.lowActivityTime?.toLong(),
                    medium_activity_time = r.mediumActivityTime?.toLong(),
                    high_activity_time = r.highActivityTime?.toLong(),
                    resting_time = r.restingTime?.toLong(),
                    sedentary_time = r.sedentaryTime?.toLong(),
                    non_wear_time = r.nonWearTime?.toLong(),
                    average_met_minutes = r.averageMetMinutes,
                    high_activity_met_minutes = r.highActivityMetMinutes?.toLong(),
                    medium_activity_met_minutes = r.mediumActivityMetMinutes?.toLong(),
                    low_activity_met_minutes = r.lowActivityMetMinutes?.toLong(),
                    sedentary_met_minutes = r.sedentaryMetMinutes?.toLong(),
                    inactivity_alerts = r.inactivityAlerts?.toLong(),
                    target_calories = r.targetCalories?.toLong(),
                    target_meters = r.targetMeters?.toLong(),
                    meters_to_target = r.metersToTarget?.toLong(),
                    contributors_meet_daily_targets = r.contributors?.meetDailyTargets?.toLong(),
                    contributors_move_every_hour = r.contributors?.moveEveryHour?.toLong(),
                    contributors_recovery_time = r.contributors?.recoveryTime?.toLong(),
                    contributors_stay_active = r.contributors?.stayActive?.toLong(),
                    contributors_training_frequency = r.contributors?.trainingFrequency?.toLong(),
                    contributors_training_volume = r.contributors?.trainingVolume?.toLong(),
                )
            }
        }
        return records.size
    }

    suspend fun syncWorkouts(start: String, end: String): Int {
        val records = api.fetchAll<ApiWorkout>("workout", start, end)
        db.transaction {
            records.forEach { r ->
                val day = r.day ?: return@forEach
                db.workoutQueries.upsert(
                    id = r.id,
                    day = day,
                    activity = r.activity,
                    calories = r.calories,
                    distance = r.distance,
                    start_datetime = r.startDatetime,
                    end_datetime = r.endDatetime,
                    intensity = r.intensity,
                    label = r.label,
                    source = r.source,
                )
            }
        }
        return records.size
    }

    fun latest(end: String) =
        db.dailyActivityQueries.selectLatest(end).executeAsOneOrNull()

    fun trend(start: String, end: String) =
        db.dailyActivityQueries.selectInRange(start, end).executeAsList()

    fun stepsTrend(start: String, end: String): List<DayValue> =
        db.dailyActivityQueries.selectInRange(start, end).executeAsList()
            .mapNotNull { r -> r.steps?.let { DayValue(r.day, it.toDouble()) } }

    fun workouts(start: String, end: String) =
        db.workoutQueries.selectInRange(start, end).executeAsList()
}
