package com.example.kotlinapp

import com.example.kotlinapp.data.remote.ApiException
import com.example.kotlinapp.data.remote.NetworkException
import com.example.kotlinapp.util.mapException
import org.junit.Test
import org.junit.Assert.*

class PasswordRecoveryErrorMapperTest {

    @Test
    fun mapException_403_withDetail_returnsInviteCodeError() {
        val exception = ApiException(403, """{"detail": "Invalid invite code"}""")
        val result = mapException(exception)
        assertTrue(result.contains("приглашения"))
    }

    @Test
    fun mapException_403_withoutDetail_returnsForbiddenMessage() {
        val exception = ApiException(403, "No detail provided")
        val result = mapException(exception)
        assertTrue(result.contains("запрещён") || result.contains("приглашения"))
    }

    @Test
    fun mapException_404_withDetail_returnsUserNotFoundError() {
        val exception = ApiException(404, """{"detail": "User not found"}""")
        val result = mapException(exception)
        assertTrue(result.contains("найд") || result.contains("имени"))
    }

    @Test
    fun mapException_404_withoutDetail_returnsNotFoundError() {
        val exception = ApiException(404, "Not found")
        val result = mapException(exception)
        assertTrue(result.contains("найд"))
    }

    @Test
    fun mapException_422_returnsValidationErrorMessage() {
        val exception = ApiException(422, """{"detail": "Password too short"}""")
        val result = mapException(exception)
        assertTrue(result.contains("Некорректные") || result.contains("Проверьте"))
    }

    @Test
    fun mapException_500_returnsServerErrorMessage() {
        val exception = ApiException(500, """{"detail": "Internal error"}""")
        val result = mapException(exception)
        assertTrue(result.contains("сервера") || result.contains("сервер"))
    }

    @Test
    fun mapException_networkException_returnsConnectionErrorMessage() {
        val exception = NetworkException("Connection refused", null)
        val result = mapException(exception)
        assertTrue(result.contains("подключения") || result.contains("сервер"))
    }
}
