package com.example.kotlinapp.viewmodel

data class SettingsUiState(
    val isDarkTheme: Boolean = false,
    val apiUrl: String = "",
    val showSettings: Boolean = false,
    val serverStatus: String = "Не проверено",
    val isCheckingServer: Boolean = false,
    val isLoadingSettings: Boolean = false,
    val isSavingSettings: Boolean = false,
    val matchThreshold: Float = 0.6f,
    val cameraResolution: String = "640x480",
    val cameraFps: Int = 30,
    val loadError: String? = null,
    val saveError: String? = null
)
