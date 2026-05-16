package com.example.kotlinapp.viewmodel

import com.example.kotlinapp.domain.model.Admin
import com.example.kotlinapp.domain.repository.AuthRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest

class RegisterViewModelTest : FunSpec({

    test("register success sets isSuccess to true") {
        runTest {
            val authRepository = mockk<AuthRepository>()
            coEvery { authRepository.register(any()) } returns Admin(id = 1, username = "admin", email = "admin@test.com", createdAt = "2024-01-01")

            val viewModel = RegisterViewModel(authRepository)
            viewModel.register("admin", "admin@test.com", "password123", "INVITE001")
            delay(100)

            viewModel.uiState.value.isSuccess shouldBe true
            viewModel.uiState.value.isLoading shouldBe false
            viewModel.uiState.value.errorMessage shouldBe null
        }
    }

    test("register with invalid invite code sets error") {
        runTest {
            val authRepository = mockk<AuthRepository>()
            coEvery { authRepository.register(any()) } throws RuntimeException("detail\" : \"Invalid invite code\"")

            val viewModel = RegisterViewModel(authRepository)
            viewModel.register("admin", "admin@test.com", "password123", "INVALID")
            delay(100)

            viewModel.uiState.value.isSuccess shouldBe false
            viewModel.uiState.value.errorMessage shouldNotBe null
        }
    }

    test("register with duplicate username sets error") {
        runTest {
            val authRepository = mockk<AuthRepository>()
            coEvery { authRepository.register(any()) } throws RuntimeException("detail\" : \"User already exists\"")

            val viewModel = RegisterViewModel(authRepository)
            viewModel.register("existing", "admin@test.com", "password123", "INVITE001")
            delay(100)

            viewModel.uiState.value.isSuccess shouldBe false
            viewModel.uiState.value.errorMessage shouldNotBe null
        }
    }
})
