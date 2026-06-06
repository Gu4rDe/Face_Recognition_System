package com.example.kotlinapp.util

import com.example.kotlinapp.data.remote.ApiException
import com.example.kotlinapp.data.remote.NetworkException
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ErrorMapperTest : FunSpec({

    test("401 returns login error message") {
        val result = mapException(ApiException(401, "detail\" : \"Invalid credentials\""))
        result shouldBe "Неверный логин или пароль\nПроверьте правильность ввода и попробуйте снова"
    }

    test("400 returns validation error message") {
        val result = mapException(ApiException(400, "detail\" : \"Invalid data\""))
        result shouldBe "Некорректные данные\nПроверьте правильность заполнения всех полей"
    }

    test("403 returns forbidden message") {
        val result = mapException(ApiException(403, "detail\" : \"Access denied\""))
        result shouldBe "Access denied\nПроверьте, что ваш код приглашения действителен и не истёк"
    }

    test("404 returns not found message") {
        val result = mapException(ApiException(404, "detail\" : \"User not found\""))
        result shouldBe "Пользователь не найден\nПроверьте правильность имени пользователя"
    }

    test("409 returns conflict message") {
        val result = mapException(ApiException(409, "detail\" : \"User exists\""))
        result shouldBe "Пользователь уже существует\nПопробуйте другой логин или email"
    }

    test("422 returns unprocessable message") {
        val result = mapException(ApiException(422, "detail\" : \"Invalid\""))
        result shouldBe "Некорректные данные\nПроверьте правильность заполнения всех полей"
    }

    test("500 returns server error message") {
        val result = mapException(ApiException(500, "detail\" : \"Internal error\""))
        result shouldBe "Ошибка сервера. Попробуйте позже"
    }

    test("NetworkException returns network error message") {
        val result = mapException(NetworkException("Connection refused", null))
        result shouldBe "Нет подключения к серверу. Проверьте интернет"
    }

    test("Connection refused exception returns server unavailable message") {
        val result = mapException(RuntimeException("Connection refused"))
        result shouldBe "Сервер недоступен. Убедитесь, что сервер запущен"
    }

    test("Timeout exception returns timeout message") {
        val result = mapException(RuntimeException("Request timed out"))
        result shouldBe "Превышено время ожидания. Проверьте подключение"
    }

    test("Unknown host exception returns host error message") {
        val result = mapException(RuntimeException("Unknown host"))
        result shouldBe "Не удалось подключиться к серверу. Проверьте адрес"
    }

    test("Generic exception returns message") {
        val result = mapException(RuntimeException("Some error"))
        result shouldBe "Some error"
    }
})
