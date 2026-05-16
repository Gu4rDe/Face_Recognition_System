package com.example.kotlinapp.viewmodel

data class PasswordRecoveryUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
