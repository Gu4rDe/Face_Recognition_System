package com.example.kotlinapp.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class SettingsResponseDto(
    val id: Long,
    val theme: String,
    val fullscreen: Boolean,
    val camera_resolution: String,
    val camera_fps: Int,
    val sound_notifications: Boolean,
    val access_notifications: Boolean,
    val match_threshold: Double,
    val two_factor_enabled: Boolean,
    val auto_backup: Boolean,
    val backend_url: String,
    val connection_timeout: Int
)

@Serializable
data class SettingsUpdateDto(
    val theme: String? = null,
    val fullscreen: Boolean? = null,
    val camera_resolution: String? = null,
    val camera_fps: Int? = null,
    val sound_notifications: Boolean? = null,
    val access_notifications: Boolean? = null,
    val match_threshold: Double? = null,
    val two_factor_enabled: Boolean? = null,
    val auto_backup: Boolean? = null,
    val backend_url: String? = null,
    val connection_timeout: Int? = null
)