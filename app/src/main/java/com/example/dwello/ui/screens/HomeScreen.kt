package com.example.dwello.ui.screens

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.dwello.ui.components.homescreen.PropertyCard
import com.example.dwello.ui.components.homescreen.AnimatedPreloader
import com.example.dwello.ui.viewmodel.HomeViewModel
import com.example.dwello.utils.auth.GoogleAuthManager
import com.example.dwello.ui.components.SearchBar
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

// Define our theme color (same as login screen)
val PrimaryPurple2 = Color(0xFF7b4bee)
val LightPurple2 = Color(0xFFa47bf1)
val DarkPurple2 = Color(0xFF5a35b0)
val BackgroundColor2 = Color(0xFFF8F5FF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val locationOptions = listOf("All", "Mumbai", "Delhi", "Bangalore", "Hyderabad", "Chennai", "Kolkata", "Pune")
    val selectedLocations = remember { mutableStateListOf<String>() }

    val context = LocalContext.current
    val application = context.applicationContext as Application

    var searchQuery by remember { mutableStateOf("") }

    val viewModel: HomeViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(application) as T
        }
    })

    val userProfile by viewModel.userProfile.collectAsState()
    val propertyList by viewModel.propertyList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val refreshing = rememberSwipeRefreshState(isLoading)

    val coroutineScope = rememberCoroutineScope()

    // Create the CredentialManager outside of remember
    val credentialManager = remember { CredentialManager.create(context) }

    // Now use DisposableEffect to handle its lifecycle
    DisposableEffect(key1 = context) {
        onDispose {
            // Clean up if needed when the composable leaves composition
        }
    }

    val googleAuthManager = GoogleAuthManager(
        context = context,
        credentialManager = credentialManager,
        coroutineScope = coroutineScope,
        onSignInSuccess = {}, // No-op
        onSignInFailure = {}
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BackgroundColor2, // Fixed variable name
                        Color.White
                    )
                )
            )
    ) {
        SwipeRefresh(
            state = refreshing,
            onRefresh = {
                viewModel.fetchProperties()
            }
        ) {
            // Filter properties based on selections and search query
            val filteredProperties by remember(propertyList, selectedLocations, searchQuery) {
                derivedStateOf {
                    propertyList.filter { property ->
                        val matchesLocation = selectedLocations.isEmpty() || selectedLocations.any {
                            property.location.trim().equals(it.trim(), ignoreCase = true)
                        }

                        val matchesQuery = searchQuery.isBlank() ||
                                property.title.contains(searchQuery, ignoreCase = true) ||
                                property.location.contains(searchQuery, ignoreCase = true)

                        matchesLocation && matchesQuery
                    }
                }
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    userProfile?.let {
                        Spacer(modifier = Modifier.height(16.dp))

                        // User profile header with gradient card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .shadow(
                                    elevation = 4.dp,
                                    shape = RoundedCornerShape(20.dp)
                                ),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Hi, ${it.name.split(" ").first()} 👋",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryPurple2 // Fixed variable name
                                        )
                                    )

                                    Text(
                                        text = "Find your perfect home",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color.Gray
                                        ),
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }

                                Surface(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .shadow(4.dp, CircleShape),
                                    shape = CircleShape,
                                    color = Color.White
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(it.profile_pic),
                                        contentDescription = "User Profile",
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .clickable {
                                                navController.navigate("profile")
                                            }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stylized search bar

                            SearchBar(
                                query = searchQuery,
                                onQueryChanged = { searchQuery = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                            )


                        Spacer(modifier = Modifier.height(24.dp))

                        // Location filters header
                        Text(
                            text = "Browse by location",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray
                            ),
                            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                        )
                    }
                }



                // Location filter chips with animation
                item {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(locationOptions) { location ->
                            val isSelected = location in selectedLocations || (location == "All" && selectedLocations.isEmpty())

                            // Scale animation on selection
                            val scale by animateFloatAsState(
                                targetValue = if (isSelected) 1.05f else 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )

                            Card(
                                modifier = Modifier
                                    .graphicsLayer {
                                        scaleX = scale
                                        scaleY = scale
                                    }
                                    .shadow(
                                        elevation = if (isSelected) 6.dp else 2.dp,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable {
                                        if (location == "All") {
                                            selectedLocations.clear()
                                        } else {
                                            if (selectedLocations.contains(location)) {
                                                selectedLocations.remove(location)
                                            } else {
                                                selectedLocations.add(location)
                                            }
                                        }
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) PrimaryPurple2 else Color.White // Fixed variable name
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (location == "All") Icons.Filled.Place else
                                            if (isSelected) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                        contentDescription = if (isSelected) "Selected" else "Not Selected",
                                        tint = if (isSelected) Color.White else PrimaryPurple2, // Fixed variable name
                                        modifier = Modifier.size(16.dp)
                                    )

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Text(
                                        text = location,
                                        color = if (isSelected) Color.White else Color.DarkGray,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Properties header
                item {
                    Text(
                        text = "Available Properties",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        ),
                        modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Loading, error or content states
                item {
                    if (isLoading && propertyList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = PrimaryPurple2, // Fixed variable name
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    } else if (errorMessage != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFEBEE)
                            )
                        ) {
                            Text(
                                text = errorMessage ?: "Unknown error",
                                color = Color(0xFFB71C1C),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }



                // No results state
                item {
                    AnimatedVisibility(
                        visible = filteredProperties.isEmpty() && !isLoading,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            AnimatedPreloader(modifier = Modifier.size(150.dp))

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "No properties match your criteria",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray
                                )
                            )
                        }
                    }
                }

                // Property list
                items(filteredProperties) { property ->

                        PropertyCard(
                            property = property,
                            navController = navController
                        )

                }

                // Add some bottom padding
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Show a small loading indicator at the top when refreshing
        if (isLoading && propertyList.isNotEmpty()) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = PrimaryPurple2 // Fixed variable name
            )
        }
    }
}