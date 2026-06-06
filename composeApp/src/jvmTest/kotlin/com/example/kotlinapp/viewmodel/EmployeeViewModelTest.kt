package com.example.kotlinapp.viewmodel

import com.example.kotlinapp.domain.model.Employee
import com.example.kotlinapp.domain.repository.EmployeeRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest

class EmployeeViewModelTest : FunSpec({

    fun testEmployee(id: Long = 1, username: String = "alice") = Employee(
        id = id, employeeId = "EMP-$id", username = username, email = "$username@test.com",
        phone = null, department = null, position = null, location = null,
        hireDate = null, isActive = true, accessEnabled = true, photoPath = null, createdAt = "2024-01-01"
    )

    test("loadAll fetches employees") {
        runTest {
            val employeeRepository = mockk<EmployeeRepository>()
            val employees = listOf(testEmployee(1, "alice"), testEmployee(2, "bob"))
            coEvery { employeeRepository.listEmployees(any(), any()) } returns employees

            val viewModel = EmployeeViewModel(employeeRepository)
            delay(100)

            viewModel.uiState.value.employees shouldHaveSize 2
            viewModel.uiState.value.isLoading shouldBe false
        }
    }

    test("search delegates to repository") {
        runTest {
            val employeeRepository = mockk<EmployeeRepository>()
            coEvery { employeeRepository.listEmployees(any(), any()) } returns emptyList()
            coEvery { employeeRepository.searchEmployees("alice") } returns listOf(testEmployee(1, "alice"))

            val viewModel = EmployeeViewModel(employeeRepository)
            delay(100)
            viewModel.searchEmployees("alice")
            delay(100)

            viewModel.uiState.value.employees shouldHaveSize 1
            coVerify { employeeRepository.searchEmployees("alice") }
        }
    }

    test("delete calls repository and reloads") {
        runTest {
            val employeeRepository = mockk<EmployeeRepository>()
            coEvery { employeeRepository.listEmployees(any(), any()) } returns emptyList()
            coEvery { employeeRepository.deleteEmployee(1L) } returns Unit

            val viewModel = EmployeeViewModel(employeeRepository)
            delay(100)
            viewModel.deleteEmployee(1L)
            delay(100)

            coVerify { employeeRepository.deleteEmployee(1L) }
        }
    }

    test("onCapture stores photo and advances step") {
        runTest {
            val employeeRepository = mockk<EmployeeRepository>()
            coEvery { employeeRepository.listEmployees(any(), any()) } returns emptyList()

            val viewModel = EmployeeViewModel(employeeRepository)
            delay(100)
            viewModel.onPhotoCapture(byteArrayOf(1, 2, 3))

            viewModel.photoState.value.capturedCount shouldBe 1
            viewModel.photoState.value.currentStep shouldBe 1
        }
    }

    test("onSkipStep advances step only for optional steps") {
        runTest {
            val employeeRepository = mockk<EmployeeRepository>()
            coEvery { employeeRepository.listEmployees(any(), any()) } returns emptyList()

            val viewModel = EmployeeViewModel(employeeRepository)
            delay(100)
            viewModel.onSkipStep()
            viewModel.photoState.value.currentStep shouldBe 0

            viewModel.onPhotoCapture(byteArrayOf(1))
            viewModel.onPhotoCapture(byteArrayOf(2))
            viewModel.onPhotoCapture(byteArrayOf(3))

            viewModel.onSkipStep()
            viewModel.photoState.value.currentStep shouldBe 4
        }
    }

    test("canUpload is false with less than 3 photos") {
        runTest {
            val employeeRepository = mockk<EmployeeRepository>()
            coEvery { employeeRepository.listEmployees(any(), any()) } returns emptyList()

            val viewModel = EmployeeViewModel(employeeRepository)
            delay(100)
            viewModel.onPhotoCapture(byteArrayOf(1))
            viewModel.onPhotoCapture(byteArrayOf(2))

            viewModel.photoState.value.canUpload shouldBe false
        }
    }

    test("canUpload is true with 3 or more photos") {
        runTest {
            val employeeRepository = mockk<EmployeeRepository>()
            coEvery { employeeRepository.listEmployees(any(), any()) } returns emptyList()

            val viewModel = EmployeeViewModel(employeeRepository)
            delay(100)
            viewModel.onPhotoCapture(byteArrayOf(1))
            viewModel.onPhotoCapture(byteArrayOf(2))
            viewModel.onPhotoCapture(byteArrayOf(3))

            viewModel.photoState.value.canUpload shouldBe true
        }
    }
})
