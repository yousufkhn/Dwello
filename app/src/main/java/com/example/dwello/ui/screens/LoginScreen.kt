package com.example.dwello.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dwello.R
import com.example.dwello.data.model.UserProfile
import com.example.dwello.ui.components.loginscreen.GoogleSignInButton
import com.example.dwello.utils.auth.GoogleAuthManager
import com.example.dwello.utils.auth.GoogleUser
import com.example.dwello.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.Shader

// Define our theme color
val PrimaryPurple = Color(0xFF7b4bee)
val LightPurple = Color(0xFFa47bf1)
val DarkPurple = Color(0xFF5a35b0)
val BackgroundColor = Color(0xFFF8F5FF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    val authViewModel: AuthViewModel = viewModel()
    val registrationSuccess by authViewModel.registrationSuccess.collectAsState()

    // Sample cities
    val locationOptions = listOf("Mumbai", "Delhi", "Bangalore", "Hyderabad", "Chennai", "Kolkata", "Pune")

    var selectedLocation by remember { mutableStateOf<String?>(null) }
    var showLocationDropdown by remember { mutableStateOf(false) }

    var showPreferredDialog by remember { mutableStateOf(false) }
    val selectedPreferredLocations = remember { mutableStateListOf<String>() }

    LaunchedEffect(registrationSuccess) {
        if (registrationSuccess == true) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    val googleAuthManager = remember {
        GoogleAuthManager(
            context,
            credentialManager,
            coroutineScope,
            onSignInSuccess = { user: GoogleUser ->
                val userProfile = UserProfile(
                    email = user.email,
                    name = user.name,
                    profile_pic = user.profilePicUrl,
                    location = selectedLocation ?: "",
                    preferred_locations = selectedPreferredLocations.toList()
                )
                authViewModel.registerUser(userProfile)
            },
            onSignInFailure = {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Background gradient
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.login_background), // Replace with your image
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
//                .graphicsLayer {
////                    alpha = 0.55f // Required for blur to apply properly
////                    renderEffect = RenderEffect
////                        .createBlurEffect(20f, 20f, Shader.TileMode.CLAMP)
//                }
        )

        // Overlay gradient (optional)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            BackgroundColor.copy(alpha = 0.85f),
                            Color.White.copy(alpha = 1f)
                        )
                    )
                )
        )

        // App Logo/Title at the top
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Logo or App name with animation
            Text(
                text = "Dwello",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryPurple
            )

            Text(
                text = "Find your perfect stay",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Current Location Section
            LocationSelectionSection(
                locationOptions = locationOptions,
                selectedLocation = selectedLocation,
                onLocationSelected = { selectedLocation = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Preferred Locations Section with horizontal scrolling
            Text(
                text = "Where would you like to explore?",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(12.dp))

            PreferredLocationsSelector(
                locationOptions = locationOptions.filter { it != selectedLocation },
                selectedLocations = selectedPreferredLocations
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Continue Button
            if (selectedLocation != null && selectedPreferredLocations.isNotEmpty()) {
                Button(
                    onClick = { googleAuthManager.launchGoogleSignIn() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryPurple,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Continue with Google",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray,
                        contentColor = Color.White
                    ),
                    enabled = false
                ) {
                    Text(
                        text = "Select location & preferences first",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun LocationSelectionSection(
    locationOptions: List<String>,
    selectedLocation: String?,
    onLocationSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "Your Current Location",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.DarkGray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = "Location",
                    tint = PrimaryPurple,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = selectedLocation ?: "Select your location",
                    color = if (selectedLocation != null) Color.Black else Color.Gray,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand",
                    tint = PrimaryPurple
                )
            }
        }
    }

    // Location Dropdown Menu
    AnimatedVisibility(
        visible = expanded,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                locationOptions.forEach { location ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onLocationSelected(location)
                                expanded = false
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val isSelected = location == selectedLocation

                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) PrimaryPurple else Color.LightGray.copy(alpha = 0.3f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = location,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PreferredLocationsSelector(
    locationOptions: List<String>,
    selectedLocations: MutableList<String>
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(locationOptions) { location ->
            val isSelected = location in selectedLocations

            // Create a scale animation when the item is selected or deselected
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.05f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )

            val backgroundColor = if (isSelected) {
                PrimaryPurple
            } else {
                Color.White
            }

            val textColor = if (isSelected) {
                Color.White
            } else {
                Color.DarkGray
            }

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
                        if (isSelected) {
                            selectedLocations.remove(location)
                        } else {
                            selectedLocations.add(location)
                        }
                    },
                colors = CardDefaults.cardColors(
                    containerColor = backgroundColor
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isSelected) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = if (isSelected) "Selected" else "Not Selected",
                        tint = if (isSelected) Color.White else PrimaryPurple,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = location,
                        color = textColor,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}