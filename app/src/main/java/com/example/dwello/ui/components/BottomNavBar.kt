package com.example.dwello.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dwello.navigation.Screens
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.MaterialTheme

// The primary theme color
val PurpleTheme = Color(0xFF7b4bee)

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
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.screen.name
            val scale by animateFloatAsState(
                targetValue = if (selected) 1.2f else 1.0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != item.screen.name) {
                        navController.navigate(item.screen.name) {
                            popUpTo(Screens.HOME.name)
                            launchSingleTop = true
                        }
                    }
                },
                icon = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(bottom = if (selected) 0.dp else 2.dp)
                    ) {
                        if (selected) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(PurpleTheme.copy(alpha = 0.1f), CircleShape)
                            )
                        }
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = if (selected) PurpleTheme else Color.Gray.copy(alpha = 0.6f),
                            modifier = Modifier
                                .size(24.dp)
                                .scale(scale)
                        )
                    }
                },
                label = {
                    AnimatedVisibility(
                        visible = selected,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Text(
                            text = item.title,
                            color = PurpleTheme,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedIconColor = PurpleTheme,
                    selectedTextColor = PurpleTheme,
                    unselectedIconColor = Color.Gray.copy(alpha = 0.6f),
                    unselectedTextColor = Color.Gray.copy(alpha = 0.6f)
                )
            )
        }
    }
}