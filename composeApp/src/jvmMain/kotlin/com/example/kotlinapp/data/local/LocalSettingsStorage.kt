package com.example.kotlinapp.data.local

import java.util.prefs.Preferences

/**
 * Локальное хранилище настроек приложения.
 * Использует java.util.prefs.Preferences — данные сохраняются между запусками
 * в реестре (Windows) или dot-файлах (Linux/macOS).
 */
object LocalSettingsStorage {
    private val prefs = Preferences.userNodeForPackage(LocalSettingsStorage::class.java)

    private const val KEY_THEME = "theme"
    private const val KEY_API_URL = "api_url"

    /** Возвращает выбранную тему: "light" (по умолчанию) или "dark". */
    fun getTheme(): String = prefs.get(KEY_THEME, "light")

    /** Сохраняет выбор темы ("light" / "dark"). */
    fun setTheme(theme: String) = prefs.put(KEY_THEME, theme)

    /** Возвращает URL API-сервера, по умолчанию http://localhost:8000. */
    fun getApiUrl(): String = prefs.get(KEY_API_URL, "http://localhost:8000")

    /** Сохраняет URL API-сервера. */
    fun setApiUrl(url: String) = prefs.put(KEY_API_URL, url)
}