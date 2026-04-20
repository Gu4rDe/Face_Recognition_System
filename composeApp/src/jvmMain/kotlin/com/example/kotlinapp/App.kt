package com.example.kotlinapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.example.kotlinapp.presentation.SettingsState
import com.example.kotlinapp.screen.HomeScreen
import com.example.kotlinapp.ui.settings.SettingsOverlay
import com.example.kotlinapp.ui.theme.KotlinAppTheme

/**
 * Корневой composable приложения.
 * Создаёт состояние настроек, оборачивает весь UI в тему и оверлей настроек.
 * Благодаря тому, что KotlinAppTheme оборачивает SettingsOverlay,
 * смена темы мгновенно применяется ко всем экранам.
 */
@Composable
fun App() {
    /* SettingsState переживает рекомпозиции, хранит тему, URL, статус сервера */
    val settingsState = remember { SettingsState() }

    KotlinAppTheme(darkTheme = settingsState.isDarkTheme.value) {
        /* SettingsOverlay рисует иконку шестерёнки поверх контента
           и показывает AlertDialog при нажатии */
        SettingsOverlay(settingsState = settingsState) {
            Navigator(HomeScreen()) { navigator ->
                SlideTransition(navigator)
            }
        }
    }
}