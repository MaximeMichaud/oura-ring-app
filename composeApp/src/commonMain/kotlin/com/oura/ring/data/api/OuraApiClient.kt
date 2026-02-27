package com.oura.ring.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.delay
import kotlin.math.min
import kotlin.math.pow

class TokenExpiredException : Exception("Oura API token expired or invalid")

class ApiException(
    message: String,
) : Exception(message)

class OuraApiClient(
    @PublishedApi internal val httpClient: HttpClient,
) {
    companion object {
        @PublishedApi internal const val BASE_URL = "https://api.ouraring.com/v2/usercollection"

        @PublishedApi internal const val MAX_RETRIES = 5

        @PublishedApi internal const val MAX_RETRY_AFTER_SEC = 300

        @PublishedApi internal const val MAX_BACKOFF_MS = 120_000L
    }

    suspend inline fun <reified T> fetchAll(
        endpoint: String,
        startDate: String,
        endDate: String,
    ): List<T> {
        val results = mutableListOf<T>()
        var nextToken: String? = null

        do {
            val page = fetchPageWithRetry<T>(endpoint, startDate, endDate, nextToken)
            results.addAll(page.data)
            nextToken = page.nextToken
        } while (nextToken != null)

        return results
    }

    suspend inline fun <reified T> fetchPageWithRetry(
        endpoint: String,
        startDate: String,
        endDate: String,
        nextToken: String?,
    ): OuraPageResponse<T> {
        var attempt = 0

        while (true) {
            try {
                val response: HttpResponse =
                    httpClient.get("$BASE_URL/$endpoint") {
                        if (nextToken != null) {
                            parameter("next_token", nextToken)
                        } else {
                            parameter("start_date", startDate)
                            parameter("end_date", endDate)
                        }
                    }

                when (response.status.value) {
                    200 -> {
                        return response.body()
                    }

                    401 -> {
                        throw TokenExpiredException()
                    }

                    404 -> {
                        return OuraPageResponse(emptyList())
                    }

                    429 -> {
                        val retryAfter =
                            response.headers["Retry-After"]
                                ?.toIntOrNull()
                                ?.coerceAtMost(MAX_RETRY_AFTER_SEC)
                                ?: 60
                        delay(retryAfter * 1000L)
                    }

                    in 500..599 -> {
                        if (++attempt >= MAX_RETRIES) {
                            throw ApiException("Server error ${response.status} after $MAX_RETRIES retries")
                        }
                        delay(min(2.0.pow(attempt).toLong() * 2000, MAX_BACKOFF_MS))
                    }

                    else -> {
                        throw ApiException("Unexpected status: ${response.status}")
                    }
                }
            } catch (e: TokenExpiredException) {
                throw e
            } catch (e: ApiException) {
                throw e
            } catch (e: Exception) {
                if (++attempt >= MAX_RETRIES) throw e
                delay(min(2.0.pow(attempt).toLong() * 2000, MAX_BACKOFF_MS))
            }
        }
    }
}
