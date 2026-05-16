package com.example.kotlinapp.viewmodel

import com.example.kotlinapp.data.remote.ApiService
import com.example.kotlinapp.domain.model.EmployeeStats
import com.example.kotlinapp.domain.repository.EmployeeRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest

class DashboardViewModelTest : FunSpec({

    test("loadStats fetches and sets stats") {
        runTest {
            val employeeRepository = mockk<EmployeeRepository>()
            val apiService = mockk<ApiService>()
            val expectedStats = EmployeeStats(total = 10, active = 8, inactive = 2)
            coEvery { employeeRepository.getEmployeeStats() } returns expectedStats
            coEvery { apiService.healthCheck() } returns true

            val viewModel = DashboardViewModel(employeeRepository, apiService)
            delay(100)

            viewModel.uiState.value.stats shouldBe expectedStats
            viewModel.uiState.value.isLoadingStats shouldBe false
        }
    }

    test("checkServer sets serverStatus to Подключено") {
        runTest {
            val employeeRepository = mockk<EmployeeRepository>()
            val apiService = mockk<ApiService>()
            coEvery { employeeRepository.getEmployeeStats() } returns EmployeeStats(0, 0, 0)
            coEvery { apiService.healthCheck() } returns true

            val viewModel = DashboardViewModel(employeeRepository, apiService)
            delay(100)

            viewModel.uiState.value.serverStatus shouldBe "Подключено"
            viewModel.uiState.value.isLoadingServer shouldBe false
        }
    }

    test("checkServer sets serverStatus to Не подключено on error") {
        runTest {
            val employeeRepository = mockk<EmployeeRepository>()
            val apiService = mockk<ApiService>()
            coEvery { employeeRepository.getEmployeeStats() } returns EmployeeStats(0, 0, 0)
            coEvery { apiService.healthCheck() } throws RuntimeException("Connection refused")

            val viewModel = DashboardViewModel(employeeRepository, apiService)
            delay(100)

            viewModel.uiState.value.serverStatus shouldBe "Не подключено"
        }
    }

    test("loadStats error sets statsError") {
        runTest {
            val employeeRepository = mockk<EmployeeRepository>()
            val apiService = mockk<ApiService>()
            coEvery { employeeRepository.getEmployeeStats() } throws RuntimeException("Server error")
            coEvery { apiService.healthCheck() } returns true

            val viewModel = DashboardViewModel(employeeRepository, apiService)
            delay(100)

            viewModel.uiState.value.statsError shouldNotBe null
        }
    }
})
