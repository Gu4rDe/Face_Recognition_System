package com.example.kotlinapp.viewmodel

import com.example.kotlinapp.domain.model.AuthResult
import com.example.kotlinapp.domain.repository.AuthRepository
import com.example.kotlinapp.util.MainDispatcherRule
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class LoginViewModelTest : FunSpec({
    val mainDispatcherRule = MainDispatcherRule()
    listener(mainDispatcherRule)

    test("login success sets isSuccess to true") {
        runTest {
            val authRepository = mockk<AuthRepository>()
            coEvery { authRepository.login(any()) } returns AuthResult(accessToken = "token123")

            val viewModel = LoginViewModel(authRepository)
            viewModel.login("admin", "password123")
            mainDispatcherRule.testDispatcher.scheduler.runCurrent()

            viewModel.uiState.value.isSuccess shouldBe true
            viewModel.uiState.value.isLoading shouldBe false
            viewModel.uiState.value.errorMessage shouldBe null
            coVerify { authRepository.login(any()) }
        }
    }

    test("login failure sets errorMessage") {
        runTest {
            val authRepository = mockk<AuthRepository>()
            coEvery { authRepository.login(any()) } throws RuntimeException("Connection refused")

            val viewModel = LoginViewModel(authRepository)
            viewModel.login("admin", "wrong")
            mainDispatcherRule.testDispatcher.scheduler.runCurrent()

            viewModel.uiState.value.isSuccess shouldBe false
            viewModel.uiState.value.isLoading shouldBe false
            viewModel.uiState.value.errorMessage shouldNotBe null
        }
    }

    test("logout clears token") {
        runTest {
            val authRepository = mockk<AuthRepository>()
            every { authRepository.setToken(null) } returns Unit

            val viewModel = LoginViewModel(authRepository)
            viewModel.logout()

            coVerify { authRepository.setToken(null) }
        }
    }
})