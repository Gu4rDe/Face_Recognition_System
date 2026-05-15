package com.example.kotlinapp.presentation

import androidx.compose.runtime.mutableStateOf
import com.example.kotlinapp.ServiceLocator
import com.example.kotlinapp.data.local.LocalSettingsStorage
import com.example.kotlinapp.domain.model.AppSettingsUpdate
import com.example.kotlinapp.util.mapException
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

    val isSavingSettings = mutableStateOf(false)

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
            loadError.value = "Не удалось загрузить настройки: ${mapException(e)}"
        } finally {
            isLoadingSettings.value = false
        }
    }

    fun applySettings(onSuccess: () -> Unit = {}) {
        val trimmedUrl = apiUrl.value.trim().removeSuffix("/")
        apiUrl.value = trimmedUrl
        
        LocalSettingsStorage.setApiUrl(trimmedUrl)
        ServiceLocator.updateBaseUrl(trimmedUrl)
        
        saveError.value = null
        isSavingSettings.value = true
        
        saveScope.launch {
            try {
                ServiceLocator.settingsRepository.updateSettings(
                    AppSettingsUpdate(
                        matchThreshold = matchThreshold.value.toDouble(),
                        cameraResolution = cameraResolution.value,
                        cameraFps = cameraFps.value
                    )
                )
                launch(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                saveError.value = "Ошибка сохранения: ${mapException(e)}"
            } finally {
                isSavingSettings.value = false
            }
        }
    }

    suspend fun checkServerConnection() {
        // Ensure the ServiceLocator is using the current (potentially unapplied) URL for the check
        // Or apply it temporarily
        val currentUrl = apiUrl.value.trim()
        ServiceLocator.updateBaseUrl(currentUrl)
        
        isCheckingServer.value = true
        serverStatus.value = try {
            if (ServiceLocator.apiService.healthCheck()) "Подключено" else "Не подключено"
        } catch (_: Exception) {
            "Не подключено"
        } finally {
            isCheckingServer.value = false
        }
    }
}