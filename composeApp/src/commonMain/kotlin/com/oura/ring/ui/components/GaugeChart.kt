package com.oura.ring.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oura.ring.ui.theme.OuraColors
import com.oura.ring.ui.theme.Threshold
import com.oura.ring.ui.theme.getThresholdColor

@Composable
fun GaugeChart(
    value: Float?,
    minVal: Float = 0f,
    maxVal: Float = 100f,
    thresholds: List<Threshold>,
    title: String = "",
    unit: String = "",
    modifier: Modifier = Modifier,
) {
    val sweepAngle = 240f
    val startAngle = 150f

    Box(modifier = modifier.size(180.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(160.dp)) {
            val strokeWidth = 16.dp.toPx()
            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

            // Background arc
            drawArc(
                color = OuraColors.SurfaceVariant,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )

            // Value arc
            if (value != null) {
                val fraction = ((value - minVal) / (maxVal - minVal)).coerceIn(0f, 1f)
                val color = getThresholdColor(value, thresholds)
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle * fraction,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value?.let { String.format("%.0f", it) } ?: "—",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = value?.let { getThresholdColor(it, thresholds) } ?: OuraColors.OnSurfaceDim,
            )
            if (unit.isNotEmpty()) {
                Text(text = unit, style = MaterialTheme.typography.labelSmall, color = OuraColors.OnSurfaceDim)
            }
            if (title.isNotEmpty()) {
                Text(text = title, style = MaterialTheme.typography.labelSmall, color = OuraColors.OnSurfaceDim)
            }
        }
    }
}
