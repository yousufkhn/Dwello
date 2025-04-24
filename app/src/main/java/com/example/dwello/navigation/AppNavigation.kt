package com.example.dwello.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.dwello.ui.components.BottomNavBar
import com.example.dwello.ui.screens.*
import com.example.dwello.viewmodel.AuthViewModel

enum class Screens {
    LOGIN,
    HOME,
    LIKED,
    REQUESTS,
    RENTED,
    PROFILE,
    PROPERTY_DETAILS,
    ADD_PROPERTY  // Added new screen for property addition
}

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

    val showBottomBar = isLoggedIn && currentRoute in listOf(
        Screens.HOME.name,
        Screens.LIKED.name,
        Screens.REQUESTS.name,
        Screens.RENTED.name
    )

    val showFab = showBottomBar  // Show FAB on the same screens as the bottom bar

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController, currentRoute)
            }
        },
        floatingActionButton = {
            if (showFab) {
                ExtendedFloatingActionButton(
                    onClick = { navController.navigate(Screens.ADD_PROPERTY.name) },
                    icon = { Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Property") },
                    text = { Text(text = "Add a Property", fontSize = 14.sp) },
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                )
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
            composable(Screens.ADD_PROPERTY.name) {
                AddPropertyScreen(navController)
            }
        }
    }
}