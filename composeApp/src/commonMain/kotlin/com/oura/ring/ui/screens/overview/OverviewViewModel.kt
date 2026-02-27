package com.oura.ring.ui.screens.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oura.ring.data.repository.ActivityRepository
import com.oura.ring.data.repository.BodyRepository
import com.oura.ring.data.repository.DayValue
import com.oura.ring.data.repository.ReadinessRepository
import com.oura.ring.data.repository.SleepDurationPoint
import com.oura.ring.data.repository.SleepRepository
import com.oura.ring.data.sync.SyncManager
import com.oura.ring.data.sync.SyncResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LatestScores(
    val sleepScore: Int? = null,
    val readinessScore: Int? = null,
    val activeCal: Int? = null,
    val steps: Int? = null,
    val stressSummary: String? = null,
    val resilienceLevel: String? = null,
    val spo2: Double? = null,
    val cardioAge: Int? = null,
)

data class OverviewUiState(
    val loading: Boolean = true,
    val syncing: Boolean = false,
    val scores: LatestScores = LatestScores(),
    val sleepScoreTrend: List<DayValue> = emptyList(),
    val readinessScoreTrend: List<DayValue> = emptyList(),
    val stepsTrend: List<DayValue> = emptyList(),
    val sleepBreakdown: List<SleepDurationPoint> = emptyList(),
    val spo2Trend: List<DayValue> = emptyList(),
    val hrvTrend: List<DayValue> = emptyList(),
)

class OverviewViewModel(
    private val sleepRepo: SleepRepository,
    private val readinessRepo: ReadinessRepository,
    private val activityRepo: ActivityRepository,
    private val bodyRepo: BodyRepository,
    private val syncManager: SyncManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OverviewUiState())
    val uiState: StateFlow<OverviewUiState> = _uiState.asStateFlow()

    fun loadData(start: String, end: String) {
        viewModelScope.launch(Dispatchers.Default) {
            _uiState.update { it.copy(loading = true) }
            try {
                val scores = async { loadLatestScores(end) }
                val sleepTrend = async { sleepRepo.dailySleepInRange(start, end).mapNotNull { r -> r.score?.let { DayValue(r.day, it.toDouble()) } } }
                val readTrend = async { readinessRepo.trend(start, end) }
                val steps = async { activityRepo.stepsTrend(start, end) }
                val breakdown = async { sleepRepo.sleepDurationBreakdown(start, end) }
                val spo2 = async { bodyRepo.spo2Trend(start, end) }
                val hrv = async { sleepRepo.hrvTrend(start, end) }

                _uiState.update {
                    it.copy(
                        loading = false,
                        scores = scores.await(),
                        sleepScoreTrend = sleepTrend.await(),
                        readinessScoreTrend = readTrend.await(),
                        stepsTrend = steps.await(),
                        sleepBreakdown = breakdown.await(),
                        spo2Trend = spo2.await(),
                        hrvTrend = hrv.await(),
                    )
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(loading = false) }
            }
        }
    }

    fun sync() {
        viewModelScope.launch {
            _uiState.update { it.copy(syncing = true) }
            syncManager.syncAll()
            _uiState.update { it.copy(syncing = false) }
        }
    }

    private fun loadLatestScores(end: String): LatestScores {
        val sleep = sleepRepo.latestDailySleep(end)
        val readiness = readinessRepo.latest(end)
        val activity = activityRepo.latest(end)
        val stress = bodyRepo.latestStress(end)
        val resilience = bodyRepo.latestResilience(end)
        val spo2 = bodyRepo.latestSpo2(end)
        val cardio = bodyRepo.latestCardioAge(end)

        return LatestScores(
            sleepScore = sleep?.score?.toInt(),
            readinessScore = readiness?.score?.toInt(),
            activeCal = activity?.active_calories?.toInt(),
            steps = activity?.steps?.toInt(),
            stressSummary = stress?.day_summary,
            resilienceLevel = resilience?.level,
            spo2 = spo2?.spo2_percentage_average,
            cardioAge = cardio?.vascular_age?.toInt(),
        )
    }
}
