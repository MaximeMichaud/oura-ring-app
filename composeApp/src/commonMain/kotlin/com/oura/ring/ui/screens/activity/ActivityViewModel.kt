package com.oura.ring.ui.screens.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oura.ring.data.repository.ActivityRepository
import com.oura.ring.data.repository.DayValue
import com.oura.ring.db.Workout
import com.oura.ring.ui.components.BarItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ActivityUiState(
    val loading: Boolean = true,
    val score: Int? = null,
    val activeCal: Int? = null,
    val totalCal: Int? = null,
    val steps: Int? = null,
    val distanceKm: Double? = null,
    val contributors: List<BarItem> = emptyList(),
    val stepsTrend: List<DayValue> = emptyList(),
    val workouts: List<Workout> = emptyList(),
)

class ActivityViewModel(
    private val repo: ActivityRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ActivityUiState())
    val uiState: StateFlow<ActivityUiState> = _uiState.asStateFlow()

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
                            it.contributors_meet_daily_targets?.let { v -> BarItem("Daily Targets", v.toFloat()) },
                            it.contributors_move_every_hour?.let { v -> BarItem("Move Hourly", v.toFloat()) },
                            it.contributors_recovery_time?.let { v -> BarItem("Recovery Time", v.toFloat()) },
                            it.contributors_stay_active?.let { v -> BarItem("Stay Active", v.toFloat()) },
                            it.contributors_training_frequency?.let { v -> BarItem("Training Freq", v.toFloat()) },
                            it.contributors_training_volume?.let { v -> BarItem("Training Volume", v.toFloat()) },
                        )
                    } ?: emptyList()

                _uiState.update {
                    it.copy(
                        loading = false,
                        score = latest?.score?.toInt(),
                        activeCal = latest?.active_calories?.toInt(),
                        totalCal = latest?.total_calories?.toInt(),
                        steps = latest?.steps?.toInt(),
                        distanceKm = latest?.equivalent_walking_distance?.let { d -> d / 1000.0 },
                        contributors = contributors,
                        stepsTrend = repo.stepsTrend(start, end),
                        workouts = repo.workouts(start, end),
                    )
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(loading = false) }
            }
        }
    }
}
