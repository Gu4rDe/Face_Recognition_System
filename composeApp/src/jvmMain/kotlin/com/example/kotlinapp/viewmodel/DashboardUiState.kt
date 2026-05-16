package com.example.kotlinapp.viewmodel

import com.example.kotlinapp.domain.model.EmployeeStats

data class DashboardUiState(
    val stats: EmployeeStats? = null,
    val statsError: String? = null,
    val isLoadingStats: Boolean = false,
    val serverStatus: String = "Проверяется...",
    val isLoadingServer: Boolean = true
)
