package com.oura.ring.data.api

data class EndpointConfig(
    val name: String,
    val apiPath: String,
    val primaryKey: String,
)

val ALL_ENDPOINTS = listOf(
    EndpointConfig("sleep", "sleep", "id"),
    EndpointConfig("daily_sleep", "daily_sleep", "day"),
    EndpointConfig("daily_readiness", "daily_readiness", "day"),
    EndpointConfig("daily_activity", "daily_activity", "day"),
    EndpointConfig("daily_spo2", "daily_spo2", "day"),
    EndpointConfig("daily_stress", "daily_stress", "day"),
    EndpointConfig("daily_resilience", "daily_resilience", "day"),
    EndpointConfig("daily_cardiovascular_age", "daily_cardiovascular_age", "day"),
    EndpointConfig("daily_vo2_max", "vO2_max", "day"),
    EndpointConfig("workout", "workout", "id"),
    EndpointConfig("sleep_time", "sleep_time", "id"),
)
