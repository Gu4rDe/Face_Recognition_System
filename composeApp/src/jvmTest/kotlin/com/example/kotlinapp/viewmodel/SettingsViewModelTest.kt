package com.example.kotlinapp.viewmodel

import com.example.kotlinapp.data.local.LocalSettingsStorage
import com.example.kotlinapp.data.remote.ApiService
import com.example.kotlinapp.domain.model.AppSettings
import com.example.kotlinapp.domain.repository.SettingsRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest

class SettingsViewModelTest : FunSpec({

    fun testSettings() = AppSettings(
        id = 1, theme = "light", fullscreen = false,
        cameraResolution = "640x480", cameraFps = 30,
        soundNotifications = true, accessNotifications = true,
        matchThreshold = 0.6, twoFactorEnabled = false,
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
            coEvery { settingsRepository.getSettings() } returns testSettings()

            val viewModel = SettingsViewModel(settingsRepository, apiService)

            viewModel.uiState.value.isDarkTheme shouldBe false
            viewModel.toggleTheme()
            viewModel.uiState.value.isDarkTheme shouldBe true

            verify { LocalSettingsStorage.setTheme("dark") }
            unmockkObject(LocalSettingsStorage)
        }
    }

    test("loadSettings fetches and sets values") {
        runTest {
            mockkObject(LocalSettingsStorage)
            every { LocalSettingsStorage.getTheme() } returns "light"
            every { LocalSettingsStorage.getApiUrl() } returns "http://localhost:8000"

            val settingsRepository = mockk<SettingsRepository>()
            val apiService = mockk<ApiService>()
            val settings = testSettings().copy(matchThreshold = 0.8, cameraResolution = "1280x720", cameraFps = 60)
            coEvery { settingsRepository.getSettings() } returns settings

            val viewModel = SettingsViewModel(settingsRepository, apiService)
            viewModel.loadSettings()
            delay(100)

            viewModel.uiState.value.matchThreshold shouldBe 0.8f
            viewModel.uiState.value.cameraResolution shouldBe "1280x720"
            viewModel.uiState.value.cameraFps shouldBe 60
            unmockkObject(LocalSettingsStorage)
        }
    }

    test("applySettings calls repository") {
        runTest {
            mockkObject(LocalSettingsStorage)
            every { LocalSettingsStorage.getTheme() } returns "light"
            every { LocalSettingsStorage.getApiUrl() } returns "http://localhost:8000"

            val settingsRepository = mockk<SettingsRepository>()
            val apiService = mockk<ApiService>()
            coEvery { settingsRepository.getSettings() } returns testSettings()
            coEvery { settingsRepository.updateSettings(any()) } returns testSettings()

            val viewModel = SettingsViewModel(settingsRepository, apiService)
            viewModel.applySettings()
            delay(100)

            coVerify { settingsRepository.updateSettings(any()) }
            unmockkObject(LocalSettingsStorage)
        }
    }
})
