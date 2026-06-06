package com.example.kotlinapp.viewmodel

import com.example.kotlinapp.data.local.LocalSettingsStorage
import com.example.kotlinapp.data.remote.ApiClient
import com.example.kotlinapp.data.remote.ApiService
import com.example.kotlinapp.domain.model.AppSettings
import com.example.kotlinapp.domain.repository.AuthRepository
import com.example.kotlinapp.domain.repository.SettingsRepository
import com.example.kotlinapp.util.MainDispatcherRule
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import kotlinx.coroutines.test.runTest

class SettingsViewModelTest : FunSpec({
    val mainDispatcherRule = MainDispatcherRule()
    listener(mainDispatcherRule)

    fun testSettings() = AppSettings(
        id = 1, theme = "light", fullscreen = false,
        cameraResolution = "640x480", cameraFps = 30,
        soundNotifications = true, accessNotifications = true,
        twoFactorEnabled = false,
        autoBackup = false, backendUrl = "http://localhost:8000",
        connectionTimeout = 30
    )

    test("toggleTheme changes isDarkTheme and persists") {
        runTest {
            mockkObject(LocalSettingsStorage)
            every { LocalSettingsStorage.getTheme() } returns "light"
            every { LocalSettingsStorage.getApiUrl() } returns "http://localhost:8000"
            every { LocalSettingsStorage.setTheme(any()) } returns Unit

            val settingsRepository = mockk<SettingsRepository>()
            val apiService = mockk<ApiService>()
            val authRepository = mockk<AuthRepository>()
            every { authRepository.getToken() } returns "test-token"

            val viewModel = SettingsViewModel(settingsRepository, apiService, authRepository)

            viewModel.uiState.value.isDarkTheme shouldBe false
            viewModel.toggleTheme()
            viewModel.uiState.value.isDarkTheme shouldBe true

            verify { LocalSettingsStorage.setTheme("dark") }
            unmockkObject(LocalSettingsStorage)
        }
    }

    test("loadSettings fetches and sets values when authenticated") {
        runTest {
            mockkObject(LocalSettingsStorage)
            every { LocalSettingsStorage.getTheme() } returns "light"
            every { LocalSettingsStorage.getApiUrl() } returns "http://localhost:8000"

            val settingsRepository = mockk<SettingsRepository>()
            val apiService = mockk<ApiService>()
            val authRepository = mockk<AuthRepository>()
            every { authRepository.getToken() } returns "test-token"
            val settings = testSettings().copy(cameraResolution = "1280x720", cameraFps = 60)
            coEvery { settingsRepository.getSettings() } returns settings

            val viewModel = SettingsViewModel(settingsRepository, apiService, authRepository)
            viewModel.loadSettings()
            mainDispatcherRule.testDispatcher.scheduler.runCurrent()

            viewModel.uiState.value.cameraResolution shouldBe "1280x720"
            viewModel.uiState.value.cameraFps shouldBe 60
            unmockkObject(LocalSettingsStorage)
        }
    }

    test("loadSettings skips API call when not authenticated") {
        runTest {
            mockkObject(LocalSettingsStorage)
            every { LocalSettingsStorage.getTheme() } returns "light"
            every { LocalSettingsStorage.getApiUrl() } returns "http://localhost:8000"

            val settingsRepository = mockk<SettingsRepository>()
            val apiService = mockk<ApiService>()
            val authRepository = mockk<AuthRepository>()
            every { authRepository.getToken() } returns null

            val viewModel = SettingsViewModel(settingsRepository, apiService, authRepository)
            viewModel.loadSettings()
            mainDispatcherRule.testDispatcher.scheduler.runCurrent()

            viewModel.uiState.value.isLoadingSettings shouldBe false
            viewModel.uiState.value.loadError shouldBe null
            unmockkObject(LocalSettingsStorage)
        }
    }

    test("saveAndCheckServer calls repository when authenticated") {
        runTest {
            mockkObject(LocalSettingsStorage)
            every { LocalSettingsStorage.getTheme() } returns "light"
            every { LocalSettingsStorage.getApiUrl() } returns "http://localhost:8000"
            every { LocalSettingsStorage.setApiUrl(any()) } returns Unit

            val apiClient = mockk<ApiClient>(relaxed = true)
            val settingsRepository = mockk<SettingsRepository>()
            val apiService = mockk<ApiService>()
            val authRepository = mockk<AuthRepository>()
            every { apiService.apiClient } returns apiClient
            every { authRepository.getToken() } returns "test-token"
            coEvery { settingsRepository.updateSettings(any()) } returns testSettings()
            coEvery { apiService.healthCheck() } returns true

            val viewModel = SettingsViewModel(settingsRepository, apiService, authRepository)
            viewModel.saveAndCheckServer()
            mainDispatcherRule.testDispatcher.scheduler.runCurrent()

            coVerify { settingsRepository.updateSettings(any()) }
            unmockkObject(LocalSettingsStorage)
        }
    }

    test("saveAndCheckServer skips repository when not authenticated") {
        runTest {
            mockkObject(LocalSettingsStorage)
            every { LocalSettingsStorage.getTheme() } returns "light"
            every { LocalSettingsStorage.getApiUrl() } returns "http://localhost:8000"
            every { LocalSettingsStorage.setApiUrl(any()) } returns Unit

            val apiClient = mockk<ApiClient>(relaxed = true)
            val settingsRepository = mockk<SettingsRepository>()
            val apiService = mockk<ApiService>()
            val authRepository = mockk<AuthRepository>()
            every { apiService.apiClient } returns apiClient
            every { authRepository.getToken() } returns null
            coEvery { apiService.healthCheck() } returns true

            val viewModel = SettingsViewModel(settingsRepository, apiService, authRepository)
            viewModel.saveAndCheckServer()
            mainDispatcherRule.testDispatcher.scheduler.runCurrent()

            coVerify(exactly = 0) { settingsRepository.updateSettings(any()) }
            verify { apiClient.baseUrl = any() }
            unmockkObject(LocalSettingsStorage)
        }
    }
})