package com.example.kotlinapp.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class AppScreen {

    @Serializable
    data object Login : AppScreen()

    @Serializable
    data object Register : AppScreen()

    @Serializable
    data object PasswordRecovery : AppScreen()

    @Serializable
    data class Main(val section: String = "dashboard") : AppScreen()
}