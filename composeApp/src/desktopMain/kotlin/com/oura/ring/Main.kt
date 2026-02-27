package com.oura.ring

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.oura.ring.data.db.DriverFactory
import com.oura.ring.data.db.TokenStorage
import com.oura.ring.di.initKoin
import org.koin.dsl.module

fun main() {
    initKoin {
        modules(
            module {
                single { DriverFactory() }
                single { TokenStorage() }
            },
        )
    }
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Oura Ring",
            state = rememberWindowState(width = 420.dp, height = 800.dp),
        ) {
            OuraApp()
        }
    }
}
