package com.oura.ring.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.oura.ring.db.OuraDatabase
import java.io.File

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        val dbDir = File(System.getProperty("user.home"), ".oura-ring")
        dbDir.mkdirs()
        val dbFile = File(dbDir, "oura.db")
        val dbExists = dbFile.exists()
        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")
        if (!dbExists) {
            OuraDatabase.Schema.create(driver)
        }
        return driver
    }
}
