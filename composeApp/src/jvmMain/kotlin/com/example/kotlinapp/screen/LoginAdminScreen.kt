package com.example.kotlinapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import com.example.kotlinapp.viewmodel.LoginUiState
import com.example.kotlinapp.viewmodel.LoginViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginViewModel = koinViewModel()
) {
    var uiState by remember { mutableStateOf(LoginUiState()) }
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fieldErrors by remember { mutableStateOf<Map<String, String?>>(emptyMap()) }

    val loginHint by remember {
        derivedStateOf {
            if (login.isBlank()) "Минимум 3 символа, латиница, цифры, _"
            else null
        }
    }
    val passwordHint by remember {
        derivedStateOf {
            if (password.isBlank()) "Минимум 8 символов, буквы, цифры и спецсимволы"
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
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Вход для администратора",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LoginTextField(
            loginValue = login,
            onLoginChange = {
                login = it
                viewModel.clearError()
                fieldErrors = fieldErrors - "login"
            },
            enabled = !uiState.isLoading,
            supportingText = fieldErrors["login"] ?: loginHint,
            isError = fieldErrors["login"] != null,
            modifier = Modifier
                .width(300.dp)
                .padding(bottom = 16.dp)
        )

        PasswordTextField(
            passwordValue = password,
            onPasswordChange = {
                password = it
                viewModel.clearError()
                fieldErrors = fieldErrors - "password"
            },
            enabled = !uiState.isLoading,
            supportingText = fieldErrors["password"] ?: passwordHint,
            isError = fieldErrors["password"] != null,
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

        LoginButton(
            onClick = {
                val loginError = FormValidator.validateUsername(login)
                val passwordError = FormValidator.validatePassword(password)
                val newErrors = mutableMapOf<String, String?>()
                if (loginError != null) newErrors["login"] = loginError
                if (passwordError != null) newErrors["password"] = passwordError
                fieldErrors = newErrors

                if (newErrors.isNotEmpty()) {
                    return@LoginButton
                }

                viewModel.login(login.trim(), password)
            },
            text = "Войти",
            loading = uiState.isLoading,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TextButton(
            onClick = { navController.navigate(AppScreen.Register) },
            enabled = !uiState.isLoading
        ) {
            Text(text = "Зарегистрироваться")
        }

        TextButton(
            onClick = { navController.navigate(AppScreen.PasswordRecovery) },
            enabled = !uiState.isLoading
        ) {
            Text(text = "Забыли пароль?")
        }

        Button(
            onClick = { navController.popBackStack() },
            enabled = !uiState.isLoading,
            modifier = Modifier
                .width(300.dp)
                .padding(top = 8.dp)
        ) {
            Text(text = "Назад")
        }
    }
}
