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
import androidx.compose.ui.unit.dp
import com.example.kotlinapp.ServiceLocator
import com.example.kotlinapp.domain.model.EmployeeStats
import com.example.kotlinapp.util.mapException

@Composable
fun DashboardContent() {
    var stats by remember { mutableStateOf<EmployeeStats?>(null) }
    var statsError by remember { mutableStateOf<String?>(null) }
    var isLoadingStats by remember { mutableStateOf(true) }

    var serverStatus by remember { mutableStateOf("Проверяется...") }
    var isLoadingServer by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoadingStats = true
        try {
            stats = ServiceLocator.employeeRepository.getEmployeeStats()
            statsError = null
        } catch (e: Exception) {
            statsError = mapException(e)
        } finally {
            isLoadingStats = false
        }
    }

    LaunchedEffect(Unit) {
        isLoadingServer = true
        try {
            if (ServiceLocator.apiService.healthCheck()) {
                serverStatus = "Подключено"
            } else {
                serverStatus = "Не подключено"
            }
        } catch (_: Exception) {
            serverStatus = "Не подключено"
        } finally {
            isLoadingServer = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Панель управления",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                title = "Всего",
                value = stats?.total,
                isLoading = isLoadingStats,
                error = statsError,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Активных",
                value = stats?.active,
                isLoading = isLoadingStats,
                error = statsError,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Неактивных",
                value = stats?.inactive,
                isLoading = isLoadingStats,
                error = statsError,
                modifier = Modifier.weight(1f)
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (serverStatus == "Подключено")
                    MaterialTheme.colorScheme.primaryContainer
                else if (serverStatus == "Не подключено")
                    MaterialTheme.colorScheme.errorContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Статус сервера", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (isLoadingServer) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    }
                    Text(
                        text = serverStatus,
                        style = MaterialTheme.typography.bodyLarge,
                        color = when (serverStatus) {
                            "Подключено" -> MaterialTheme.colorScheme.primary
                            "Не подключено" -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: Int?,
    isLoading: Boolean,
    error: String?,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                error != null -> Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                value != null -> Text(value.toString(), style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}