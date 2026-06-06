package com.example.kotlinapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinapp.data.remote.ApiService
import com.example.kotlinapp.domain.repository.EmployeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val employeeRepository: com.example.kotlinapp.domain.repository.EmployeeRepository,
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadStats()
        checkServer()
    }

    fun loadStats() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingStats = true, statsError = null) }
            try {
                val stats = employeeRepository.getEmployeeStats()
                _uiState.update { it.copy(isLoadingStats = false, stats = stats) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingStats = false, statsError = com.example.kotlinapp.util.mapException(e)) }
            }
        }
    }

    fun checkServer() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingServer = true) }
            try {
                val connected = apiService.healthCheck()
                _uiState.update {
                    it.copy(
                        isLoadingServer = false,
                        serverStatus = if (connected) "Подключено" else "Не подключено"
                    )
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(isLoadingServer = false, serverStatus = "Не подключено") }
            }
        }
    }
}
