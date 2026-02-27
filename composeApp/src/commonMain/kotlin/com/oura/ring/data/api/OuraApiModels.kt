package com.oura.ring.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OuraPageResponse<T>(
    val data: List<T> = emptyList(),
    @SerialName("next_token") val nextToken: String? = null,
)

// --- Sleep ---

@Serializable
data class HeartRateData(
    val items: List<Double?>? = null,
    val interval: Double? = null,
    val timestamp: String? = null,
)

@Serializable
data class HrvData(
    val items: List<Double?>? = null,
    val interval: Double? = null,
    val timestamp: String? = null,
)

@Serializable
data class ApiSleep(
    val id: String,
    val day: String? = null,
    @SerialName("bedtime_start") val bedtimeStart: String? = null,
    @SerialName("bedtime_end") val bedtimeEnd: String? = null,
    @SerialName("time_in_bed") val timeInBed: Int? = null,
    @SerialName("total_sleep_duration") val totalSleepDuration: Int? = null,
    @SerialName("awake_time") val awakeTime: Int? = null,
    @SerialName("light_sleep_duration") val lightSleepDuration: Int? = null,
    @SerialName("deep_sleep_duration") val deepSleepDuration: Int? = null,
    @SerialName("rem_sleep_duration") val remSleepDuration: Int? = null,
    @SerialName("restless_periods") val restlessPeriods: Int? = null,
    val efficiency: Int? = null,
    val latency: Int? = null,
    val type: String? = null,
    @SerialName("readiness_score_delta") val readinessScoreDelta: Int? = null,
    @SerialName("average_breath") val averageBreath: Double? = null,
    @SerialName("average_heart_rate") val averageHeartRate: Double? = null,
    @SerialName("average_hrv") val averageHrv: Double? = null,
    @SerialName("lowest_heart_rate") val lowestHeartRate: Int? = null,
    @SerialName("heart_rate") val heartRate: HeartRateData? = null,
    val hrv: HrvData? = null,
    @SerialName("sleep_phase_5_min") val sleepPhase5Min: String? = null,
    @SerialName("movement_30_sec") val movement30Sec: String? = null,
    @SerialName("sleep_score_delta") val sleepScoreDelta: Double? = null,
    val period: Int? = null,
    @SerialName("low_battery_alert") val lowBatteryAlert: Boolean? = null,
)

// --- Daily Sleep ---

@Serializable
data class SleepContributors(
    @SerialName("deep_sleep") val deepSleep: Int? = null,
    val efficiency: Int? = null,
    val latency: Int? = null,
    @SerialName("rem_sleep") val remSleep: Int? = null,
    val restfulness: Int? = null,
    val timing: Int? = null,
    @SerialName("total_sleep") val totalSleep: Int? = null,
)

@Serializable
data class ApiDailySleep(
    val day: String,
    val score: Int? = null,
    val contributors: SleepContributors? = null,
)

// --- Daily Readiness ---

@Serializable
data class ReadinessContributors(
    @SerialName("activity_balance") val activityBalance: Int? = null,
    @SerialName("body_temperature") val bodyTemperature: Int? = null,
    @SerialName("hrv_balance") val hrvBalance: Int? = null,
    @SerialName("previous_day_activity") val previousDayActivity: Int? = null,
    @SerialName("previous_night") val previousNight: Int? = null,
    @SerialName("recovery_index") val recoveryIndex: Int? = null,
    @SerialName("resting_heart_rate") val restingHeartRate: Int? = null,
    @SerialName("sleep_balance") val sleepBalance: Int? = null,
    @SerialName("sleep_regularity") val sleepRegularity: Int? = null,
)

@Serializable
data class ApiDailyReadiness(
    val day: String,
    val score: Int? = null,
    @SerialName("temperature_deviation") val temperatureDeviation: Double? = null,
    @SerialName("temperature_trend_deviation") val temperatureTrendDeviation: Double? = null,
    val contributors: ReadinessContributors? = null,
)

// --- Daily Activity ---

@Serializable
data class ActivityContributors(
    @SerialName("meet_daily_targets") val meetDailyTargets: Int? = null,
    @SerialName("move_every_hour") val moveEveryHour: Int? = null,
    @SerialName("recovery_time") val recoveryTime: Int? = null,
    @SerialName("stay_active") val stayActive: Int? = null,
    @SerialName("training_frequency") val trainingFrequency: Int? = null,
    @SerialName("training_volume") val trainingVolume: Int? = null,
)

@Serializable
data class ApiDailyActivity(
    val day: String,
    val score: Int? = null,
    @SerialName("active_calories") val activeCalories: Int? = null,
    @SerialName("total_calories") val totalCalories: Int? = null,
    val steps: Int? = null,
    @SerialName("equivalent_walking_distance") val equivalentWalkingDistance: Int? = null,
    @SerialName("low_activity_time") val lowActivityTime: Int? = null,
    @SerialName("medium_activity_time") val mediumActivityTime: Int? = null,
    @SerialName("high_activity_time") val highActivityTime: Int? = null,
    @SerialName("resting_time") val restingTime: Int? = null,
    @SerialName("sedentary_time") val sedentaryTime: Int? = null,
    @SerialName("non_wear_time") val nonWearTime: Int? = null,
    @SerialName("average_met_minutes") val averageMetMinutes: Double? = null,
    @SerialName("high_activity_met_minutes") val highActivityMetMinutes: Int? = null,
    @SerialName("medium_activity_met_minutes") val mediumActivityMetMinutes: Int? = null,
    @SerialName("low_activity_met_minutes") val lowActivityMetMinutes: Int? = null,
    @SerialName("sedentary_met_minutes") val sedentaryMetMinutes: Int? = null,
    @SerialName("inactivity_alerts") val inactivityAlerts: Int? = null,
    @SerialName("target_calories") val targetCalories: Int? = null,
    @SerialName("target_meters") val targetMeters: Int? = null,
    @SerialName("meters_to_target") val metersToTarget: Int? = null,
    val contributors: ActivityContributors? = null,
)

// --- Daily SpO2 ---

@Serializable
data class Spo2Percentage(
    val average: Double? = null,
)

@Serializable
data class ApiDailySpo2(
    val day: String,
    @SerialName("spo2_percentage") val spo2Percentage: Spo2Percentage? = null,
    @SerialName("breathing_disturbance_index") val breathingDisturbanceIndex: Double? = null,
)

// --- Daily Stress ---

@Serializable
data class ApiDailyStress(
    val day: String,
    @SerialName("stress_high") val stressHigh: Int? = null,
    @SerialName("recovery_high") val recoveryHigh: Int? = null,
    @SerialName("day_summary") val daySummary: String? = null,
)

// --- Daily Resilience ---

@Serializable
data class ResilienceContributors(
    @SerialName("sleep_recovery") val sleepRecovery: Double? = null,
    @SerialName("daytime_recovery") val daytimeRecovery: Double? = null,
    val stress: Double? = null,
)

@Serializable
data class ApiDailyResilience(
    val day: String,
    val level: String? = null,
    val contributors: ResilienceContributors? = null,
)

// --- Daily Cardiovascular Age ---

@Serializable
data class ApiDailyCardiovascularAge(
    val day: String,
    @SerialName("vascular_age") val vascularAge: Int? = null,
)

// --- Daily VO2 Max ---

@Serializable
data class ApiDailyVo2Max(
    val day: String,
    @SerialName("vo2_max") val vo2Max: Double? = null,
)

// --- Workout ---

@Serializable
data class ApiWorkout(
    val id: String,
    val day: String? = null,
    val activity: String? = null,
    val calories: Double? = null,
    val distance: Double? = null,
    @SerialName("start_datetime") val startDatetime: String? = null,
    @SerialName("end_datetime") val endDatetime: String? = null,
    val intensity: String? = null,
    val label: String? = null,
    val source: String? = null,
)

// --- Sleep Time ---

@Serializable
data class OptimalBedtime(
    @SerialName("start_offset") val startOffset: Int? = null,
    @SerialName("end_offset") val endOffset: Int? = null,
    @SerialName("day_tz") val dayTz: Int? = null,
)

@Serializable
data class ApiSleepTime(
    val id: String,
    val day: String? = null,
    @SerialName("optimal_bedtime") val optimalBedtime: OptimalBedtime? = null,
    val recommendation: String? = null,
    val status: String? = null,
)
