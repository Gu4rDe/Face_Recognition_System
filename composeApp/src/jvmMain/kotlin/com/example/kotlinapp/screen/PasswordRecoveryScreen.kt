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
import com.example.kotlinapp.domain.model.AdminResetPassword
import com.example.kotlinapp.ui.buttons.LoginButton
import com.example.kotlinapp.ui.textfields.LoginTextField
import com.example.kotlinapp.ui.textfields.PasswordTextField
import com.example.kotlinapp.util.FormValidator
import com.example.kotlinapp.util.mapException
import kotlinx.coroutines.launch

class PasswordRecoveryScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val coroutineScope = rememberCoroutineScope()
        var username by remember { mutableStateOf("") }
        var inviteCode by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var fieldErrors by remember { mutableStateOf<Map<String, String?>>(emptyMap()) }
        var successMessage by remember { mutableStateOf<String?>(null) }

        val usernameHint by remember { derivedStateOf {
            if (username.isBlank()) "Минимум 3 символа, латиница, цифры, _"
            else null
        } }
        val inviteHint by remember { derivedStateOf {
            if (inviteCode.isBlank()) "Получите код от администратора"
            else null
        } }
        val passwordHint by remember { derivedStateOf {
            if (newPassword.isBlank()) "Минимум 8 символов, буквы, цифры и спецсимволы"
            else null
        } }
        val confirmHint by remember { derivedStateOf {
            if (confirmPassword.isBlank()) "Повторите пароль"
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
                text = "Сброс пароля",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            LoginTextField(
                loginValue = username,
                onLoginChange = {
                    username = it
                    errorMessage = null
                    successMessage = null
                    fieldErrors = fieldErrors - "username"
                },
                enabled = !isLoading,
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
                    errorMessage = null
                    successMessage = null
                    fieldErrors = fieldErrors - "inviteCode"
                },
                label = { Text("Код приглашения") },
                placeholder = { Text("Введите код приглашения") },
                singleLine = true,
                enabled = !isLoading,
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
                    errorMessage = null
                    successMessage = null
                    fieldErrors = fieldErrors - "newPassword"
                },
                enabled = !isLoading,
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
                    errorMessage = null
                    successMessage = null
                    fieldErrors = fieldErrors - "confirmPassword"
                },
                enabled = !isLoading,
                supportingText = fieldErrors["confirmPassword"] ?: confirmHint,
                isError = fieldErrors["confirmPassword"] != null,
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

            successMessage?.let { message ->
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
                        errorMessage = null
                        return@LoginButton
                    }

                    coroutineScope.launch {
                        isLoading = true
                        errorMessage = null
                        successMessage = null
                        try {
                            ServiceLocator.authRepository.resetPassword(
                                AdminResetPassword(
                                    username = username.trim(),
                                    inviteCode = inviteCode.trim(),
                                    newPassword = newPassword
                                )
                            )
                            successMessage = "Пароль успешно сброшен! Теперь вы можете войти."
                            username = ""
                            inviteCode = ""
                            newPassword = ""
                            confirmPassword = ""
                        } catch (e: Exception) {
                            errorMessage = mapException(e)
                        } finally {
                            isLoading = false
                        }
                    }
                },
                text = "Сбросить пароль",
                loading = isLoading,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            TextButton(
                onClick = { navigator.pop() },
                enabled = !isLoading
            ) {
                Text(text = "Назад")
            }
        }
    }
}
