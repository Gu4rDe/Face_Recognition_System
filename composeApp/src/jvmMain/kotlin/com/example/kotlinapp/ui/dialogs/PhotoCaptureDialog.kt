package com.example.kotlinapp.ui.dialogs

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import com.example.kotlinapp.service.WebcamService
import kotlinx.coroutines.delay
import org.jetbrains.skia.Image as SkiaImage

private fun ByteArray?.toImageBitmapOrNull(): ImageBitmap? {
    if (this == null) return null
    return try {
        SkiaImage.makeFromEncoded(this).toComposeImageBitmap()
    } catch (_: Exception) {
        null
    }
}

@Composable
fun PhotoCaptureDialog(
    onResult: (ByteArray?) -> Unit
) {
    var capturedPhoto by remember { mutableStateOf<ByteArray?>(null) }
    var preview by remember { mutableStateOf<ImageBitmap?>(null) }
    var cameraError by remember { mutableStateOf(false) }
    var policyAccepted by remember { mutableStateOf(false) }
    var showPolicyDialog by remember { mutableStateOf(false) }
    var isStreaming by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val opened = WebcamService.open()
        if (!opened) {
            cameraError = true
        } else {
            isStreaming = true
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            WebcamService.close()
        }
    }

    LaunchedEffect(isStreaming, capturedPhoto) {
        while (isStreaming && capturedPhoto == null) {
            val frame = WebcamService.capture()
            if (frame != null) {
                    preview = frame.toImageBitmapOrNull()
                }
            delay(100)
        }
    }

    if (showPolicyDialog) {
        PolicyTextDialog(onDismiss = { showPolicyDialog = false })
    }

    AlertDialog(
        onDismissRequest = { onResult(null) },
        title = { Text("Создать фото") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (cameraError) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📷", style = MaterialTheme.typography.headlineLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Камера не найдена", style = MaterialTheme.typography.bodyLarge)
                        }
                    } else if (capturedPhoto != null) {
                        preview?.let { bitmap ->
                            Image(
                                bitmap = bitmap,
                                contentDescription = "Захваченное фото",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } else {
                        preview?.let { bitmap ->
                            Image(
                                bitmap = bitmap,
                                contentDescription = "Предпросмотр камеры",
                                modifier = Modifier.fillMaxSize()
                            )
                        } ?: run {
                            CircularProgressIndicator()
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (capturedPhoto == null && !cameraError) {
                        Button(onClick = {
                            capturedPhoto = WebcamService.capture()
                            if (capturedPhoto != null) {
                                isStreaming = false
                                preview = capturedPhoto.toImageBitmapOrNull()
                            }
                        }) {
                            Text("Захватить")
                        }
                    } else if (capturedPhoto != null) {
                        OutlinedButton(onClick = {
                            capturedPhoto = null
                            isStreaming = true
                        }) {
                            Text("Переснять")
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = policyAccepted,
                        onCheckedChange = { policyAccepted = it }
                    )
                    TextButton(onClick = { showPolicyDialog = true }) {
                        Text("Я принимаю Политику использования")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onResult(capturedPhoto) },
                enabled = capturedPhoto != null && policyAccepted
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = { onResult(null) }) {
                Text("Отмена")
            }
        }
    )
}

private fun loadPolicyText(): String {
    return try {
        val stream = PolicyTextDialog::class.java.classLoader.getResourceAsStream("policy.md")
        stream?.bufferedReader()?.use { it.readText() }
            ?: "Текст политики не найден."
    } catch (_: Exception) {
        "Не удалось загрузить текст политики."
    }
}

@Composable
private fun PolicyTextDialog(onDismiss: () -> Unit) {
    val policyText = remember { loadPolicyText() }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Политика использования") },
        text = {
            Column {
                Text(policyText, style = MaterialTheme.typography.bodyMedium)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Закрыть")
            }
        }
    )
}