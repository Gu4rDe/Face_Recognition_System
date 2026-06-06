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
import com.example.kotlinapp.navigation.AppScreen
import com.example.kotlinapp.ui.buttons.LoginButton
import com.example.kotlinapp.ui.textfields.LoginTextField
import com.example.kotlinapp.ui.textfields.PasswordTextField
import com.example.kotlinapp.util.FormValidator
import com.example.kotlinapp.viewmodel.RegisterUiState
import com.example.kotlinapp.viewmodel.RegisterViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterScreen(
    navController: NavHostController,
    viewModel: RegisterViewModel = koinViewModel()
) {
    var uiState by remember { mutableStateOf(RegisterUiState()) }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var inviteCode by remember { mutableStateOf("") }
    var fieldErrors by remember { mutableStateOf<Map<String, String?>>(emptyMap()) }

    val usernameHint by remember {
        derivedStateOf {
            if (username.isBlank()) "Минимум 3 символа, латиница, цифры, _"
            else null
        }
    }
    val emailHint by remember {
        derivedStateOf {
            if (email.isBlank()) "Например: user@example.com"
            else null
        }
    }
    val passwordHint by remember {
        derivedStateOf {
            if (password.isBlank()) "Минимум 8 символов, буквы, цифры и спецсимволы"
            else null
        }
    }
    val inviteHint by remember {
        derivedStateOf {
            if (inviteCode.isBlank()) "Получите код от администратора"
            else null
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.uiState.collect { uiState = it }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.resetSuccess()
            navController.navigate(AppScreen.Main()) {
                popUpTo<AppScreen.Login> { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Регистрация администратора",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LoginTextField(
            loginValue = username, onLoginChange = {
                username = it
                viewModel.clearError()
                fieldErrors = fieldErrors - "username"
            }, enabled = !uiState.isLoading,
            supportingText = fieldErrors["username"] ?: usernameHint,
            isError = fieldErrors["username"] != null,
            modifier = Modifier.width(300.dp).padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                viewModel.clearError()
                fieldErrors = fieldErrors - "email"
            },
            label = { Text("Email") },
            placeholder = { Text("Введите ваш email") },
            singleLine = true,
            enabled = !uiState.isLoading,
            supportingText = if (fieldErrors["email"] != null) { { Text(fieldErrors["email"]!!) } } else if (emailHint != null) { { Text(emailHint!!) } } else null,
            isError = fieldErrors["email"] != null,
            modifier = Modifier.width(300.dp).padding(bottom = 16.dp)
        )

        PasswordTextField(
            passwordValue = password, onPasswordChange = {
                password = it
                viewModel.clearError()
                fieldErrors = fieldErrors - "password"
            }, enabled = !uiState.isLoading,
            supportingText = fieldErrors["password"] ?: passwordHint,
            isError = fieldErrors["password"] != null,
            modifier = Modifier.width(300.dp).padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = inviteCode,
            onValueChange = {
                inviteCode = it
                viewModel.clearError()
                fieldErrors = fieldErrors - "inviteCode"
            },
            label = { Text("Код приглашения") },
            placeholder = { Text("Введите код приглашения") },
            singleLine = true,
            enabled = !uiState.isLoading,
            supportingText = if (fieldErrors["inviteCode"] != null) { { Text(fieldErrors["inviteCode"]!!) } } else if (inviteHint != null) { { Text(inviteHint!!) } } else null,
            isError = fieldErrors["inviteCode"] != null,
            modifier = Modifier.width(300.dp).padding(bottom = 16.dp)
        )

        uiState.errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        LoginButton(
            onClick = {
                val usernameError = FormValidator.validateUsername(username)
                val emailError = FormValidator.validateEmail(email)
                val passwordError = FormValidator.validatePassword(password)
                val inviteError = FormValidator.validateInviteCode(inviteCode)
                val newErrors = mutableMapOf<String, String?>()
                if (usernameError != null) newErrors["username"] = usernameError
                if (emailError != null) newErrors["email"] = emailError
                if (passwordError != null) newErrors["password"] = passwordError
                if (inviteError != null) newErrors["inviteCode"] = inviteError
                fieldErrors = newErrors

                if (newErrors.isNotEmpty()) {
                    return@LoginButton
                }

                viewModel.register(username.trim(), email.trim(), password, inviteCode.trim())
            },
            text = "Зарегистрироваться",
            loading = uiState.isLoading,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TextButton(
            onClick = { navController.popBackStack() }, enabled = !uiState.isLoading
        ) {
            Text(text = "Уже есть аккаунт? Войти")
        }
    }
}
