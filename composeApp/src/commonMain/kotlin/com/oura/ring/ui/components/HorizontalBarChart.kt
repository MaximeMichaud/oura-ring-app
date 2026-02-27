package com.oura.ring.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oura.ring.ui.theme.OuraColors
import com.oura.ring.ui.theme.Threshold
import com.oura.ring.ui.theme.getThresholdColor

data class BarItem(
    val label: String,
    val value: Float,
)

@Composable
fun HorizontalBarChart(
    items: List<BarItem>,
    thresholds: List<Threshold>,
    maxValue: Float = 100f,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth().padding(8.dp)) {
        items.forEach { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
            ) {
                Text(
                    text = item.label,
                    fontSize = 11.sp,
                    color = OuraColors.OnSurfaceDim,
                    modifier = Modifier.width(100.dp),
                )
                Spacer(Modifier.width(8.dp))
                Canvas(modifier = Modifier.weight(1f).height(16.dp)) {
                    val fraction = (item.value / maxValue).coerceIn(0f, 1f)
                    // Background
                    drawRoundRect(
                        color = OuraColors.SurfaceVariant,
                        size = Size(size.width, size.height),
                        cornerRadius = CornerRadius(4.dp.toPx()),
                    )
                    // Value
                    drawRoundRect(
                        color = getThresholdColor(item.value, thresholds),
                        size = Size(size.width * fraction, size.height),
                        cornerRadius = CornerRadius(4.dp.toPx()),
                    )
                }
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "${item.value.toInt()}",
                    fontSize = 11.sp,
                    color = OuraColors.OnSurface,
                    modifier = Modifier.width(28.dp),
                )
            }
        }
    }
}
