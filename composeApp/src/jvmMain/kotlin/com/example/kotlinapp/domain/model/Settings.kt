package com.example.kotlinapp.domain.model

data class AppSettings(
    val id: Long,
    val theme: String,
    val fullscreen: Boolean,
    val cameraResolution: String,
    val cameraFps: Int,
    val soundNotifications: Boolean,
    val accessNotifications: Boolean,
    val matchThreshold: Double,
    val twoFactorEnabled: Boolean,
    val autoBackup: Boolean,
    val backendUrl: String,
    val connectionTimeout: Int
)

data class AppSettingsUpdate(
    val theme: String? = null,
    val fullscreen: Boolean? = null,
    val cameraResolution: String? = null,
    val cameraFps: Int? = null,
    val soundNotifications: Boolean? = null,
    val accessNotifications: Boolean? = null,
    val matchThreshold: Double? = null,
    val twoFactorEnabled: Boolean? = null,
    val autoBackup: Boolean? = null,
    val backendUrl: String? = null,
    val connectionTimeout: Int? = null
)