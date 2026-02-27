package com.oura.ring.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oura.ring.ui.theme.OuraColors

data class PieSlice(
    val label: String,
    val value: Float,
    val color: Color,
)

@Composable
fun PieDonutChart(
    slices: List<PieSlice>,
    holeRatio: Float = 0.4f,
    modifier: Modifier = Modifier,
) {
    val total = slices.sumOf { it.value.toDouble() }.toFloat()
    if (total <= 0f) return

    Column(modifier = modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(modifier = Modifier.size(160.dp)) {
            val strokeWidth = size.minDimension * (1f - holeRatio) / 2f
            val arcRadius = (size.minDimension - strokeWidth) / 2f
            val arcSize = Size(arcRadius * 2, arcRadius * 2)
            val topLeft =
                Offset(
                    (size.width - arcSize.width) / 2,
                    (size.height - arcSize.height) / 2,
                )

            var currentAngle = -90f
            slices.forEach { slice ->
                val sweep = (slice.value / total) * 360f
                drawArc(
                    color = slice.color,
                    startAngle = currentAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth),
                )
                currentAngle += sweep
            }
        }

        Spacer(Modifier.height(8.dp))

        // Legend
        slices.forEach { slice ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 2.dp),
            ) {
                Canvas(Modifier.size(10.dp)) {
                    drawCircle(color = slice.color)
                }
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "${slice.label}: ${String.format("%.0f", slice.value)} min",
                    fontSize = 11.sp,
                    color = OuraColors.OnSurfaceDim,
                )
            }
        }
    }
}
