package com.oura.ring.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oura.ring.ui.theme.OuraColors
import com.oura.ring.ui.theme.Threshold
import com.oura.ring.ui.theme.getThresholdColor

@Composable
fun StatCard(
    label: String,
    value: String,
    color: Color = OuraColors.OnSurface,
    unit: String = "",
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.widthIn(min = 100.dp).padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = OuraColors.Surface),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = OuraColors.OnSurfaceDim,
            )
            Text(
                text = "$value$unit",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = color,
            )
        }
    }
}

@Composable
fun StatCardWithThreshold(
    label: String,
    value: Float?,
    thresholds: List<Threshold>,
    format: String = "%.0f",
    unit: String = "",
    modifier: Modifier = Modifier,
) {
    val display = value?.let { String.format(format, it) } ?: "-"
    val color = value?.let { getThresholdColor(it, thresholds) } ?: OuraColors.OnSurfaceDim
    StatCard(label = label, value = display, color = color, unit = unit, modifier = modifier)
}

@Composable
fun StatCardMapped(
    label: String,
    value: String?,
    colorMap: Map<String, Color>,
    modifier: Modifier = Modifier,
) {
    val display = value?.replaceFirstChar { it.titlecase() } ?: "-"
    val color = value?.let { colorMap[it] } ?: OuraColors.OnSurfaceDim
    StatCard(label = label, value = display, color = color, modifier = modifier)
}
