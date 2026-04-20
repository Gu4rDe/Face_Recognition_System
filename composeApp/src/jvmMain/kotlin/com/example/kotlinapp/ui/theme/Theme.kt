package com.example.kotlinapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/* Светлая цветовая схема — используется по умолчанию */
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    error = Error,
)

/* Тёмная цветовая схема — активируется через переключатель в настройках */
private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    error = DarkError,
)

/**
 * Корневая тема приложения.
 * Оборачивает контент в MaterialTheme + Surface, чтобы тёмная тема
 * применялась ко всему окну, включая фон.
 *
 * @param darkTheme если true — используется тёмная палитра
 */
@Composable
fun KotlinAppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    /* Выбираем цветовую схему в зависимости от флага darkTheme */
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme
    ) {
        /* Surface заполняет весь экран фоном из текущей цветовой схемы,
           чтобы тёмная тема применялась ко всему окну */
        Surface(color = MaterialTheme.colorScheme.background) {
            content()
        }
    }
}