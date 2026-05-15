package com.example.kotlinapp

import com.example.kotlinapp.data.dto.*
import com.example.kotlinapp.data.mapper.toDomain
import com.example.kotlinapp.data.mapper.toDto
import com.example.kotlinapp.domain.model.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SettingsMapperTest : FunSpec({

    test("SettingsResponseDto maps to AppSettings correctly") {
        val dto = SettingsResponseDto(
            id = 1L,
            theme = "dark",
            fullscreen = true,
            camera_resolution = "1280x720",
            camera_fps = 30,
            sound_notifications = true,
            access_notifications = false,
            match_threshold = 0.75,
            two_factor_enabled = false,
            auto_backup = true,
            backend_url = "http://localhost:8000",
            connection_timeout = 30
        )
        val domain = dto.toDomain()
        domain.id shouldBe 1L
        domain.theme shouldBe "dark"
        domain.fullscreen shouldBe true
        domain.cameraResolution shouldBe "1280x720"
        domain.cameraFps shouldBe 30
        domain.matchThreshold shouldBe 0.75
    }

    test("AppSettingsUpdate maps to SettingsUpdateDto correctly") {
        val domain = AppSettingsUpdate(
            matchThreshold = 0.8,
            cameraResolution = "1920x1080",
            cameraFps = 60
        )
        val dto = domain.toDto()
        dto.match_threshold shouldBe 0.8
        dto.camera_resolution shouldBe "1920x1080"
        dto.camera_fps shouldBe 60
        dto.theme shouldBe null
        dto.fullscreen shouldBe null
    }
})