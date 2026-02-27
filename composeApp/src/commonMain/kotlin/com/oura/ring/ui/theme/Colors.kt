package com.oura.ring.ui.theme

import androidx.compose.ui.graphics.Color

object OuraColors {
    val Blue = Color(0xFF1F77B4)
    val Green = Color(0xFF2CA02C)
    val GreenLight = Color(0xFF73BF69)
    val Orange = Color(0xFFFF7F0E)
    val OrangeLight = Color(0xFFFFBB78)
    val Red = Color(0xFFD62728)
    val RedLight = Color(0xFFF2495C)
    val Yellow = Color(0xFFFF9830)
    val Purple = Color(0xFF9467BD)
    val Cyan = Color(0xFF17BECF)
    val LightBlue = Color(0xFF7EB2DD)
    val Pink = Color(0xFFE377C2)
    val DarkGreen = Color(0xFF145A32)

    val Background = Color(0xFF111217)
    val Surface = Color(0xFF1A1B23)
    val SurfaceVariant = Color(0xFF252630)
    val OnSurface = Color(0xFFE0E0E0)
    val OnSurfaceDim = Color(0xFF9E9E9E)

    val SleepDeep = Blue
    val SleepLight = LightBlue
    val SleepRem = Purple
    val SleepAwake = Red
}

data class Threshold(val cutoff: Float, val color: Color)

object Thresholds {
    val Score = listOf(
        Threshold(0f, OuraColors.RedLight),
        Threshold(60f, OuraColors.Yellow),
        Threshold(80f, OuraColors.GreenLight),
    )

    val Spo2 = listOf(
        Threshold(0f, OuraColors.RedLight),
        Threshold(92f, OuraColors.Yellow),
        Threshold(95f, OuraColors.GreenLight),
    )

    val Efficiency = listOf(
        Threshold(0f, OuraColors.RedLight),
        Threshold(75f, OuraColors.Yellow),
        Threshold(90f, OuraColors.GreenLight),
    )

    val CardioAge = listOf(
        Threshold(0f, OuraColors.GreenLight),
        Threshold(40f, OuraColors.Yellow),
        Threshold(55f, OuraColors.RedLight),
    )

    val Vo2Max = listOf(
        Threshold(0f, OuraColors.RedLight),
        Threshold(35f, OuraColors.Yellow),
        Threshold(45f, OuraColors.GreenLight),
    )

    val Bdi = listOf(
        Threshold(0f, OuraColors.GreenLight),
        Threshold(5f, OuraColors.Yellow),
        Threshold(15f, OuraColors.RedLight),
    )
}

fun getThresholdColor(value: Float, thresholds: List<Threshold>): Color {
    var color = thresholds.firstOrNull()?.color ?: OuraColors.OnSurface
    for (t in thresholds) {
        if (value >= t.cutoff) color = t.color
    }
    return color
}

val StressSummaryColors = mapOf(
    "restored" to OuraColors.Green,
    "normal" to OuraColors.Yellow,
    "stressful" to OuraColors.RedLight,
)

val ResilienceLevelColors = mapOf(
    "limited" to OuraColors.RedLight,
    "adequate" to OuraColors.Yellow,
    "solid" to OuraColors.OrangeLight,
    "strong" to OuraColors.Green,
    "exceptional" to OuraColors.Blue,
)
