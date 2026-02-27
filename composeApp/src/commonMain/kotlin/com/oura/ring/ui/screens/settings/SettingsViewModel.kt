package com.oura.ring.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oura.ring.data.db.TokenStorage
import com.oura.ring.data.sync.SyncManager
import com.oura.ring.data.sync.SyncResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val token: String = "",
    val hasToken: Boolean = false,
    val syncing: Boolean = false,
    val syncStatus: String = "",
)

class SettingsViewModel(
    private val tokenStorage: TokenStorage,
    private val syncManager: SyncManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        val saved = tokenStorage.getToken()
        _uiState.update {
            it.copy(
                token = saved ?: "",
                hasToken = !saved.isNullOrBlank(),
            )
        }
    }

    fun updateToken(token: String) {
        _uiState.update { it.copy(token = token) }
    }

    fun saveToken() {
        val token = _uiState.value.token.trim()
        if (token.isNotBlank()) {
            tokenStorage.saveToken(token)
            _uiState.update { it.copy(hasToken = true, syncStatus = "Token saved") }
        }
    }

    fun clearToken() {
        tokenStorage.clearToken()
        _uiState.update { it.copy(token = "", hasToken = false, syncStatus = "Token cleared") }
    }

    fun triggerSync() {
        viewModelScope.launch {
            _uiState.update { it.copy(syncing = true, syncStatus = "Syncing...") }
            val result = syncManager.syncAll()
            val status = when (result) {
                is SyncResult.Success -> "Synced ${result.counts.values.sum()} records"
                is SyncResult.TokenExpired -> "Token expired — update in settings"
                is SyncResult.AlreadyRunning -> "Sync already running"
                is SyncResult.Error -> "Error: ${result.message}"
            }
            _uiState.update { it.copy(syncing = false, syncStatus = status) }
        }
    }
}
