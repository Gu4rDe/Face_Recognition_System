package com.example.kotlinapp.screen

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kotlinapp.domain.model.Employee
import com.example.kotlinapp.domain.model.EmployeeCreate
import com.example.kotlinapp.service.WebcamService
import com.example.kotlinapp.ui.dialogs.PhotoCaptureDialog
import com.example.kotlinapp.viewmodel.EmployeeListUiState
import com.example.kotlinapp.viewmodel.EmployeeViewModel
import com.example.kotlinapp.viewmodel.PhotoRegistrationUiState
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EmployeeContent(
    viewModel: EmployeeViewModel = koinViewModel()
) {
    var uiState by remember { mutableStateOf(EmployeeListUiState()) }
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<Employee?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.uiState.collect { uiState = it }
    }

    LaunchedEffect(searchQuery) {
        delay(300)
        if (searchQuery.isBlank()) {
            viewModel.loadEmployees()
        } else {
            viewModel.searchEmployees(searchQuery)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Сотрудники",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { newQuery ->
                    searchQuery = newQuery
                },
                label = { Text("Поиск сотрудников") },
                placeholder = { Text("Введите имя или ID") },
                singleLine = true,
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            )
            Button(onClick = { showAddDialog = true }) {
                Text("Добавить")
            }
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 32.dp))
        } else if (uiState.error != null) {
            Text(uiState.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyLarge)
        } else if (uiState.employees.isEmpty()) {
            Text("Сотрудники не найдены", style = MaterialTheme.typography.bodyLarge)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.employees) { employee ->
                    EmployeeCard(
                        employee = employee,
                        onDelete = { deleteTarget = employee }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddEmployeeDialog(
            viewModel = viewModel,
            onDismiss = { showAddDialog = false }
        )
    }

    deleteTarget?.let { employee ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Удаление сотрудника") },
            text = { Text("Удалить сотрудника «${employee.username}»?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteEmployee(employee.id)
                    deleteTarget = null
                }) { Text("Удалить", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text("Отмена") } }
        )
    }
}

@Composable
private fun EmployeeCard(employee: Employee, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(employee.username, style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(employee.department ?: "—", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(employee.position ?: "—", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(
                    if (employee.isActive) "Активен" else "Неактивен",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (employee.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
            IconButton(onClick = onDelete) {
                Text("✕", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun AddEmployeeDialog(viewModel: EmployeeViewModel, onDismiss: () -> Unit) {
    var uiState by remember { mutableStateOf(EmployeeListUiState()) }
    var photoState by remember { mutableStateOf(PhotoRegistrationUiState()) }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var showPhotoCapture by remember { mutableStateOf(false) }
    val webcamService = remember { WebcamService() }

    LaunchedEffect(viewModel) {
        viewModel.uiState.collect { uiState = it }
    }
    LaunchedEffect(viewModel) {
        viewModel.photoState.collect { photoState = it }
    }

    if (showPhotoCapture) {
        PhotoCaptureDialog(webcamService = webcamService, onResult = { bytes ->
            showPhotoCapture = false
            if (bytes != null) {
                viewModel.onPhotoCapture(bytes)
            }
        })
    } else {
        AlertDialog(
            onDismissRequest = { if (!uiState.isLoading && !photoState.isUploading) onDismiss() },
            title = { Text("Добавить сотрудника") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Имя пользователя *") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email *") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Телефон") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = department, onValueChange = { department = it }, label = { Text("Отдел") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = position, onValueChange = { position = it }, label = { Text("Должность") }, singleLine = true, modifier = Modifier.fillMaxWidth())

                    HorizontalDivider()

                    Text("Фотографии: ${photoState.capturedCount}/5 (минимум 3)", style = MaterialTheme.typography.titleSmall)

                    ProgressIndicator(
                        currentStep = photoState.currentStep,
                        totalSteps = photoState.totalSteps,
                        capturedCount = photoState.capturedCount
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showPhotoCapture = true },
                            modifier = Modifier.weight(1f),
                            enabled = !photoState.isUploading
                        ) {
                            Text("Захватить")
                        }
                        if (photoState.isOptionalStep) {
                            OutlinedButton(
                                onClick = { viewModel.onSkipStep() },
                                modifier = Modifier.weight(1f),
                                enabled = !photoState.isUploading
                            ) {
                                Text("Пропустить")
                            }
                        }
                    }

                    photoState.errorMessage?.let { msg ->
                        Text(msg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (username.isBlank() || email.isBlank()) {
                            return@Button
                        }
                        viewModel.uploadPhotos(
                            EmployeeCreate(
                                employeeId = "EMP-${System.currentTimeMillis()}",
                                username = username.trim(),
                                email = email.trim(),
                                phone = phone.ifBlank { null },
                                department = department.ifBlank { null },
                                position = position.ifBlank { null },
                                location = null,
                                hireDate = null,
                                isActive = true,
                                accessEnabled = true,
                                photoBytes = byteArrayOf()
                            ),
                            onSuccess = { onDismiss() }
                        )
                    },
                    enabled = photoState.canUpload && username.isNotBlank() && email.isNotBlank() && !uiState.isLoading
                ) {
                    if (photoState.isUploading) CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    else Text("Загрузить (${photoState.capturedCount} фото)")
                }
            },
            dismissButton = {
                TextButton(onClick = { if (!uiState.isLoading && !photoState.isUploading) onDismiss() }, enabled = !uiState.isLoading && !photoState.isUploading) { Text("Отмена") }
            }
        )
    }
}

@Composable
private fun ProgressIndicator(currentStep: Int, totalSteps: Int, capturedCount: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        LinearProgressIndicator(
            progress = { (currentStep + 1).toFloat() / totalSteps },
            modifier = Modifier.fillMaxWidth().height(8.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Шаг ${currentStep + 1} из $totalSteps",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
