package com.example.dwello.ui.components

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.example.dwello.navigation.Screens
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import com.example.dwello.R

data class BottomNavItem(
    val screen: Screens,
    val title: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screens.HOME, "Home", Icons.Rounded.Home),
    BottomNavItem(Screens.LIKED, "Liked", Icons.Outlined.FavoriteBorder),
    BottomNavItem(Screens.REQUESTS, "Requests", Icons.Outlined.MailOutline),
    BottomNavItem(Screens.RENTED, "Rented", Icons.Rounded.Lock)
)

@Composable
fun BottomNavBar(navController: NavController, currentRoute: String?) {
    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.screen.name,
                onClick = {
                    if (currentRoute != item.screen.name) {
                        navController.navigate(item.screen.name) {
                            popUpTo(Screens.HOME.name)
                            launchSingleTop = true
                        }
                    }
                },
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(item.title) }
            )
        }
    }
}
