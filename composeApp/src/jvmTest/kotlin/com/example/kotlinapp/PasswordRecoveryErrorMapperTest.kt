package com.example.kotlinapp

import com.example.kotlinapp.data.remote.ApiException
import com.example.kotlinapp.data.remote.NetworkException
import com.example.kotlinapp.util.mapException
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class PasswordRecoveryErrorMapperTest : FunSpec({

    test("mapException_403_withDetail_returnsInviteCodeError") {
        val exception = ApiException(403, """{"detail": "Invalid invite code"}""")
        val result = mapException(exception)
        result shouldContain "приглашения"
    }

    test("mapException_403_withoutDetail_returnsForbiddenMessage") {
        val exception = ApiException(403, "No detail provided")
        val result = mapException(exception)
        (result.contains("запрещён") || result.contains("приглашения")) shouldBe true
    }

    test("mapException_404_withDetail_returnsUserNotFoundError") {
        val exception = ApiException(404, """{"detail": "User not found"}""")
        val result = mapException(exception)
        (result.contains("найд") || result.contains("имени")) shouldBe true
    }

    test("mapException_404_withoutDetail_returnsNotFoundError") {
        val exception = ApiException(404, "Not found")
        val result = mapException(exception)
        result shouldContain "найд"
    }

    test("mapException_422_returnsValidationErrorMessage") {
        val exception = ApiException(422, """{"detail": "Password too short"}""")
        val result = mapException(exception)
        (result.contains("Некорректные") || result.contains("Проверьте")) shouldBe true
    }

    test("mapException_500_returnsServerErrorMessage") {
        val exception = ApiException(500, """{"detail": "Internal error"}""")
        val result = mapException(exception)
        (result.contains("сервера") || result.contains("сервер")) shouldBe true
    }

    test("mapException_networkException_returnsConnectionErrorMessage") {
        val exception = NetworkException("Connection refused", null)
        val result = mapException(exception)
        (result.contains("подключения") || result.contains("сервер")) shouldBe true
    }
})