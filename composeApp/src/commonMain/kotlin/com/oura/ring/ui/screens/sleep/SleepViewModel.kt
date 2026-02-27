package com.oura.ring.ui.screens.sleep

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oura.ring.data.repository.DayValue
import com.oura.ring.data.repository.SleepPhaseBreakdown
import com.oura.ring.data.repository.SleepRepository
import com.oura.ring.db.Sleep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SleepUiState(
    val loading: Boolean = true,
    val nights: List<String> = emptyList(),
    val selectedNight: String? = null,
    val session: Sleep? = null,
    val phaseBreakdown: SleepPhaseBreakdown? = null,
    val hrvTrend: List<DayValue> = emptyList(),
    val restingHrTrend: List<DayValue> = emptyList(),
    val efficiencyTrend: List<DayValue> = emptyList(),
    val latencyTrend: List<DayValue> = emptyList(),
    val breathingTrend: List<DayValue> = emptyList(),
)

class SleepViewModel(private val sleepRepo: SleepRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SleepUiState())
    val uiState: StateFlow<SleepUiState> = _uiState.asStateFlow()

    fun loadData(start: String, end: String) {
        viewModelScope.launch(Dispatchers.Default) {
            _uiState.update { it.copy(loading = true) }
            try {
                val nights = sleepRepo.availableNights(start, end)
                val selected = nights.firstOrNull()

                _uiState.update {
                    it.copy(
                        loading = false,
                        nights = nights,
                        selectedNight = selected,
                        session = selected?.let { n -> sleepRepo.sleepSession(n) },
                        phaseBreakdown = selected?.let { n -> sleepRepo.sleepPhaseBreakdown(n) },
                        hrvTrend = sleepRepo.hrvTrend(start, end),
                        restingHrTrend = sleepRepo.restingHrTrend(start, end),
                        efficiencyTrend = sleepRepo.efficiencyTrend(start, end),
                        latencyTrend = sleepRepo.latencyTrend(start, end),
                        breathingTrend = sleepRepo.breathingTrend(start, end),
                    )
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(loading = false) }
            }
        }
    }

    fun selectNight(night: String) {
        viewModelScope.launch(Dispatchers.Default) {
            _uiState.update {
                it.copy(
                    selectedNight = night,
                    session = sleepRepo.sleepSession(night),
                    phaseBreakdown = sleepRepo.sleepPhaseBreakdown(night),
                )
            }
        }
    }
}
