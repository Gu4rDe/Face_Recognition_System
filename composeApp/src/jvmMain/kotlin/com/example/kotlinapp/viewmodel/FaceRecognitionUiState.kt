package com.example.kotlinapp.viewmodel

import com.example.kotlinapp.domain.model.Employee
import com.example.kotlinapp.domain.model.FaceRecognitionResult

data class FaceRecognitionUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val result: FaceRecognitionResult? = null,
    val employeeMap: Map<Long, Employee> = emptyMap()
)
