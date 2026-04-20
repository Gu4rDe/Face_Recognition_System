package com.example.kotlinapp.util

import com.example.kotlinapp.data.remote.ApiException
import com.example.kotlinapp.data.remote.NetworkException

fun mapException(e: Exception): String = when (e) {
    is ApiException -> when (e.code) {
        400 -> "${parseDetail(e.message) ?: "Некорректные данные"}\nПроверьте правильность заполнения всех полей"
        401 -> "Неверный логин или пароль\nПроверьте правильность ввода и попробуйте снова"
        403 -> "${parseDetail(e.message) ?: "Доступ запрещён"}\nУбедитесь, что ваш код приглашения действителен"
        404 -> "Сервер не найден"
        409 -> "Пользователь уже существует\nПопробуйте другой логин или email"
        422 -> "${parseDetail(e.message) ?: "Некорректные данные"}\nПроверьте правильность заполнения всех полей"
        in 500..599 -> "Ошибка сервера. Попробуйте позже"
        else -> parseDetail(e.message) ?: "Ошибка (${e.code})"
    }
    is NetworkException -> "Нет подключения к серверу. Проверьте интернет"
    else -> {
        val msg = e.message.orEmpty().lowercase()
        when {
            "connection refused" in msg || "getsockopt" in msg -> "Сервер недоступен. Убедитесь, что сервер запущен"
            "timeout" in msg || "timed out" in msg -> "Превышено время ожидания. Проверьте подключение"
            "resolve" in msg || "unknown host" in msg -> "Не удалось подключиться к серверу. Проверьте адрес"
            else -> e.message ?: "Неизвестная ошибка"
        }
    }
}

private fun parseDetail(body: String?): String? {
    if (body == null) return null
    val detailRegex = """"detail"\s*:\s*"([^"]+)"""".toRegex()
    return detailRegex.find(body)?.groupValues?.getOrNull(1)
}