package com.example.dwello.navigation

import android.provider.ContactsContract.Profile
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.dwello.ui.components.BottomNavBar
import com.example.dwello.ui.components.PropertyCard
import com.example.dwello.ui.screens.*
import com.example.dwello.viewmodel.AuthViewModel

@Composable
fun AppNavigation(navController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate(Screens.HOME.name) {
                popUpTo(Screens.LOGIN.name) { inclusive = true }
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (isLoggedIn && currentRoute in listOf(
                    Screens.HOME.name,
                    Screens.LIKED.name,
                    Screens.REQUESTS.name,
                    Screens.RENTED.name
                )
            ) {
                BottomNavBar(navController, currentRoute)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screens.LOGIN.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screens.LOGIN.name) {
                LoginScreen(navController)
            }
            composable(Screens.HOME.name) {
                Box(Modifier.fillMaxSize()) {
                    HomeScreen(navController)
                }
            }
            composable(Screens.LIKED.name) {
                Box(Modifier.fillMaxSize()) {
                    LikedPropertiesScreen(navController)
                }
            }
            composable(Screens.REQUESTS.name) {
                Box(Modifier.fillMaxSize()) {
                    RequestsScreen(navController)
                }
            }
            composable(Screens.RENTED.name) {
                Box(Modifier.fillMaxSize()) {
                    RentedPropertiesScreen(navController)
                }
            }
            composable(Screens.PROFILE.name) {
                Box(Modifier.fillMaxSize()) {
                    ProfileScreen(navController)
                }
            }
            composable("${Screens.PROPERTY_DETAILS.name}/{propertyJson}") { backStackEntry ->
                val propertyJson = backStackEntry.arguments?.getString("propertyJson")
                PropertyDetailsScreen(navController, propertyJson)
            }

        }
    }
}
