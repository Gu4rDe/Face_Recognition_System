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
import com.example.kotlinapp.ServiceLocator
import com.example.kotlinapp.domain.model.Employee
import com.example.kotlinapp.domain.model.FaceRecognitionResult
import com.example.kotlinapp.presentation.SettingsState
import com.example.kotlinapp.ui.components.FaceBoundingBoxOverlay
import com.example.kotlinapp.ui.dialogs.PhotoCaptureDialog
import com.example.kotlinapp.util.mapException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.runtime.rememberCoroutineScope
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import org.jetbrains.skia.Image as SkiaImage

@Composable
fun FaceRecognitionContent() {
    var photoBytes by remember { mutableStateOf<ByteArray?>(null) }
    var photoPreview by remember { mutableStateOf<ImageBitmap?>(null) }
    var sourceLabel by remember { mutableStateOf<String?>(null) }
    var showCameraDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<FaceRecognitionResult?>(null) }
    var employeeMap by remember { mutableStateOf<Map<Long, Employee>>(emptyMap()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val settingsState = remember { SettingsState() }
    val matchThreshold = settingsState.matchThreshold.value

    if (showCameraDialog) {
        PhotoCaptureDialog(onResult = { bytes ->
            showCameraDialog = false
            if (bytes != null) {
                photoBytes = bytes
                sourceLabel = "С камеры"
                try {
                    photoPreview = SkiaImage.makeFromEncoded(bytes).toComposeImageBitmap()
                } catch (_: Exception) {
                    photoPreview = null
                }
                result = null
                errorMessage = null
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
                        result = null
                        errorMessage = null
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
                        coroutineScope.launch {
                            isLoading = true
                            errorMessage = null
                            result = null
                            employeeMap = emptyMap()
                            try {
                                val recognitionResult = ServiceLocator.faceRecognitionRepository.recognizeFace(bytes)
                                result = recognitionResult
                                val matches = recognitionResult.results.flatMap { it.matches }
                                if (matches.isNotEmpty()) {
                                    val loaded = coroutineScope {
                                        val deferred = matches.map { match ->
                                            async {
                                                try {
                                                    val found = ServiceLocator.employeeRepository.searchEmployees(match.username)
                                                    found.firstOrNull { it.id == match.id }
                                                } catch (_: Exception) {
                                                    null
                                                }
                                            }
                                        }
                                        deferred.awaitAll().filterNotNull().associateBy { it.id }
                                    }
                                    employeeMap = loaded
                                }
                            } catch (e: Exception) {
                                errorMessage = mapException(e)
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                },
                enabled = photoBytes != null && !isLoading
            ) {
                if (isLoading) {
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
                    result?.let { res ->
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

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }

        errorMessage?.let { msg ->
            Text(msg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 8.dp))
        }

        result?.let { res ->
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
                                    val employee = employeeMap[match.id]
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