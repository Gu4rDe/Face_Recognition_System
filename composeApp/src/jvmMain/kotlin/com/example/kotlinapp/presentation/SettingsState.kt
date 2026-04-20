package com.example.kotlinapp.presentation

import androidx.compose.runtime.mutableStateOf
import com.example.kotlinapp.ServiceLocator
import com.example.kotlinapp.data.local.LocalSettingsStorage
import com.example.kotlinapp.domain.model.AppSettingsUpdate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SettingsState {

    private val saveScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val isDarkTheme = mutableStateOf(LocalSettingsStorage.getTheme() == "dark")

    val apiUrl = mutableStateOf(LocalSettingsStorage.getApiUrl())

    val showSettings = mutableStateOf(false)

    val serverStatus = mutableStateOf("Не проверено")

    val isCheckingServer = mutableStateOf(false)

    val isLoadingSettings = mutableStateOf(false)

    val matchThreshold = mutableStateOf(0.6f)

    val cameraResolution = mutableStateOf("640x480")

    val cameraFps = mutableStateOf(30)

    val loadError = mutableStateOf<String?>(null)

    val saveError = mutableStateOf<String?>(null)

    fun toggleTheme() {
        isDarkTheme.value = !isDarkTheme.value
        LocalSettingsStorage.setTheme(if (isDarkTheme.value) "dark" else "light")
    }

    fun onApiUrlChanged(url: String) {
        apiUrl.value = url
    }

    suspend fun loadFaceRecognitionSettings() {
        isLoadingSettings.value = true
        loadError.value = null
        try {
            val settings = ServiceLocator.settingsRepository.getSettings()
            matchThreshold.value = settings.matchThreshold.toFloat()
            cameraResolution.value = settings.cameraResolution
            cameraFps.value = settings.cameraFps
        } catch (e: Exception) {
            loadError.value = "Не удалось загрузить настройки: ${e.message}"
        }
        isLoadingSettings.value = false
    }

    fun applySettings() {
        LocalSettingsStorage.setApiUrl(apiUrl.value)
        ServiceLocator.updateBaseUrl(apiUrl.value)
        saveError.value = null
        saveScope.launch {
            try {
                ServiceLocator.settingsRepository.updateSettings(
                    AppSettingsUpdate(
                        matchThreshold = matchThreshold.value.toDouble(),
                        cameraResolution = cameraResolution.value,
                        cameraFps = cameraFps.value
                    )
                )
            } catch (e: Exception) {
                saveError.value = "Не удалось сохранить настройки: ${e.message}"
            }
        }
    }

    suspend fun checkServerConnection() {
        isCheckingServer.value = true
        serverStatus.value = try {
            if (ServiceLocator.apiService.healthCheck()) "Подключено" else "Не подключено"
        } catch (_: Exception) {
            "Не подключено"
        }
        isCheckingServer.value = false
    }
}