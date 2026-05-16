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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kotlinapp.ui.icons.SettingsIcon
import com.example.kotlinapp.viewmodel.SettingsUiState
import com.example.kotlinapp.viewmodel.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsOverlay(
    viewModel: SettingsViewModel = koinViewModel(),
    content: @Composable () -> Unit
) {
    var uiState by remember { mutableStateOf(SettingsUiState()) }

    LaunchedEffect(viewModel) {
        viewModel.uiState.collect { uiState = it }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        content()

        IconButton(
            onClick = { viewModel.setShowSettings(true) },
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

    if (uiState.showSettings) {
        SettingsDialog(viewModel)
    }
}

@Composable
private fun SettingsDialog(viewModel: SettingsViewModel) {
    var uiState by remember { mutableStateOf(SettingsUiState()) }

    LaunchedEffect(viewModel) {
        viewModel.uiState.collect { uiState = it }
    }

    LaunchedEffect(Unit) {
        viewModel.checkServerConnection()
        viewModel.loadSettings()
    }

    AlertDialog(
        onDismissRequest = {
            viewModel.applySettings()
            viewModel.setShowSettings(false)
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
                        checked = uiState.isDarkTheme,
                        onCheckedChange = { viewModel.toggleTheme() }
                    )
                }

                OutlinedTextField(
                    value = uiState.apiUrl,
                    onValueChange = { viewModel.onApiUrlChanged(it) },
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
                    if (uiState.loadError != null) {
                        Text(
                            uiState.loadError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    if (uiState.saveError != null) {
                        Text(
                            uiState.saveError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Text(
                        "Порог совпадения: ${(uiState.matchThreshold * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Slider(
                        value = uiState.matchThreshold,
                        onValueChange = { viewModel.onMatchThresholdChanged(it) },
                        valueRange = 0f..1f,
                        steps = 98
                    )
                }

                OutlinedTextField(
                    value = uiState.cameraResolution,
                    onValueChange = { viewModel.onCameraResolutionChanged(it) },
                    label = { Text("Разрешение камеры (например, 640x480)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.cameraFps.toString(),
                    onValueChange = { viewModel.onCameraFpsChanged(it.toIntOrNull() ?: uiState.cameraFps) },
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
                        if (uiState.isCheckingServer) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        }
                        Text(
                            uiState.serverStatus,
                            style = MaterialTheme.typography.bodyMedium,
                            color = when (uiState.serverStatus) {
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
                viewModel.applySettings()
                viewModel.setShowSettings(false)
            }) {
                Text("Закрыть")
            }
        }
    )
}
