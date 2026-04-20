package com.example.kotlinapp.domain.repository

import com.example.kotlinapp.domain.model.AppSettings
import com.example.kotlinapp.domain.model.AppSettingsUpdate

interface SettingsRepository {
    suspend fun getSettings(): AppSettings
    suspend fun updateSettings(update: AppSettingsUpdate): AppSettings
    suspend fun createBackup()
}