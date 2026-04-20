package com.example.kotlinapp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.window.WindowPlacement

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "KotlinApp",
        state = rememberWindowState(placement = WindowPlacement.Maximized),
    ) {
        App()
    }
}