package com.example.dwello.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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

// The primary theme color
val PurpleTheme = Color(0xFF7b4bee)

enum class Screens {
    LOGIN,
    HOME,
    LIKED,
    REQUESTS,
    RENTED,
    PROFILE,
    PROPERTY_DETAILS,
    ADD_PROPERTY
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

    val showFab = showBottomBar
    val expandedFab = rememberSaveable { mutableStateOf(true) }

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
                    expanded = expandedFab.value,
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Property",
                            tint = Color.White
                        )
                    },
                    text = {
                        AnimatedVisibility(
                            visible = expandedFab.value,
                            enter = fadeIn() + expandHorizontally(),
                            exit = fadeOut() + shrinkHorizontally()
                        ) {
                            Text(
                                text = "Add a Property",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    containerColor = PurpleTheme,
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 12.dp,
                        hoveredElevation = 8.dp,
                        focusedElevation = 8.dp
                    ),
                    modifier = Modifier.shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = PurpleTheme.copy(alpha = 0.3f),
                        spotColor = PurpleTheme.copy(alpha = 0.4f)
                    )
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