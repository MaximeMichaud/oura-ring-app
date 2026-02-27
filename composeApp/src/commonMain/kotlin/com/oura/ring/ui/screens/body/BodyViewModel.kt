package com.oura.ring.ui.screens.body

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oura.ring.data.repository.BodyRepository
import com.oura.ring.data.repository.DayValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BodyUiState(
    val loading: Boolean = true,
    // SpO2
    val spo2: Double? = null,
    val bdi: Double? = null,
    val spo2Trend: List<DayValue> = emptyList(),
    // Stress
    val stressSummary: String? = null,
    val stressMinutes: Double? = null,
    val recoveryMinutes: Double? = null,
    // Resilience
    val resilienceLevel: String? = null,
    val resilienceSleepRecovery: Double? = null,
    val resilienceDaytimeRecovery: Double? = null,
    val resilienceStress: Double? = null,
    // Cardio
    val cardioAge: Int? = null,
    val cardioAgeTrend: List<DayValue> = emptyList(),
    // VO2 Max
    val vo2Max: Double? = null,
    val vo2MaxPb: Double? = null,
    val vo2MaxTrend: List<DayValue> = emptyList(),
)

class BodyViewModel(private val repo: BodyRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(BodyUiState())
    val uiState: StateFlow<BodyUiState> = _uiState.asStateFlow()

    fun loadData(start: String, end: String) {
        viewModelScope.launch(Dispatchers.Default) {
            _uiState.update { it.copy(loading = true) }
            try {
            val spo2 = repo.latestSpo2(end)
            val stress = repo.latestStress(end)
            val resilience = repo.latestResilience(end)
            val cardio = repo.latestCardioAge(end)
            val vo2 = repo.latestVo2Max(end)

            _uiState.update {
                it.copy(
                    loading = false,
                    spo2 = spo2?.spo2_percentage_average,
                    bdi = spo2?.breathing_disturbance_index,
                    spo2Trend = repo.spo2Trend(start, end),
                    stressSummary = stress?.day_summary,
                    stressMinutes = stress?.stress_high?.let { s -> s / 60.0 },
                    recoveryMinutes = stress?.recovery_high?.let { r -> r / 60.0 },
                    resilienceLevel = resilience?.level,
                    resilienceSleepRecovery = resilience?.contributors_sleep_recovery,
                    resilienceDaytimeRecovery = resilience?.contributors_daytime_recovery,
                    resilienceStress = resilience?.contributors_stress,
                    cardioAge = cardio?.vascular_age?.toInt(),
                    cardioAgeTrend = repo.cardioAgeTrend(start, end),
                    vo2Max = vo2?.vo2_max,
                    vo2MaxPb = repo.vo2MaxPersonalBest(),
                    vo2MaxTrend = repo.vo2MaxTrend(start, end),
                )
            }
            } catch (_: Exception) {
                _uiState.update { it.copy(loading = false) }
            }
        }
    }
}
