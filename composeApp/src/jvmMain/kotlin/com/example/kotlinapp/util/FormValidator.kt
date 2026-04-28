package com.example.kotlinapp.util

object FormValidator {

    fun validateUsername(value: String): String? {
        if (value.isBlank()) return "Введите логин"
        if (value.trim().length < 3) return "Логин должен содержать минимум 3 символа"
        if (value.contains(" ")) return "Логин не должен содержать пробелы"
        if (!value.matches(Regex("^[a-zA-Z0-9_]+$"))) return "Логин может содержать только латинские буквы, цифры и подчёркивания"
        return null
    }

    fun validateEmail(value: String): String? {
        if (value.isBlank()) return "Введите email"
        if (!value.contains("@") || !value.contains(".")) return "Введите корректный email (например, user@example.com)"
        val parts = value.split("@")
        if (parts.size != 2 || parts[0].isBlank() || parts[1].isBlank()) return "Введите корректный email (например, user@example.com)"
        return null
    }

    fun validatePassword(value: String): String? {
        if (value.isBlank()) return "Введите пароль"
        if (value.length < 8) return "Пароль должен содержать минимум 8 символов"
        if (!value.any { it.isLetter() }) return "Пароль должен содержать хотя бы одну букву"
        if (!value.any { it.isDigit() }) return "Пароль должен содержать хотя бы одну цифру"
        if (!value.any { !it.isLetterOrDigit() }) return "Пароль должен содержать хотя бы один спецсимвол (!@#$%^&*)"
        return null
    }

    fun validateInviteCode(value: String): String? {
        if (value.isBlank()) return "Введите код приглашения"
        return null
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        if (confirmPassword.isBlank()) return "Подтвердите пароль"
        if (password != confirmPassword) return "Пароли не совпадают"
        return null
    }
}
