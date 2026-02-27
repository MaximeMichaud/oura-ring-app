package com.oura.ring.ui.screens.sleep

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oura.ring.ui.components.PieDonutChart
import com.oura.ring.ui.components.PieSlice
import com.oura.ring.ui.components.StatCard
import com.oura.ring.ui.components.StatCardWithThreshold
import com.oura.ring.ui.theme.OuraColors
import com.oura.ring.ui.theme.Thresholds
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SleepScreen(viewModel: SleepViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val today =
        Clock.System
            .now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
    val start = today.minus(90, DateTimeUnit.DAY).toString()
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
            Text("Sleep", style = MaterialTheme.typography.headlineMedium, color = OuraColors.OnSurface)
        }

        // Night selector
        item {
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                TextField(
                    value = state.selectedNight ?: "No data",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    state.nights.forEach { night ->
                        DropdownMenuItem(
                            text = { Text(night) },
                            onClick = {
                                viewModel.selectNight(night)
                                expanded = false
                            },
                        )
                    }
                }
            }
        }

        // Stat cards
        item {
            val s = state.session
            FlowRow(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 3,
            ) {
                StatCard(
                    label = "Total Sleep",
                    value = s?.total_sleep?.let { String.format("%.1f", it / 3600.0) } ?: "-",
                    color = OuraColors.Blue,
                    unit = " h",
                    modifier = Modifier.weight(1f),
                )
                StatCardWithThreshold(
                    label = "Efficiency",
                    value = s?.efficiency?.toFloat(),
                    thresholds = Thresholds.Efficiency,
                    unit = "%",
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    label = "Avg HRV",
                    value = s?.average_hrv?.let { String.format("%.0f", it) } ?: "-",
                    color = OuraColors.Purple,
                    unit = " ms",
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    label = "Lowest HR",
                    value = s?.lowest_heart_rate?.toString() ?: "-",
                    color = OuraColors.Red,
                    unit = " bpm",
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    label = "Latency",
                    value = s?.latency?.let { String.format("%.0f", it / 60.0) } ?: "-",
                    color = OuraColors.LightBlue,
                    unit = " min",
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    label = "Avg Breathing",
                    value = s?.average_breath?.let { String.format("%.1f", it) } ?: "-",
                    color = OuraColors.Cyan,
                    unit = " br/min",
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // Phase donut
        item {
            state.phaseBreakdown?.let { pb ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    PieDonutChart(
                        slices =
                            listOf(
                                PieSlice("Deep", pb.deep.toFloat(), OuraColors.SleepDeep),
                                PieSlice("Light", pb.light.toFloat(), OuraColors.SleepLight),
                                PieSlice("REM", pb.rem.toFloat(), OuraColors.SleepRem),
                                PieSlice("Awake", pb.awake.toFloat(), OuraColors.SleepAwake),
                            ),
                    )
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}
