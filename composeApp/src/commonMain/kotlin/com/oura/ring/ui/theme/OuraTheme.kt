package com.oura.ring.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = OuraColors.Blue,
    secondary = OuraColors.Green,
    tertiary = OuraColors.Purple,
    background = OuraColors.Background,
    surface = OuraColors.Surface,
    surfaceVariant = OuraColors.SurfaceVariant,
    onBackground = OuraColors.OnSurface,
    onSurface = OuraColors.OnSurface,
    onSurfaceVariant = OuraColors.OnSurfaceDim,
)

@Composable
fun OuraTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        content = content,
    )
}
