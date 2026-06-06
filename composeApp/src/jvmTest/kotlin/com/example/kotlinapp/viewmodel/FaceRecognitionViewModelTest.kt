package com.example.kotlinapp.viewmodel

import com.example.kotlinapp.domain.model.BoundingBox
import com.example.kotlinapp.domain.model.Employee
import com.example.kotlinapp.domain.model.FaceMatch
import com.example.kotlinapp.domain.model.FaceRecognitionResult
import com.example.kotlinapp.domain.model.FaceResult
import com.example.kotlinapp.domain.repository.EmployeeRepository
import com.example.kotlinapp.domain.repository.FaceRecognitionRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest

class FaceRecognitionViewModelTest : FunSpec({

    test("recognize sets result and employeeMap") {
        runTest {
            val faceRecognitionRepository = mockk<FaceRecognitionRepository>()
            val employeeRepository = mockk<EmployeeRepository>()
            val matches = listOf(FaceMatch(id = 1, username = "alice", similarity = 0.95))
            val result = FaceRecognitionResult(
                facesDetected = 1,
                results = listOf(FaceResult(bbox = BoundingBox(0, 0, 100, 100), matches = matches))
            )
            coEvery { faceRecognitionRepository.recognizeFace(any()) } returns result
            coEvery { employeeRepository.searchEmployees("alice") } returns listOf(
                Employee(id = 1, username = "alice", email = "alice@test.com", employeeId = "EMP-1",
                    phone = null, department = null, position = null, location = null,
                    hireDate = null, isActive = true, accessEnabled = true, photoPath = null, createdAt = "2024-01-01")
            )

            val viewModel = FaceRecognitionViewModel(faceRecognitionRepository, employeeRepository)
            viewModel.recognize(byteArrayOf(1, 2, 3))
            delay(200)

            viewModel.uiState.value.result shouldNotBe null
            viewModel.uiState.value.isLoading shouldBe false
            viewModel.uiState.value.errorMessage shouldBe null
        }
    }

    test("recognize error sets errorMessage") {
        runTest {
            val faceRecognitionRepository = mockk<FaceRecognitionRepository>()
            val employeeRepository = mockk<EmployeeRepository>()
            coEvery { faceRecognitionRepository.recognizeFace(any()) } throws RuntimeException("Server error")

            val viewModel = FaceRecognitionViewModel(faceRecognitionRepository, employeeRepository)
            viewModel.recognize(byteArrayOf(1, 2, 3))
            delay(100)

            viewModel.uiState.value.errorMessage shouldNotBe null
            viewModel.uiState.value.isLoading shouldBe false
        }
    }

    test("no matches results in empty employeeMap") {
        runTest {
            val faceRecognitionRepository = mockk<FaceRecognitionRepository>()
            val employeeRepository = mockk<EmployeeRepository>()
            val result = FaceRecognitionResult(
                facesDetected = 1,
                results = listOf(FaceResult(bbox = BoundingBox(0, 0, 100, 100), matches = emptyList()))
            )
            coEvery { faceRecognitionRepository.recognizeFace(any()) } returns result

            val viewModel = FaceRecognitionViewModel(faceRecognitionRepository, employeeRepository)
            viewModel.recognize(byteArrayOf(1, 2, 3))
            delay(100)

            viewModel.uiState.value.employeeMap.shouldBeEmpty()
        }
    }
})
