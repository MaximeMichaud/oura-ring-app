package com.oura.ring.ui.screens.body

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oura.ring.ui.components.GaugeChart
import com.oura.ring.ui.components.StatCard
import com.oura.ring.ui.components.StatCardMapped
import com.oura.ring.ui.components.StatCardWithThreshold
import com.oura.ring.ui.theme.OuraColors
import com.oura.ring.ui.theme.ResilienceLevelColors
import com.oura.ring.ui.theme.StressSummaryColors
import com.oura.ring.ui.theme.Thresholds
import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BodyScreen(viewModel: BodyViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val start = today.minus(30, DateTimeUnit.DAY).toString()
    val end = today.toString()

    LaunchedEffect(Unit) { viewModel.loadData(start, end) }

    if (state.loading) {
        Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Spacer(Modifier.height(8.dp))
            Text("Body", style = MaterialTheme.typography.headlineMedium, color = OuraColors.OnSurface)
        }

        // SpO2
        item {
            Text("SpO2", style = MaterialTheme.typography.titleMedium, color = OuraColors.OnSurface)
            FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCardWithThreshold("SpO2", state.spo2?.toFloat(), Thresholds.Spo2, "%.1f", "%", Modifier.weight(1f))
                StatCardWithThreshold("BDI", state.bdi?.toFloat(), Thresholds.Bdi, "%.1f", " ev/hr", Modifier.weight(1f))
            }
        }

        item { HorizontalDivider(color = OuraColors.SurfaceVariant) }

        // Stress
        item {
            Text("Stress", style = MaterialTheme.typography.titleMedium, color = OuraColors.OnSurface)
            FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCardMapped("Summary", state.stressSummary, StressSummaryColors, Modifier.weight(1f))
                StatCard("Stress", state.stressMinutes?.let { String.format("%.0f", it) } ?: "-", OuraColors.Red, " min", Modifier.weight(1f))
                StatCard("Recovery", state.recoveryMinutes?.let { String.format("%.0f", it) } ?: "-", OuraColors.Green, " min", Modifier.weight(1f))
            }
        }

        item { HorizontalDivider(color = OuraColors.SurfaceVariant) }

        // Resilience
        item {
            Text("Resilience", style = MaterialTheme.typography.titleMedium, color = OuraColors.OnSurface)
            FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCardMapped("Level", state.resilienceLevel, ResilienceLevelColors, Modifier.weight(1f))
                StatCard("Sleep Recovery", state.resilienceSleepRecovery?.let { String.format("%.0f", it) } ?: "-", OuraColors.Purple, modifier = Modifier.weight(1f))
                StatCard("Daytime Recovery", state.resilienceDaytimeRecovery?.let { String.format("%.0f", it) } ?: "-", OuraColors.Purple, modifier = Modifier.weight(1f))
            }
        }

        item { HorizontalDivider(color = OuraColors.SurfaceVariant) }

        // Cardiovascular Age
        item {
            Text("Cardiovascular Age", style = MaterialTheme.typography.titleMedium, color = OuraColors.OnSurface)
            FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                GaugeChart(
                    value = state.cardioAge?.toFloat(),
                    minVal = 15f,
                    maxVal = 80f,
                    thresholds = Thresholds.CardioAge,
                    unit = "yrs",
                )
            }
        }

        item { HorizontalDivider(color = OuraColors.SurfaceVariant) }

        // VO2 Max
        item {
            Text("VO2 Max", style = MaterialTheme.typography.titleMedium, color = OuraColors.OnSurface)
            FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCardWithThreshold("VO2 Max", state.vo2Max?.toFloat(), Thresholds.Vo2Max, "%.1f", modifier = Modifier.weight(1f))
                StatCard("Personal Best", state.vo2MaxPb?.let { String.format("%.1f", it) } ?: "-", OuraColors.Orange, modifier = Modifier.weight(1f))
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}
