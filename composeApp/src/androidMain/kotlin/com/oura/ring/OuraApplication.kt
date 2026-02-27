package com.oura.ring

import android.app.Application
import com.oura.ring.data.db.DriverFactory
import com.oura.ring.data.db.TokenStorage
import com.oura.ring.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

class OuraApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@OuraApplication)
            modules(module {
                single { DriverFactory(this@OuraApplication) }
                single { TokenStorage(this@OuraApplication) }
            })
        }
    }
}
