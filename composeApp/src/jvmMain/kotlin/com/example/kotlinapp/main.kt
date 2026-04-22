package com.example.kotlinapp

import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.skia.Image

fun loadAppIcon() = BitmapPainter(
    Image.makeFromEncoded(
        Thread.currentThread().contextClassLoader
            .getResourceAsStream("app.png")!!.readBytes()
    ).toComposeImageBitmap()
)

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Face Recognition System",
        icon = loadAppIcon(),
        state = rememberWindowState(placement = WindowPlacement.Maximized),
    ) {
        App()
    }
}