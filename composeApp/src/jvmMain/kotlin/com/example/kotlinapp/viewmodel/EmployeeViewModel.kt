package com.example.kotlinapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinapp.domain.model.EmployeeCreate
import com.example.kotlinapp.domain.repository.EmployeeRepository
import com.example.kotlinapp.util.mapException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmployeeViewModel(
    private val employeeRepository: EmployeeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeeListUiState())
    val uiState: StateFlow<EmployeeListUiState> = _uiState.asStateFlow()

    init {
        loadEmployees()
    }

    fun loadEmployees() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val employees = employeeRepository.listEmployees()
                _uiState.update { it.copy(isLoading = false, employees = employees) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = mapException(e)) }
            }
        }
    }

    fun searchEmployees(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true, error = null) }
            try {
                val employees = employeeRepository.searchEmployees(query)
                _uiState.update { it.copy(isSearching = false, employees = employees) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSearching = false, error = mapException(e)) }
            }
        }
    }

    fun deleteEmployee(id: Long) {
        viewModelScope.launch {
            try {
                employeeRepository.deleteEmployee(id)
                loadEmployees()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = mapException(e)) }
            }
        }
    }

    fun createEmployee(create: EmployeeCreate, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                employeeRepository.createEmployee(create)
                onSuccess()
                loadEmployees()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = mapException(e)) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private val _photoState = MutableStateFlow(PhotoRegistrationUiState())
    val photoState: StateFlow<PhotoRegistrationUiState> = _photoState.asStateFlow()

    fun onPhotoCapture(photoBytes: ByteArray) {
        val currentStep = _photoState.value.currentStep
        _photoState.update {
            it.copy(
                capturedPhotos = it.capturedPhotos + (currentStep to photoBytes),
                currentStep = minOf(currentStep + 1, it.totalSteps - 1)
            )
        }
    }

    fun onSkipStep() {
        val currentStep = _photoState.value.currentStep
        if (currentStep < 3) return
        _photoState.update {
            it.copy(
                currentStep = minOf(currentStep + 1, it.totalSteps - 1)
            )
        }
    }

    fun resetPhotoState() {
        _photoState.value = PhotoRegistrationUiState()
    }

    fun uploadPhotos(create: com.example.kotlinapp.domain.model.EmployeeCreate, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val photos = _photoState.value.capturedPhotos.values.filterNotNull()
            if (photos.size < 3) {
                _photoState.update { it.copy(errorMessage = "Требуется минимум 3 фото") }
                return@launch
            }
            _photoState.update { it.copy(isUploading = true, errorMessage = null) }
            try {
                employeeRepository.registerWithPhotos(create, photos)
                _photoState.update { it.copy(isUploading = false, isSuccess = true) }
                onSuccess()
                loadEmployees()
            } catch (e: Exception) {
                _photoState.update { it.copy(isUploading = false, errorMessage = mapException(e)) }
            }
        }
    }
}
