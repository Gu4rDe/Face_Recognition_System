package com.example.kotlinapp

import com.example.kotlinapp.data.local.LocalSettingsStorage
import com.example.kotlinapp.data.remote.ApiClient
import com.example.kotlinapp.data.remote.ApiService
import com.example.kotlinapp.data.repository.AuthRepositoryImpl
import com.example.kotlinapp.data.repository.EmployeeRepositoryImpl
import com.example.kotlinapp.data.repository.InviteCodeRepositoryImpl
import com.example.kotlinapp.data.repository.SettingsRepositoryImpl
import com.example.kotlinapp.data.repository.FaceRecognitionRepositoryImpl
import com.example.kotlinapp.domain.repository.AuthRepository
import com.example.kotlinapp.domain.repository.EmployeeRepository
import com.example.kotlinapp.domain.repository.InviteCodeRepository
import com.example.kotlinapp.domain.repository.SettingsRepository
import com.example.kotlinapp.domain.repository.FaceRecognitionRepository

object ServiceLocator {

    val apiClient = ApiClient(LocalSettingsStorage.getApiUrl())
    val apiService = ApiService(apiClient)

    val authRepository: AuthRepository = AuthRepositoryImpl(apiService)
    val employeeRepository: EmployeeRepository = EmployeeRepositoryImpl(apiService)
    val inviteCodeRepository: InviteCodeRepository = InviteCodeRepositoryImpl(apiService)
    val settingsRepository: SettingsRepository = SettingsRepositoryImpl(apiService)
    val faceRecognitionRepository: FaceRecognitionRepository = FaceRecognitionRepositoryImpl(apiService)

    fun updateBaseUrl(url: String) {
        apiClient.baseUrl = url
    }
}