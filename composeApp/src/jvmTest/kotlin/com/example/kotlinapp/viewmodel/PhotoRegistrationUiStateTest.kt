package com.example.kotlinapp.viewmodel

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PhotoRegistrationUiStateTest : FunSpec({

    test("canUpload is false with 0 photos") {
        val state = PhotoRegistrationUiState()
        state.canUpload shouldBe false
    }

    test("canUpload is false with 1 photo") {
        val state = PhotoRegistrationUiState(
            capturedPhotos = mapOf(0 to byteArrayOf(1))
        )
        state.canUpload shouldBe false
    }

    test("canUpload is false with 2 photos") {
        val state = PhotoRegistrationUiState(
            capturedPhotos = mapOf(0 to byteArrayOf(1), 1 to byteArrayOf(2))
        )
        state.canUpload shouldBe false
    }

    test("canUpload is true with 3 photos") {
        val state = PhotoRegistrationUiState(
            capturedPhotos = mapOf(0 to byteArrayOf(1), 1 to byteArrayOf(2), 2 to byteArrayOf(3))
        )
        state.canUpload shouldBe true
    }

    test("canUpload is true with 5 photos") {
        val state = PhotoRegistrationUiState(
            capturedPhotos = mapOf(
                0 to byteArrayOf(1), 1 to byteArrayOf(2), 2 to byteArrayOf(3),
                3 to byteArrayOf(4), 4 to byteArrayOf(5)
            )
        )
        state.canUpload shouldBe true
    }

    test("isOptionalStep is false for steps 0, 1, 2") {
        PhotoRegistrationUiState(currentStep = 0).isOptionalStep shouldBe false
        PhotoRegistrationUiState(currentStep = 1).isOptionalStep shouldBe false
        PhotoRegistrationUiState(currentStep = 2).isOptionalStep shouldBe false
    }

    test("isOptionalStep is true for steps 3, 4") {
        PhotoRegistrationUiState(currentStep = 3).isOptionalStep shouldBe true
        PhotoRegistrationUiState(currentStep = 4).isOptionalStep shouldBe true
    }

    test("capturedCount returns correct count") {
        val state = PhotoRegistrationUiState(
            capturedPhotos = mapOf(0 to byteArrayOf(1), 2 to byteArrayOf(3))
        )
        state.capturedCount shouldBe 2
    }

    test("capturedCount ignores null entries") {
        val state = PhotoRegistrationUiState(
            capturedPhotos = mapOf(0 to byteArrayOf(1), 1 to null, 2 to byteArrayOf(3))
        )
        state.capturedCount shouldBe 2
    }
})
