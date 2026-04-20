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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.kotlinapp.ServiceLocator
import com.example.kotlinapp.ui.icons.DashboardIcon
import com.example.kotlinapp.ui.icons.FaceIcon
import com.example.kotlinapp.ui.icons.LogoutIcon
import com.example.kotlinapp.ui.icons.PeopleIcon

class MainScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var selectedItem by remember { mutableStateOf<MenuItem>(MenuItem.Dashboard) }

        Row(modifier = Modifier.fillMaxSize()) {
            SidebarRail(
                selectedItem = selectedItem,
                onSelectItem = { selectedItem = it },
                onLogout = {
                    ServiceLocator.authRepository.setToken(null)
                    navigator.pop()
                }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                when (selectedItem) {
                    MenuItem.Dashboard -> DashboardContent()
                    MenuItem.Employees -> EmployeeContent()
                    MenuItem.FaceRecognition -> FaceRecognitionContent()
                }
            }
        }
    }
}

@Composable
private fun SidebarRail(
    selectedItem: MenuItem,
    onSelectItem: (MenuItem) -> Unit,
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
                selected = selectedItem == MenuItem.Dashboard,
                onClick = { onSelectItem(MenuItem.Dashboard) },
                icon = { tint ->
                    DashboardIcon(modifier = Modifier.size(24.dp), tint = tint)
                }
            )

            SidebarItem(
                label = "Сотрудники",
                selected = selectedItem == MenuItem.Employees,
                onClick = { onSelectItem(MenuItem.Employees) },
                icon = { tint ->
                    PeopleIcon(modifier = Modifier.size(24.dp), tint = tint)
                }
            )

            SidebarItem(
                label = "Лица",
                selected = selectedItem == MenuItem.FaceRecognition,
                onClick = { onSelectItem(MenuItem.FaceRecognition) },
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