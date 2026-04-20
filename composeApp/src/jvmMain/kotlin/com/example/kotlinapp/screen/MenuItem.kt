package com.example.kotlinapp.screen

sealed interface MenuItem {
    data object Dashboard : MenuItem
    data object Employees : MenuItem
    data object FaceRecognition : MenuItem
}
