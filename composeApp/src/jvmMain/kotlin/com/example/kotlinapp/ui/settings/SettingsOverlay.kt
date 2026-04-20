package com.example.kotlinapp.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kotlinapp.presentation.SettingsState
import com.example.kotlinapp.ui.icons.SettingsIcon

@Composable
fun SettingsOverlay(
    settingsState: SettingsState,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        content()

        IconButton(
            onClick = { settingsState.showSettings.value = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(48.dp)
        ) {
            SettingsIcon(
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (settingsState.showSettings.value) {
        SettingsDialog(settingsState)
    }
}

@Composable
private fun SettingsDialog(settingsState: SettingsState) {
    LaunchedEffect(Unit) {
        settingsState.checkServerConnection()
        settingsState.loadFaceRecognitionSettings()
    }

    AlertDialog(
        onDismissRequest = {
            settingsState.applySettings()
            settingsState.showSettings.value = false
        },
        title = { Text("Настройки") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Тёмная тема", style = MaterialTheme.typography.bodyLarge)
                    Switch(
                        checked = settingsState.isDarkTheme.value,
                        onCheckedChange = { settingsState.toggleTheme() }
                    )
                }

                OutlinedTextField(
                    value = settingsState.apiUrl.value,
                    onValueChange = { settingsState.onApiUrlChanged(it) },
                    label = { Text("API URL") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider()

                Text(
                    "Распознавание лиц",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Column {
                    if (settingsState.loadError.value != null) {
                        Text(
                            settingsState.loadError.value ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    if (settingsState.saveError.value != null) {
                        Text(
                            settingsState.saveError.value ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Text(
                        "Порог совпадения: ${(settingsState.matchThreshold.value * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Slider(
                        value = settingsState.matchThreshold.value,
                        onValueChange = { settingsState.matchThreshold.value = it },
                        valueRange = 0f..1f,
                        steps = 98
                    )
                }

                OutlinedTextField(
                    value = settingsState.cameraResolution.value,
                    onValueChange = { settingsState.cameraResolution.value = it },
                    label = { Text("Разрешение камеры (например, 640x480)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = settingsState.cameraFps.value.toString(),
                    onValueChange = { settingsState.cameraFps.value = it.toIntOrNull() ?: settingsState.cameraFps.value },
                    label = { Text("Частота кадров камеры (FPS)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider()

                Column {
                    Text(
                        "Системная информация",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Версия: 1.0.0",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Статус подключения: ",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (settingsState.isCheckingServer.value) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        }
                        Text(
                            settingsState.serverStatus.value,
                            style = MaterialTheme.typography.bodyMedium,
                            color = when (settingsState.serverStatus.value) {
                                "Подключено" -> MaterialTheme.colorScheme.primary
                                "Не подключено" -> MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                settingsState.applySettings()
                settingsState.showSettings.value = false
            }) {
                Text("Закрыть")
            }
        }
    )
}