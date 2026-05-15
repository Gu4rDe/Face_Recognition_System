package com.example.kotlinapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.example.kotlinapp.navigation.AppNavGraph
import com.example.kotlinapp.presentation.SettingsState
import com.example.kotlinapp.ui.settings.SettingsOverlay
import com.example.kotlinapp.ui.theme.KotlinAppTheme

@Composable
fun App() {
    val settingsState = remember { SettingsState() }
    val navController = rememberNavController()

    KotlinAppTheme(darkTheme = settingsState.isDarkTheme.value) {
        SettingsOverlay(settingsState = settingsState) {
            AppNavGraph(navController)
        }
    }
}