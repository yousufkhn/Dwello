package com.example.dwello.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.dwello.ui.screens.HomeScreen
import com.example.dwello.ui.screens.LoginScreen
import com.example.dwello.viewmodel.AuthViewModel

@Composable
fun AppNavigation(navController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()
    val isLoggedIn = authViewModel.isLoggedIn.collectAsState()

    // Auto-login logic
    LaunchedEffect(isLoggedIn.value) {
        if (isLoggedIn.value) {
            navController.navigate(Screens.HOME.name) {
                popUpTo(Screens.LOGIN.name) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screens.LOGIN.name
    ) {
        composable(Screens.LOGIN.name) {
            LoginScreen(navController)
        }
        composable(Screens.HOME.name) {
            HomeScreen(navController)
        }
    }
}
