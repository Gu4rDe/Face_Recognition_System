package com.example.kotlinapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.example.kotlinapp.navigation.AppNavGraph
import com.example.kotlinapp.ui.settings.SettingsOverlay
import com.example.kotlinapp.ui.theme.KotlinAppTheme
import com.example.kotlinapp.viewmodel.SettingsUiState
import com.example.kotlinapp.viewmodel.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    val settingsViewModel: SettingsViewModel = koinViewModel()
    var settingsState by remember { mutableStateOf(SettingsUiState()) }

    LaunchedEffect(settingsViewModel) {
        settingsViewModel.uiState.collect { settingsState = it }
    }

    KotlinAppTheme(darkTheme = settingsState.isDarkTheme) {
        SettingsOverlay(viewModel = settingsViewModel) {
            AppNavGraph(rememberNavController())
        }
    }
}
