package com.example.kotlinapp.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.http.URLBuilder
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class ApiClient(initialBaseUrl: String = "http://localhost:8000") {

    var baseUrl: String = initialBaseUrl
        set(value) {
            if (field != value) {
                field = value
                rebuildClient()
            }
        }

    var token: String? = null

    @Volatile
    var client: HttpClient = buildClient()
        private set

    private val closeScope = CoroutineScope(Dispatchers.IO)

    private fun buildClient(): HttpClient = HttpClient(CIO) {
        expectSuccess = true
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                encodeDefaults = false
                explicitNulls = false
                isLenient = true
            })
        }
        install(Logging) {
            level = LogLevel.INFO
        }
        defaultRequest {
            // Use takeFrom to safely merge base URL with relative paths
            url.takeFrom(URLBuilder(baseUrl).apply {
                // Ensure base path is preserved if present in baseUrl
            })
        }
    }

    private fun rebuildClient() {
        val oldClient = client
        client = buildClient()
        closeScope.launch {
            try {
                oldClient.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun close() {
        client.close()
    }
}

fun HttpRequestBuilder.addAuthToken(token: String?) {
    token?.let { header("Authorization", "Bearer $it") }
}