package com.example.kotlinapp.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

class ApiClient(initialBaseUrl: String = "http://localhost:8000") {

    var baseUrl: String = initialBaseUrl
        set(value) {
            field = value
            rebuildClient()
        }

    var token: String? = null

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
            url(baseUrl)
        }
    }

    private fun rebuildClient() {
        val old = client
        client = buildClient()
        closeScope.launch {
            try { old.close() } catch (_: Exception) {}
        }
    }
}

fun HttpRequestBuilder.addAuthToken(token: String?) {
    token?.let { header("Authorization", "Bearer $it") }
}