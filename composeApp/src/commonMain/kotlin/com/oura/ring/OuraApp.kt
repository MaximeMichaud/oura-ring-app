package com.oura.ring

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.oura.ring.ui.navigation.AppNavigation
import com.oura.ring.ui.theme.OuraTheme

@Composable
fun OuraApp() {
    OuraTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            AppNavigation()
        }
    }
}
