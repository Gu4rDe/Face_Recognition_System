package com.example.kotlinapp.data.repository

import com.example.kotlinapp.data.mapper.toDomain
import com.example.kotlinapp.data.mapper.toDto
import com.example.kotlinapp.data.remote.ApiService
import com.example.kotlinapp.domain.model.AppSettings
import com.example.kotlinapp.domain.model.AppSettingsUpdate
import com.example.kotlinapp.domain.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val apiService: ApiService
) : SettingsRepository {

    override suspend fun getSettings(): AppSettings {
        return apiService.getSettings().toDomain()
    }

    override suspend fun updateSettings(update: AppSettingsUpdate): AppSettings {
        return apiService.updateSettings(update.toDto()).toDomain()
    }

    override suspend fun createBackup() {
        apiService.createBackup()
    }
}