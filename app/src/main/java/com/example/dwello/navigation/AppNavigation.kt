package com.example.dwello.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.dwello.ui.screens.LoginScreen
import com.example.dwello.ui.screens.HomeScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screens.LOGIN.name) {
        composable(Screens.LOGIN.name) {
            LoginScreen(navController)
        }

        composable(Screens.HOME.name) {
            HomeScreen()
        }
    }
}
