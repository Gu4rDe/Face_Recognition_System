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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.kotlinapp.ServiceLocator
import com.example.kotlinapp.domain.model.Employee
import com.example.kotlinapp.domain.model.EmployeeCreate
import com.example.kotlinapp.util.mapException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.example.kotlinapp.ui.dialogs.PhotoCaptureDialog

@Composable
fun EmployeeContent() {
    val coroutineScope = rememberCoroutineScope()
    var employees by remember { mutableStateOf<List<Employee>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    var showAddDialog by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<Employee?>(null) }

    suspend fun loadEmployees() {
        isLoading = true
        error = null
        try {
            employees = ServiceLocator.employeeRepository.listEmployees()
        } catch (e: Exception) {
            error = mapException(e)
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { loadEmployees() }

    LaunchedEffect(searchQuery) {
        delay(300)
        if (searchQuery.isBlank()) {
            loadEmployees()
        } else {
            isSearching = true
            try {
                employees = ServiceLocator.employeeRepository.searchEmployees(searchQuery)
            } catch (e: Exception) {
                error = mapException(e)
            } finally {
                isSearching = false
            }
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

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 32.dp))
        } else if (error != null) {
            Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyLarge)
        } else if (employees.isEmpty()) {
            Text("Сотрудники не найдены", style = MaterialTheme.typography.bodyLarge)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(employees) { employee ->
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
            onDismiss = { showAddDialog = false },
            onAdded = {
                showAddDialog = false
                coroutineScope.launch { loadEmployees() }
            }
        )
    }

    deleteTarget?.let { employee ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Удаление сотрудника") },
            text = { Text("Удалить сотрудника «${employee.username}»?") },
            confirmButton = {
                TextButton(onClick = {
                    coroutineScope.launch {
                        try {
                            ServiceLocator.employeeRepository.deleteEmployee(employee.id)
                            deleteTarget = null
                            loadEmployees()
                        } catch (e: Exception) {
                            error = mapException(e)
                            deleteTarget = null
                        }
                    }
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
private fun AddEmployeeDialog(onDismiss: () -> Unit, onAdded: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var photoBytes by remember { mutableStateOf<ByteArray?>(null) }
    var showPhotoCapture by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    if (showPhotoCapture) {
        PhotoCaptureDialog(onResult = { bytes ->
            showPhotoCapture = false
            if (bytes != null) {
                photoBytes = bytes
            }
        })
    } else {
        AlertDialog(
            onDismissRequest = { if (!isLoading) onDismiss() },
            title = { Text("Добавить сотрудника") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = username, onValueChange = { username = it; errorMessage = null }, label = { Text("Имя пользователя *") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = email, onValueChange = { email = it; errorMessage = null }, label = { Text("Email *") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Телефон") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = department, onValueChange = { department = it }, label = { Text("Отдел") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = position, onValueChange = { position = it }, label = { Text("Должность") }, singleLine = true, modifier = Modifier.fillMaxWidth())

                    Button(
                        onClick = { showPhotoCapture = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (photoBytes != null) "Фото выбрано ✓" else "Создать фото")
                    }

                    errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (username.isBlank() || email.isBlank()) {
                            errorMessage = "Заполните обязательные поля: имя, email"
                            return@Button
                        }
                        if (photoBytes == null) {
                            errorMessage = "Создайте фотографию"
                            return@Button
                        }
                        coroutineScope.launch {
                            isLoading = true
                            errorMessage = null
                            try {
                                ServiceLocator.employeeRepository.createEmployee(
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
                                        photoBytes = photoBytes!!
                                    )
                                )
                                onAdded()
                            } catch (e: Exception) {
                                errorMessage = mapException(e)
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading
                ) {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    else Text("Создать")
                }
            },
            dismissButton = {
                TextButton(onClick = { if (!isLoading) onDismiss() }, enabled = !isLoading) { Text("Отмена") }
            }
        )
    }
}