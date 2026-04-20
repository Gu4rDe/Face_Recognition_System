package com.example.kotlinapp.ui.textfields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.kotlinapp.ui.icons.VisibilityIcon
import com.example.kotlinapp.ui.icons.VisibilityOffIcon

@Composable
fun PasswordTextField(
    passwordValue: String,
    onPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    supportingText: String? = null,
    isError: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = passwordValue,
        onValueChange = onPasswordChange,
        label = { Text("Пароль") },
        placeholder = { Text("Введите ваш пароль") },
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                if (passwordVisible) {
                    VisibilityOffIcon(modifier = Modifier.size(24.dp))
                } else {
                    VisibilityIcon(modifier = Modifier.size(24.dp))
                }
            }
        },
        singleLine = true,
        enabled = enabled,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = if (passwordVisible) KeyboardType.Text else KeyboardType.Password,
            imeAction = ImeAction.Next
        ),
        supportingText = if (supportingText != null) { { Text(supportingText) } } else null,
        isError = isError,
        modifier = modifier.fillMaxWidth()
    )
}