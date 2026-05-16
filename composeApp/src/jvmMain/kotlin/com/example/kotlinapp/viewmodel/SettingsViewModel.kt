package com.example.kotlinapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinapp.data.local.LocalSettingsStorage
import com.example.kotlinapp.data.remote.ApiService
import com.example.kotlinapp.domain.model.AppSettingsUpdate
import com.example.kotlinapp.domain.repository.SettingsRepository
import com.example.kotlinapp.util.mapException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            isDarkTheme = LocalSettingsStorage.getTheme() == "dark",
            apiUrl = LocalSettingsStorage.getApiUrl()
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun toggleTheme() {
        val newTheme = !_uiState.value.isDarkTheme
        _uiState.update { it.copy(isDarkTheme = newTheme) }
        LocalSettingsStorage.setTheme(if (newTheme) "dark" else "light")
    }

    fun onApiUrlChanged(url: String) {
        _uiState.update { it.copy(apiUrl = url) }
    }

    fun setShowSettings(show: Boolean) {
        _uiState.update { it.copy(showSettings = show) }
    }

    fun loadSettings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingSettings = true, loadError = null) }
            try {
                val settings = settingsRepository.getSettings()
                _uiState.update {
                    it.copy(
                        isLoadingSettings = false,
                        matchThreshold = settings.matchThreshold.toFloat(),
                        cameraResolution = settings.cameraResolution,
                        cameraFps = settings.cameraFps
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingSettings = false, loadError = "Не удалось загрузить настройки: ${mapException(e)}") }
            }
        }
    }

    fun applySettings(onSuccess: () -> Unit = {}) {
        val trimmedUrl = _uiState.value.apiUrl.trim().removeSuffix("/")
        _uiState.update { it.copy(apiUrl = trimmedUrl, isSavingSettings = true, saveError = null) }

        LocalSettingsStorage.setApiUrl(trimmedUrl)

        viewModelScope.launch {
            try {
                settingsRepository.updateSettings(
                    AppSettingsUpdate(
                        matchThreshold = _uiState.value.matchThreshold.toDouble(),
                        cameraResolution = _uiState.value.cameraResolution,
                        cameraFps = _uiState.value.cameraFps
                    )
                )
                _uiState.update { it.copy(isSavingSettings = false) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(isSavingSettings = false, saveError = "Ошибка сохранения: ${mapException(e)}") }
            }
        }
    }

    fun checkServerConnection() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCheckingServer = true) }
            val currentUrl = _uiState.value.apiUrl.trim()
            try {
                val wasConnected = apiService.healthCheck()
                _uiState.update {
                    it.copy(
                        isCheckingServer = false,
                        serverStatus = if (wasConnected) "Подключено" else "Не подключено"
                    )
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(isCheckingServer = false, serverStatus = "Не подключено") }
            }
        }
    }

    fun onMatchThresholdChanged(value: Float) {
        _uiState.update { it.copy(matchThreshold = value) }
    }

    fun onCameraResolutionChanged(value: String) {
        _uiState.update { it.copy(cameraResolution = value) }
    }

    fun onCameraFpsChanged(value: Int) {
        _uiState.update { it.copy(cameraFps = value) }
    }
}
