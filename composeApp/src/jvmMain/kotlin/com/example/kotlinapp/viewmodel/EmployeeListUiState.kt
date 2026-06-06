package com.example.kotlinapp.viewmodel

import com.example.kotlinapp.domain.model.Employee

data class EmployeeListUiState(
    val employees: List<Employee> = emptyList(),
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val error: String? = null
)
