package com.oura.ring.ui.screens.overview

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
fun OverviewScreen(
    viewModel: OverviewViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val start = today.minus(30, DateTimeUnit.DAY).toString()
    val end = today.toString()

    LaunchedEffect(Unit) {
        viewModel.loadData(start, end)
    }

    if (state.loading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator()
            Spacer(Modifier.height(16.dp))
            Text("Loading data...", color = OuraColors.OnSurfaceDim)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Spacer(Modifier.height(8.dp))
            Text("Overview", style = MaterialTheme.typography.headlineMedium, color = OuraColors.OnSurface)
        }

        // Stat cards
        item {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 3,
            ) {
                StatCardWithThreshold(
                    label = "Sleep Score",
                    value = state.scores.sleepScore?.toFloat(),
                    thresholds = Thresholds.Score,
                    modifier = Modifier.weight(1f),
                )
                StatCardWithThreshold(
                    label = "Readiness",
                    value = state.scores.readinessScore?.toFloat(),
                    thresholds = Thresholds.Score,
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    label = "Active Cal",
                    value = state.scores.activeCal?.toString() ?: "-",
                    color = OuraColors.Orange,
                    unit = " kcal",
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    label = "Steps",
                    value = state.scores.steps?.toString() ?: "-",
                    color = OuraColors.Orange,
                    modifier = Modifier.weight(1f),
                )
                StatCardMapped(
                    label = "Stress",
                    value = state.scores.stressSummary,
                    colorMap = StressSummaryColors,
                    modifier = Modifier.weight(1f),
                )
                StatCardMapped(
                    label = "Resilience",
                    value = state.scores.resilienceLevel,
                    colorMap = ResilienceLevelColors,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // SpO2 & Cardiovascular Age
        item {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StatCardWithThreshold(
                    label = "SpO2",
                    value = state.scores.spo2?.toFloat(),
                    thresholds = Thresholds.Spo2,
                    format = "%.1f",
                    unit = "%",
                    modifier = Modifier.weight(1f),
                )
                StatCardWithThreshold(
                    label = "Cardio Age",
                    value = state.scores.cardioAge?.toFloat(),
                    thresholds = Thresholds.CardioAge,
                    unit = " yrs",
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // Placeholder for charts - will be enhanced with Vico integration
        item {
            Text(
                "Score trends, sleep breakdown, HRV charts will render here",
                color = OuraColors.OnSurfaceDim,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 16.dp),
            )
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}
