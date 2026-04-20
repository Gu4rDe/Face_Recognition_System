package com.example.kotlinapp.data.mapper

import com.example.kotlinapp.data.dto.SettingsResponseDto
import com.example.kotlinapp.data.dto.SettingsUpdateDto
import com.example.kotlinapp.domain.model.AppSettings
import com.example.kotlinapp.domain.model.AppSettingsUpdate

fun SettingsResponseDto.toDomain() = AppSettings(
    id = id,
    theme = theme,
    fullscreen = fullscreen,
    cameraResolution = camera_resolution,
    cameraFps = camera_fps,
    soundNotifications = sound_notifications,
    accessNotifications = access_notifications,
    matchThreshold = match_threshold,
    twoFactorEnabled = two_factor_enabled,
    autoBackup = auto_backup,
    backendUrl = backend_url,
    connectionTimeout = connection_timeout
)

fun AppSettingsUpdate.toDto() = SettingsUpdateDto(
    theme = theme,
    fullscreen = fullscreen,
    camera_resolution = cameraResolution,
    camera_fps = cameraFps,
    sound_notifications = soundNotifications,
    access_notifications = accessNotifications,
    match_threshold = matchThreshold,
    two_factor_enabled = twoFactorEnabled,
    auto_backup = autoBackup,
    backend_url = backendUrl,
    connection_timeout = connectionTimeout
)