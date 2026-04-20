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
import com.example.kotlinapp.domain.model.AdminRegister
import com.example.kotlinapp.ui.buttons.LoginButton
import com.example.kotlinapp.ui.textfields.LoginTextField
import com.example.kotlinapp.ui.textfields.PasswordTextField
import com.example.kotlinapp.util.FormValidator
import com.example.kotlinapp.util.mapException
import kotlinx.coroutines.launch

class RegisterAdminScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val coroutineScope = rememberCoroutineScope()
        var username by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var inviteCode by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var fieldErrors by remember { mutableStateOf<Map<String, String?>>(emptyMap()) }

        val usernameHint by remember { derivedStateOf {
            if (username.isBlank()) "Минимум 3 символа, латиница, цифры, _"
            else null
        } }
        val emailHint by remember { derivedStateOf {
            if (email.isBlank()) "Например: user@example.com"
            else null
        } }
        val passwordHint by remember { derivedStateOf {
            if (password.isBlank()) "Минимум 8 символов, буквы, цифры и спецсимволы"
            else null
        } }
        val inviteHint by remember { derivedStateOf {
            if (inviteCode.isBlank()) "Получите код от администратора"
            else null
        } }

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
                    errorMessage = null
                    fieldErrors = fieldErrors - "username"
                }, enabled = !isLoading,
                supportingText = fieldErrors["username"] ?: usernameHint,
                isError = fieldErrors["username"] != null,
                modifier = Modifier.width(300.dp).padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = null
                    fieldErrors = fieldErrors - "email"
                },
                label = { Text("Email") },
                placeholder = { Text("Введите ваш email") },
                singleLine = true,
                enabled = !isLoading,
                supportingText = if (fieldErrors["email"] != null) { { Text(fieldErrors["email"]!!) } } else if (emailHint != null) { { Text(emailHint!!) } } else null,
                isError = fieldErrors["email"] != null,
                modifier = Modifier.width(300.dp).padding(bottom = 16.dp)
            )

            PasswordTextField(
                passwordValue = password, onPasswordChange = {
                    password = it
                    errorMessage = null
                    fieldErrors = fieldErrors - "password"
                }, enabled = !isLoading,
                supportingText = fieldErrors["password"] ?: passwordHint,
                isError = fieldErrors["password"] != null,
                modifier = Modifier.width(300.dp).padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = inviteCode,
                onValueChange = {
                    inviteCode = it
                    errorMessage = null
                    fieldErrors = fieldErrors - "inviteCode"
                },
                label = { Text("Код приглашения") },
                placeholder = { Text("Введите код приглашения") },
                singleLine = true,
                enabled = !isLoading,
                supportingText = if (fieldErrors["inviteCode"] != null) { { Text(fieldErrors["inviteCode"]!!) } } else if (inviteHint != null) { { Text(inviteHint!!) } } else null,
                isError = fieldErrors["inviteCode"] != null,
                modifier = Modifier.width(300.dp).padding(bottom = 16.dp)
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
                        errorMessage = null
                        return@LoginButton
                    }

                    coroutineScope.launch {
                        isLoading = true
                        errorMessage = null
                        try {
                            ServiceLocator.authRepository.register(
                                AdminRegister(
                                    username = username.trim(),
                                    email = email.trim(),
                                    password = password,
                                    inviteCode = inviteCode.trim()
                                )
                            )
                            navigator.replace(MainScreen())
                        } catch (e: Exception) {
                            errorMessage = mapException(e)
                        } finally {
                            isLoading = false
                        }
                    }
                },
                text = "Зарегистрироваться",
                loading = isLoading,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            TextButton(
                onClick = { navigator.pop() }, enabled = !isLoading
            ) {
                Text(text = "Уже есть аккаунт? Войти")
            }
        }
    }
}