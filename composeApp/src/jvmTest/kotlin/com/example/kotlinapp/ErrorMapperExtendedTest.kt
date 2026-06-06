package com.example.kotlinapp

import com.example.kotlinapp.data.remote.ApiException
import com.example.kotlinapp.data.remote.NetworkException
import com.example.kotlinapp.util.mapException
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class ErrorMapperExtendedTest : FunSpec({

    context("API exceptions") {
        test("400 returns parsed detail with hint") {
            val exception = ApiException(400, """{"detail": "Invalid data"}""")
            val result = mapException(exception)
            result shouldContain "Invalid data"
            result shouldContain "Проверьте"
        }

        test("400 without detail returns default message") {
            val exception = ApiException(400, "Bad Request")
            val result = mapException(exception)
            result shouldContain "Некорректные данные"
        }

        test("401 returns unauthorized message") {
            val exception = ApiException(401, """{"detail": "Unauthorized"}""")
            val result = mapException(exception)
            result shouldContain "Неверный логин"
        }

        test("403 with not found detail returns not found hint") {
            val exception = ApiException(403, """{"detail": "Invite code not found"}""")
            val result = mapException(exception)
            result shouldContain "not found"
        }

        test("409 returns conflict message") {
            val exception = ApiException(409, "Conflict")
            val result = mapException(exception)
            result shouldContain "уже существует"
        }

        test("503 returns server error message") {
            val exception = ApiException(503, "Service unavailable")
            val result = mapException(exception)
            result shouldContain "сервера"
        }
    }

    context("Network exceptions") {
        test("NetworkException returns connection error message") {
            val exception = NetworkException("Connection refused", null)
            val result = mapException(exception)
            result shouldContain "подключения"
        }

        test("Generic exception with connection refused returns server unavailable") {
            val exception = RuntimeException("Connection refused")
            val result = mapException(exception)
            result shouldContain "недоступен"
        }

        test("Generic exception with timeout returns timeout message") {
            val exception = RuntimeException("Connection timed out")
            val result = mapException(exception)
            result shouldContain "время ожидания"
        }

        test("Generic exception with unknown host returns resolve error") {
            val exception = RuntimeException("Unknown host exception")
            val result = mapException(exception)
            result shouldContain "подключиться"
        }

        test("Generic network error returns message") {
            val exception = RuntimeException("Some specific error")
            val result = mapException(exception)
            result shouldBe "Some specific error"
        }
    }

    context("Detail parsing") {
        test("parses JSON detail field from 404 error") {
            val exception = ApiException(404, """{"detail": "User not found"}""")
            val result = mapException(exception)
            result shouldContain "User not found"
            result shouldContain "Проверьте"
        }

        test("handles error body without detail field for 500") {
            val exception = ApiException(500, "Internal Server Error")
            val result = mapException(exception)
            result shouldContain "сервера"
        }
    }
})