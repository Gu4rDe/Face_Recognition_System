package com.example.kotlinapp.viewmodel

data class RegisterUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)
