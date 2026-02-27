package com.oura.ring.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.oura.ring.ui.theme.OuraColors
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(Modifier.height(8.dp))
        Text("Settings", style = MaterialTheme.typography.headlineMedium, color = OuraColors.OnSurface)

        // Token input
        Text("Oura Personal Access Token", style = MaterialTheme.typography.titleSmall, color = OuraColors.OnSurface)
        Text(
            "Get your token at cloud.ouraring.com/personal-access-tokens",
            style = MaterialTheme.typography.bodySmall,
            color = OuraColors.OnSurfaceDim,
        )

        OutlinedTextField(
            value = state.token,
            onValueChange = { viewModel.updateToken(it) },
            label = { Text("Token") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Row {
            Button(onClick = { viewModel.saveToken() }) {
                Text("Save Token")
            }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(onClick = { viewModel.clearToken() }) {
                Text("Clear")
            }
        }

        // Sync
        Spacer(Modifier.height(16.dp))
        Text("Data Sync", style = MaterialTheme.typography.titleSmall, color = OuraColors.OnSurface)

        Button(
            onClick = { viewModel.triggerSync() },
            enabled = state.hasToken && !state.syncing,
        ) {
            if (state.syncing) {
                CircularProgressIndicator(modifier = Modifier.height(18.dp).width(18.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(if (state.syncing) "Syncing..." else "Sync Now")
        }

        if (state.syncStatus.isNotBlank()) {
            Text(state.syncStatus, style = MaterialTheme.typography.bodyMedium, color = OuraColors.OnSurfaceDim)
        }
    }
}
