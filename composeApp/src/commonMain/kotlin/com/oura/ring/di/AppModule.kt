package com.oura.ring.di

import com.oura.ring.data.api.OuraApiClient
import com.oura.ring.data.db.DriverFactory
import com.oura.ring.data.db.TokenStorage
import com.oura.ring.data.repository.ActivityRepository
import com.oura.ring.data.repository.BodyRepository
import com.oura.ring.data.repository.ReadinessRepository
import com.oura.ring.data.repository.SleepRepository
import com.oura.ring.data.repository.SyncRepository
import com.oura.ring.data.sync.SyncManager
import com.oura.ring.db.OuraDatabase
import com.oura.ring.ui.screens.activity.ActivityViewModel
import com.oura.ring.ui.screens.body.BodyViewModel
import com.oura.ring.ui.screens.overview.OverviewViewModel
import com.oura.ring.ui.screens.readiness.ReadinessViewModel
import com.oura.ring.ui.screens.settings.SettingsViewModel
import com.oura.ring.ui.screens.sleep.SleepViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val jsonInstance = Json {
    ignoreUnknownKeys = true
    isLenient = true
    coerceInputValues = true
}

val dataModule = module {
    single { get<DriverFactory>().createDriver() }
    single { OuraDatabase(get()) }
    single { jsonInstance }

    single {
        val tokenStorage = get<TokenStorage>()
        HttpClient {
            install(ContentNegotiation) {
                json(jsonInstance)
            }
            defaultRequest {
                val token = tokenStorage.getToken() ?: ""
                headers.append("Authorization", "Bearer $token")
            }
        }
    }

    single { OuraApiClient(get()) }

    single { SleepRepository(get(), get(), get()) }
    single { ReadinessRepository(get(), get()) }
    single { ActivityRepository(get(), get()) }
    single { BodyRepository(get(), get()) }
    single { SyncRepository(get()) }

    single { SyncManager(get(), get(), get(), get(), get()) }
}

val viewModelModule = module {
    viewModel { OverviewViewModel(get(), get(), get(), get(), get()) }
    viewModel { SleepViewModel(get()) }
    viewModel { ReadinessViewModel(get()) }
    viewModel { ActivityViewModel(get()) }
    viewModel { BodyViewModel(get()) }
    viewModel { SettingsViewModel(get(), get()) }
}

fun initKoin(appDeclaration: KoinApplication.() -> Unit = {}) {
    startKoin {
        appDeclaration()
        modules(dataModule, viewModelModule)
    }
}
