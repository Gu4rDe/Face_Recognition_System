package com.example.kotlinapp.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.kotlinapp.navigation.AppScreen
import com.example.kotlinapp.ui.icons.DashboardIcon
import com.example.kotlinapp.ui.icons.FaceIcon
import com.example.kotlinapp.ui.icons.LogoutIcon
import com.example.kotlinapp.ui.icons.PeopleIcon
import com.example.kotlinapp.viewmodel.LoginViewModel
import org.koin.compose.viewmodel.koinViewModel

enum class MainSection {
    Dashboard, Employees, FaceRecognition
}

@Composable
fun MainScreen(
    navController: NavHostController,
    initialSection: String = "dashboard",
    loginViewModel: LoginViewModel = koinViewModel()
) {
    val initialEnum = when (initialSection) {
        "employees" -> MainSection.Employees
        "face_recognition" -> MainSection.FaceRecognition
        else -> MainSection.Dashboard
    }
    var currentSection by remember { mutableStateOf(initialEnum) }

    Row(modifier = Modifier.fillMaxSize()) {
        SidebarRail(
            selectedSection = currentSection,
            onSelectSection = { currentSection = it },
            onLogout = {
                loginViewModel.logout()
                navController.navigate(AppScreen.Login) {
                    popUpTo<AppScreen.Login> { inclusive = true }
                }
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .padding(24.dp)
        ) {
            when (currentSection) {
                MainSection.Dashboard -> DashboardContent()
                MainSection.Employees -> EmployeeContent()
                MainSection.FaceRecognition -> FaceRecognitionContent()
            }
        }
    }
}

@Composable
private fun SidebarRail(
    selectedSection: MainSection,
    onSelectSection: (MainSection) -> Unit,
    onLogout: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.width(80.dp).fillMaxHeight()
    ) {
        Column(
            modifier = Modifier.fillMaxHeight().padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            SidebarItem(
                label = "Панель",
                selected = selectedSection == MainSection.Dashboard,
                onClick = { onSelectSection(MainSection.Dashboard) },
                icon = { tint ->
                    DashboardIcon(modifier = Modifier.size(24.dp), tint = tint)
                }
            )

            SidebarItem(
                label = "Сотрудники",
                selected = selectedSection == MainSection.Employees,
                onClick = { onSelectSection(MainSection.Employees) },
                icon = { tint ->
                    PeopleIcon(modifier = Modifier.size(24.dp), tint = tint)
                }
            )

            SidebarItem(
                label = "Лица",
                selected = selectedSection == MainSection.FaceRecognition,
                onClick = { onSelectSection(MainSection.FaceRecognition) },
                icon = { tint ->
                    FaceIcon(modifier = Modifier.size(24.dp), tint = tint)
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))

            SidebarItem(
                label = "Выйти",
                selected = false,
                onClick = onLogout,
                icon = { _ ->
                    LogoutIcon(modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.error)
                },
                labelColor = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun SidebarItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable (tint: androidx.compose.ui.graphics.Color) -> Unit,
    labelColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    val itemColor = if (selected) MaterialTheme.colorScheme.primary else labelColor
    val iconTint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier
            .width(80.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        icon(if (selected) MaterialTheme.colorScheme.primary else iconTint)
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = itemColor,
            maxLines = 1
        )
    }
}