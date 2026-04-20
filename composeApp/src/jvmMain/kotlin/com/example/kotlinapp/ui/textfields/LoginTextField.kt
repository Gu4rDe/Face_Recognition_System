package com.example.kotlinapp.ui.textfields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType


@Composable
fun LoginTextField(
    loginValue: String,
    onLoginChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    supportingText: String? = null,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = loginValue,
        onValueChange = onLoginChange,
        label = { Text("Логин") },
        placeholder = { Text("Введите ваш логин") },
        singleLine = true,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        supportingText = if (supportingText != null) { { Text(supportingText) } } else null,
        isError = isError,
        modifier = modifier.fillMaxWidth()
    )
}