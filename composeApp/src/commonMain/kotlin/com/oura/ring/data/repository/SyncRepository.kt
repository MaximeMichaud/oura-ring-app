package com.oura.ring.data.repository

import com.oura.ring.db.OuraDatabase
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class SyncRepository(private val db: OuraDatabase) {

    private fun now(): String = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .toString()

    fun getLastSyncDate(endpoint: String): String? {
        return db.syncLogQueries.getLastSyncDate(endpoint)
            .executeAsOneOrNull()
            ?.last_sync_date
    }

    fun recordSuccess(endpoint: String, syncDate: String, count: Int) {
        val ts = now()
        db.syncLogQueries.upsertSuccess(
            endpoint = endpoint,
            last_sync_date = syncDate,
            record_count = count.toLong(),
            updated_at = ts,
            last_success_at = ts,
        )
    }

    fun recordFailure(endpoint: String, error: String) {
        val ts = now()
        db.syncLogQueries.insertIfMissing(endpoint)
        db.syncLogQueries.recordFailure(
            last_error = error,
            updated_at = ts,
            endpoint = endpoint,
        )
    }

    fun getAll() = db.syncLogQueries.getAll().executeAsList()
}
