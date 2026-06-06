package com.example.kotlinapp

import com.example.kotlinapp.data.remote.ApiClient
import com.example.kotlinapp.data.remote.ApiException
import com.example.kotlinapp.data.remote.NetworkException
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.nulls.shouldBeNull

class ApiClientTest : FunSpec({

    test("ApiClient initializes with default base URL") {
        val client = ApiClient("http://localhost:8000")
        client.baseUrl shouldBe "http://localhost:8000"
    }

    test("ApiClient token is initially null") {
        val client = ApiClient()
        client.token.shouldBeNull()
    }

    test("ApiClient token can be set and retrieved") {
        val client = ApiClient()
        client.token = "test-token-123"
        client.token shouldBe "test-token-123"
    }

    test("ApiClient rebuilds client when baseUrl changes") {
        val client = ApiClient("http://localhost:8000")
        val oldClient = client.client
        client.baseUrl = "http://localhost:9000"
        client.baseUrl shouldBe "http://localhost:9000"
        client.client shouldNotBe oldClient
    }

    test("ApiClient does not rebuild when same baseUrl is set") {
        val client = ApiClient("http://localhost:8000")
        val oldClient = client.client
        client.baseUrl = "http://localhost:8000"
        client.client shouldBe oldClient
    }

    test("ApiException stores code and message") {
        val exception = ApiException(404, "Not found")
        exception.code shouldBe 404
        exception.message shouldBe "Not found"
    }

    test("NetworkException stores message and cause") {
        val cause = RuntimeException("root cause")
        val exception = NetworkException("Connection failed", cause)
        exception.message shouldBe "Connection failed"
        exception.cause shouldBe cause
    }
})