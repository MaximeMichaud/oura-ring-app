package com.oura.ring.ui.screens.readiness

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oura.ring.data.repository.DayValue
import com.oura.ring.data.repository.ReadinessRepository
import com.oura.ring.ui.components.BarItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReadinessUiState(
    val loading: Boolean = true,
    val score: Int? = null,
    val tempDeviation: Double? = null,
    val contributors: List<BarItem> = emptyList(),
    val scoreTrend: List<DayValue> = emptyList(),
    val tempTrend: List<DayValue> = emptyList(),
)

class ReadinessViewModel(
    private val repo: ReadinessRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReadinessUiState())
    val uiState: StateFlow<ReadinessUiState> = _uiState.asStateFlow()

    fun loadData(
        start: String,
        end: String,
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            _uiState.update { it.copy(loading = true) }
            try {
                val latest = repo.latest(end)
                val contributors =
                    latest?.let {
                        listOfNotNull(
                            it.contributors_activity_balance?.let { v -> BarItem("Activity Balance", v.toFloat()) },
                            it.contributors_body_temperature?.let { v -> BarItem("Body Temp", v.toFloat()) },
                            it.contributors_hrv_balance?.let { v -> BarItem("HRV Balance", v.toFloat()) },
                            it.contributors_previous_day_activity?.let { v ->
                                BarItem("Prev Day Activity", v.toFloat())
                            },
                            it.contributors_previous_night?.let { v -> BarItem("Previous Night", v.toFloat()) },
                            it.contributors_recovery_index?.let { v -> BarItem("Recovery Index", v.toFloat()) },
                            it.contributors_resting_heart_rate?.let { v -> BarItem("Resting HR", v.toFloat()) },
                            it.contributors_sleep_balance?.let { v -> BarItem("Sleep Balance", v.toFloat()) },
                            it.contributors_sleep_regularity?.let { v -> BarItem("Sleep Regularity", v.toFloat()) },
                        )
                    } ?: emptyList()

                _uiState.update {
                    it.copy(
                        loading = false,
                        score = latest?.score?.toInt(),
                        tempDeviation = latest?.temperature_deviation,
                        contributors = contributors,
                        scoreTrend = repo.trend(start, end),
                        tempTrend = repo.temperatureTrend(start, end),
                    )
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(loading = false) }
            }
        }
    }
}
