package com.example.kotlinapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.kotlinapp.ui.buttons.LoginButton
import com.example.kotlinapp.ui.textfields.LoginTextField
import com.example.kotlinapp.ui.textfields.PasswordTextField
import com.example.kotlinapp.util.FormValidator
import com.example.kotlinapp.viewmodel.PasswordRecoveryUiState
import com.example.kotlinapp.viewmodel.PasswordRecoveryViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PasswordRecoveryScreen(
    navController: NavHostController,
    viewModel: PasswordRecoveryViewModel = koinViewModel()
) {
    var uiState by remember { mutableStateOf(PasswordRecoveryUiState()) }
    var username by remember { mutableStateOf("") }
    var inviteCode by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var fieldErrors by remember { mutableStateOf<Map<String, String?>>(emptyMap()) }

    val usernameHint by remember {
        derivedStateOf {
            if (username.isBlank()) "Минимум 3 символа, латиница, цифры, _"
            else null
        }
    }
    val inviteHint by remember {
        derivedStateOf {
            if (inviteCode.isBlank()) "Получите код от администратора"
            else null
        }
    }
    val passwordHint by remember {
        derivedStateOf {
            if (newPassword.isBlank()) "Минимум 8 символов, буквы, цифры и спецсимволы"
            else null
        }
    }
    val confirmHint by remember {
        derivedStateOf {
            if (confirmPassword.isBlank()) "Повторите пароль"
            else null
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.uiState.collect { uiState = it }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Сброс пароля",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LoginTextField(
            loginValue = username,
            onLoginChange = {
                username = it
                viewModel.clearMessages()
                fieldErrors = fieldErrors - "username"
            },
            enabled = !uiState.isLoading,
            supportingText = fieldErrors["username"] ?: usernameHint,
            isError = fieldErrors["username"] != null,
            modifier = Modifier
                .width(300.dp)
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = inviteCode,
            onValueChange = {
                inviteCode = it
                viewModel.clearMessages()
                fieldErrors = fieldErrors - "inviteCode"
            },
            label = { Text("Код приглашения") },
            placeholder = { Text("Введите код приглашения") },
            singleLine = true,
            enabled = !uiState.isLoading,
            supportingText = if (fieldErrors["inviteCode"] != null) { { Text(fieldErrors["inviteCode"]!!) } } else if (inviteHint != null) { { Text(inviteHint!!) } } else null,
            isError = fieldErrors["inviteCode"] != null,
            modifier = Modifier
                .width(300.dp)
                .padding(bottom = 16.dp)
        )

        PasswordTextField(
            passwordValue = newPassword,
            onPasswordChange = {
                newPassword = it
                viewModel.clearMessages()
                fieldErrors = fieldErrors - "newPassword"
            },
            enabled = !uiState.isLoading,
            supportingText = fieldErrors["newPassword"] ?: passwordHint,
            isError = fieldErrors["newPassword"] != null,
            modifier = Modifier
                .width(300.dp)
                .padding(bottom = 16.dp)
        )

        PasswordTextField(
            passwordValue = confirmPassword,
            onPasswordChange = {
                confirmPassword = it
                viewModel.clearMessages()
                fieldErrors = fieldErrors - "confirmPassword"
            },
            enabled = !uiState.isLoading,
            supportingText = fieldErrors["confirmPassword"] ?: confirmHint,
            isError = fieldErrors["confirmPassword"] != null,
            modifier = Modifier
                .width(300.dp)
                .padding(bottom = 16.dp)
        )

        uiState.errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        uiState.successMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        LoginButton(
            onClick = {
                val usernameError = FormValidator.validateUsername(username)
                val inviteError = FormValidator.validateInviteCode(inviteCode)
                val passwordError = FormValidator.validatePassword(newPassword)
                val confirmError = FormValidator.validateConfirmPassword(newPassword, confirmPassword)
                val newErrors = mutableMapOf<String, String?>()
                if (usernameError != null) newErrors["username"] = usernameError
                if (inviteError != null) newErrors["inviteCode"] = inviteError
                if (passwordError != null) newErrors["newPassword"] = passwordError
                if (confirmError != null) newErrors["confirmPassword"] = confirmError
                fieldErrors = newErrors

                if (newErrors.isNotEmpty()) {
                    return@LoginButton
                }

                viewModel.resetPassword(username.trim(), inviteCode.trim(), newPassword)
            },
            text = "Сбросить пароль",
            loading = uiState.isLoading,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TextButton(
            onClick = { navController.popBackStack() },
            enabled = !uiState.isLoading
        ) {
            Text(text = "Назад")
        }
    }
}
