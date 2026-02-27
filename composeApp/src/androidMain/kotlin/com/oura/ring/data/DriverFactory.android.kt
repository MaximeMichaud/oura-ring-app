package com.oura.ring.data.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.oura.ring.db.OuraDatabase

actual class DriverFactory(
    private val context: Context,
) {
    actual fun createDriver(): SqlDriver = AndroidSqliteDriver(OuraDatabase.Schema, context, "oura.db")
}
