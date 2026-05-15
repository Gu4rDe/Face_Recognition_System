package com.example.kotlinapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.kotlinapp.screen.LoginScreen
import com.example.kotlinapp.screen.MainScreen
import com.example.kotlinapp.screen.PasswordRecoveryScreen
import com.example.kotlinapp.screen.RegisterScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = AppScreen.Login) {
        composable<AppScreen.Login> {
            LoginScreen(navController)
        }
        composable<AppScreen.Register> {
            RegisterScreen(navController)
        }
        composable<AppScreen.PasswordRecovery> {
            PasswordRecoveryScreen(navController)
        }
        composable<AppScreen.Main> { backStackEntry ->
            val mainRoute = backStackEntry.toRoute<AppScreen.Main>()
            MainScreen(navController, initialSection = mainRoute.section)
        }
    }
}