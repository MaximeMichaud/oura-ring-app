package com.oura.ring

import androidx.compose.ui.window.ComposeUIViewController
import com.oura.ring.data.db.DriverFactory
import com.oura.ring.data.db.TokenStorage
import com.oura.ring.di.initKoin
import org.koin.dsl.module

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin {
            modules(module {
                single { DriverFactory() }
                single { TokenStorage() }
            })
        }
    }
) {
    OuraApp()
}
