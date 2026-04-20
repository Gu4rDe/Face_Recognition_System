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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.kotlinapp.ServiceLocator
import com.example.kotlinapp.domain.model.AdminLogin
import com.example.kotlinapp.ui.buttons.LoginButton
import com.example.kotlinapp.ui.textfields.LoginTextField
import com.example.kotlinapp.ui.textfields.PasswordTextField
import com.example.kotlinapp.util.FormValidator
import com.example.kotlinapp.util.mapException
import kotlinx.coroutines.launch

class LoginAdminScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val coroutineScope = rememberCoroutineScope()
        var login by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var fieldErrors by remember { mutableStateOf<Map<String, String?>>(emptyMap()) }

        val loginHint by remember { derivedStateOf {
            if (login.isBlank()) "Минимум 3 символа, латиница, цифры, _"
            else null
        } }
        val passwordHint by remember { derivedStateOf {
            if (password.isBlank()) "Минимум 8 символов, буквы, цифры и спецсимволы"
            else null
        } }

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
                    errorMessage = null
                    fieldErrors = fieldErrors - "login"
                },
                enabled = !isLoading,
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
                    errorMessage = null
                    fieldErrors = fieldErrors - "password"
                },
                enabled = !isLoading,
                supportingText = fieldErrors["password"] ?: passwordHint,
                isError = fieldErrors["password"] != null,
                modifier = Modifier
                    .width(300.dp)
                    .padding(bottom = 16.dp)
            )

            errorMessage?.let { message ->
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
                        errorMessage = null
                        return@LoginButton
                    }

                    coroutineScope.launch {
                        isLoading = true
                        errorMessage = null
                        try {
                            ServiceLocator.authRepository.login(
                                AdminLogin(username = login.trim(), password = password)
                            )
                            navigator.replace(MainScreen())
                        } catch (e: Exception) {
                            errorMessage = mapException(e)
                        } finally {
                            isLoading = false
                        }
                    }
                },
                text = "Войти",
                loading = isLoading,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            TextButton(
                onClick = { navigator.push(RegisterAdminScreen()) },
                enabled = !isLoading
            ) {
                Text(text = "Зарегистрироваться")
            }

            Button(
                onClick = { navigator.pop() },
                enabled = !isLoading,
                modifier = Modifier
                    .width(300.dp)
                    .padding(top = 8.dp)
            ) {
                Text(text = "Назад")
            }
        }
    }
}