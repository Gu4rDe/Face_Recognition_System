package com.example.kotlinapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kotlinapp.domain.model.FaceRecognitionResult
import com.example.kotlinapp.service.WebcamService
import com.example.kotlinapp.ui.components.FaceBoundingBoxOverlay
import com.example.kotlinapp.ui.dialogs.PhotoCaptureDialog
import com.example.kotlinapp.viewmodel.FaceRecognitionUiState
import com.example.kotlinapp.viewmodel.FaceRecognitionViewModel
import com.example.kotlinapp.viewmodel.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.runtime.rememberCoroutineScope
import org.jetbrains.skia.Image as SkiaImage
import org.koin.compose.viewmodel.koinViewModel
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun FaceRecognitionContent(
    viewModel: FaceRecognitionViewModel = koinViewModel()
) {
    var uiState by remember { mutableStateOf(FaceRecognitionUiState()) }
    val coroutineScope = rememberCoroutineScope()
    val settingsViewModel: SettingsViewModel = koinViewModel()
    var settingsUiState by remember { mutableStateOf(com.example.kotlinapp.viewmodel.SettingsUiState()) }
    LaunchedEffect(settingsViewModel) { settingsViewModel.uiState.collect { settingsUiState = it } }
    var photoBytes by remember { mutableStateOf<ByteArray?>(null) }
    var photoPreview by remember { mutableStateOf<ImageBitmap?>(null) }
    var sourceLabel by remember { mutableStateOf<String?>(null) }
    var showCameraDialog by remember { mutableStateOf(false) }
    val webcamService = remember { WebcamService() }
    val matchThreshold = settingsUiState.matchThreshold

    LaunchedEffect(viewModel) {
        viewModel.uiState.collect { uiState = it }
    }

    if (showCameraDialog) {
        PhotoCaptureDialog(webcamService = webcamService, onResult = { bytes ->
            showCameraDialog = false
            if (bytes != null) {
                photoBytes = bytes
                sourceLabel = "С камеры"
                try {
                    photoPreview = SkiaImage.makeFromEncoded(bytes).toComposeImageBitmap()
                } catch (_: Exception) {
                    photoPreview = null
                }
                viewModel.clearError()
            }
        })
    }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text(
            text = "Распознавание лиц",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                coroutineScope.launch {
                    val chosenFile = withContext(Dispatchers.IO) {
                        val chooser = JFileChooser()
                        chooser.fileFilter = FileNameExtensionFilter("Изображения", "jpg", "jpeg", "png")
                        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                            chooser.selectedFile
                        } else {
                            null
                        }
                    }
                    chosenFile?.let { file ->
                        try {
                            val bytes = withContext(Dispatchers.IO) { file.readBytes() }
                            photoPreview = SkiaImage.makeFromEncoded(bytes).toComposeImageBitmap()
                            photoBytes = bytes
                            sourceLabel = file.name
                        } catch (_: Exception) {
                            photoPreview = null
                            photoBytes = null
                            sourceLabel = null
                        }
                        viewModel.clearError()
                    }
                }
            }) {
                Text("Выбрать фото")
            }

            Button(onClick = {
                showCameraDialog = true
            }) {
                Text("Снять с камеры")
            }

            Button(
                onClick = {
                    photoBytes?.let { bytes ->
                        viewModel.recognize(bytes)
                    }
                },
                enabled = photoBytes != null && !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Распознать")
            }
        }

        sourceLabel?.let { label ->
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        photoPreview?.let { bitmap ->
            Spacer(modifier = Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    uiState.result?.let { res ->
                        if (res.results.isNotEmpty()) {
                            FaceBoundingBoxOverlay(
                                imageBitmap = bitmap,
                                faceResults = res.results,
                                matchThreshold = matchThreshold,
                                modifier = Modifier.height(300.dp).fillMaxWidth()
                            )
                        } else {
                            Image(
                                bitmap = bitmap,
                                contentDescription = "Предпросмотр",
                                modifier = Modifier.height(300.dp)
                            )
                        }
                    } ?: run {
                        Image(
                            bitmap = bitmap,
                            contentDescription = "Предпросмотр",
                            modifier = Modifier.height(300.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }

        uiState.errorMessage?.let { msg ->
            Text(msg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 8.dp))
        }

        uiState.result?.let { res ->
            Spacer(modifier = Modifier.height(8.dp))
            Text("Обнаружено лиц: ${res.facesDetected}", style = MaterialTheme.typography.titleMedium)

            if (res.results.isEmpty()) {
                Text("Лица не распознаны", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 8.dp))
            } else {
                res.results.forEachIndexed { index, faceResult ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Лицо ${index + 1}", style = MaterialTheme.typography.titleSmall)
                            if (faceResult.matches.isEmpty()) {
                                Text("Совпадений не найдено", style = MaterialTheme.typography.bodySmall)
                            } else {
                                faceResult.matches.forEach { match ->
                                    val employee = uiState.employeeMap[match.id]
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            employee?.username ?: match.username,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            "Сходство: ${(match.similarity * 100).toInt()}%",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    employee?.let { emp ->
                                        emp.position?.let { pos ->
                                            if (pos.isNotBlank()) {
                                                Text("Должность: $pos", style = MaterialTheme.typography.bodyMedium)
                                            }
                                        }
                                        emp.department?.let { dep ->
                                            if (dep.isNotBlank()) {
                                                Text("Отдел: $dep", style = MaterialTheme.typography.bodyMedium)
                                            }
                                        }
                                        emp.email.let { mail ->
                                            if (mail.isNotBlank()) {
                                                Text("Email: $mail", style = MaterialTheme.typography.bodyMedium)
                                            }
                                        }
                                        emp.phone?.let { ph ->
                                            if (ph.isNotBlank()) {
                                                Text("Телефон: $ph", style = MaterialTheme.typography.bodyMedium)
                                            }
                                        }
                                        emp.location?.let { loc ->
                                            if (loc.isNotBlank()) {
                                                Text("Расположение: $loc", style = MaterialTheme.typography.bodyMedium)
                                            }
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                if (emp.isActive) "Активен" else "Неактивен",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = if (emp.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                            )
                                            if (!emp.accessEnabled) {
                                                Text(
                                                    "Доступ отключён",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = MaterialTheme.colorScheme.error
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
