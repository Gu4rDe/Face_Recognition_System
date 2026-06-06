package com.example.kotlinapp.viewmodel

import com.example.kotlinapp.domain.repository.AuthRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest

class PasswordRecoveryViewModelTest : FunSpec({

    test("reset password success sets successMessage") {
        runTest {
            val authRepository = mockk<AuthRepository>()
            coEvery { authRepository.resetPassword(any()) } returns "Password reset successfully"

            val viewModel = PasswordRecoveryViewModel(authRepository)
            viewModel.resetPassword("admin", "INVITE001", "newpassword123")
            delay(100)

            viewModel.uiState.value.successMessage shouldNotBe null
            viewModel.uiState.value.isLoading shouldBe false
            viewModel.uiState.value.errorMessage shouldBe null
        }
    }

    test("reset password with invalid invite code sets error") {
        runTest {
            val authRepository = mockk<AuthRepository>()
            coEvery { authRepository.resetPassword(any()) } throws RuntimeException("detail\" : \"Invalid invite code\"")

            val viewModel = PasswordRecoveryViewModel(authRepository)
            viewModel.resetPassword("admin", "INVALID", "newpassword123")
            delay(100)

            viewModel.uiState.value.successMessage shouldBe null
            viewModel.uiState.value.errorMessage shouldNotBe null
        }
    }
})
