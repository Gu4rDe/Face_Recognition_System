package com.example.kotlinapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinapp.domain.model.AdminResetPassword
import com.example.kotlinapp.domain.repository.AuthRepository
import com.example.kotlinapp.util.mapException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PasswordRecoveryViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PasswordRecoveryUiState())
    val uiState: StateFlow<PasswordRecoveryUiState> = _uiState.asStateFlow()

    fun resetPassword(username: String, inviteCode: String, newPassword: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            try {
                authRepository.resetPassword(
                    AdminResetPassword(
                        username = username,
                        inviteCode = inviteCode,
                        newPassword = newPassword
                    )
                )
                _uiState.update { it.copy(isLoading = false, successMessage = "Пароль успешно сброшен! Теперь вы можете войти.") }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = mapException(e)) }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
