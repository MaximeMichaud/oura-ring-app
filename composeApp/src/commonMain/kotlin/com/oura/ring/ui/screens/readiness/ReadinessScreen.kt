package com.oura.ring.ui.screens.readiness

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
import com.oura.ring.ui.components.HorizontalBarChart
import com.oura.ring.ui.components.StatCard
import com.oura.ring.ui.theme.OuraColors
import com.oura.ring.ui.theme.Thresholds
import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReadinessScreen(viewModel: ReadinessViewModel = koinViewModel()) {
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
            Text("Readiness", style = MaterialTheme.typography.headlineMedium, color = OuraColors.OnSurface)
        }

        item {
            FlowRow(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                GaugeChart(
                    value = state.score?.toFloat(),
                    thresholds = Thresholds.Score,
                    title = "Readiness",
                )
                StatCard(
                    label = "Temp Deviation",
                    value = state.tempDeviation?.let { String.format("%.2f", it) } ?: "-",
                    color = OuraColors.Orange,
                    unit = " °C",
                )
            }
        }

        item {
            Text("Contributors", style = MaterialTheme.typography.titleMedium, color = OuraColors.OnSurface)
            HorizontalBarChart(items = state.contributors, thresholds = Thresholds.Score)
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}
