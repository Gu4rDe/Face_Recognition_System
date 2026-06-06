package com.example.kotlinapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinapp.domain.repository.EmployeeRepository
import com.example.kotlinapp.domain.repository.FaceRecognitionRepository
import com.example.kotlinapp.util.mapException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FaceRecognitionViewModel(
    private val faceRecognitionRepository: FaceRecognitionRepository,
    private val employeeRepository: EmployeeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FaceRecognitionUiState())
    val uiState: StateFlow<FaceRecognitionUiState> = _uiState.asStateFlow()

    fun recognize(imageBytes: ByteArray) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, result = null, employeeMap = emptyMap()) }
            try {
                val recognitionResult = faceRecognitionRepository.recognizeFace(imageBytes)
                val matches = recognitionResult.results.flatMap { it.matches }
                val employeeMap = if (matches.isNotEmpty()) {
                    coroutineScope {
                        val deferred = matches.map { match ->
                            async {
                                try {
                                    val found = employeeRepository.searchEmployees(match.username)
                                    found.firstOrNull { it.id == match.id }
                                } catch (_: Exception) {
                                    null
                                }
                            }
                        }
                        deferred.awaitAll().filterNotNull().associateBy { it.id }
                    }
                } else {
                    emptyMap()
                }
                _uiState.update { it.copy(isLoading = false, result = recognitionResult, employeeMap = employeeMap) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = mapException(e)) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
