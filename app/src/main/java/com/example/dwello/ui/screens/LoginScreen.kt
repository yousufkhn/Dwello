package com.example.dwello.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dwello.data.model.UserProfile
import com.example.dwello.ui.components.loginscreen.GoogleSignInButton
import com.example.dwello.utils.auth.GoogleAuthManager
import com.example.dwello.utils.auth.GoogleUser
import com.example.dwello.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

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

    // UI
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Location Dropdown
            ExposedDropdownMenuBox(
                expanded = showLocationDropdown,
                onExpandedChange = { showLocationDropdown = !showLocationDropdown }
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = selectedLocation ?: "Choose Your Location",
                    onValueChange = {},
                    label = { Text("Your Location") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showLocationDropdown) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = showLocationDropdown,
                    onDismissRequest = { showLocationDropdown = false }
                ) {
                    locationOptions.forEach { location ->
                        DropdownMenuItem(
                            text = { Text(location) },
                            onClick = {
                                selectedLocation = location
                                showLocationDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Preferred Locations Dialog Trigger
            OutlinedButton(
                onClick = { showPreferredDialog = true },
                enabled = selectedLocation != null
            ) {
                Text("Choose Preferred Locations")
            }

            // Preferred Locations Chips
            LazyRow(modifier = Modifier.padding(top = 8.dp)) {
                items(selectedPreferredLocations.size) { index ->
                    AssistChip(
                        onClick = { },
                        label = { Text(selectedPreferredLocations[index]) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove",
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable {
                                        selectedPreferredLocations.removeAt(index)
                                    }
                            )
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Show Google Sign In only if at least one preferred location is selected
            if (selectedLocation != null && selectedPreferredLocations.isNotEmpty()) {
                GoogleSignInButton(onClick = {
                    googleAuthManager.launchGoogleSignIn()
                })
            }
        }
    }

    // Preferred Location Selection Dialog
    if (showPreferredDialog) {
        AlertDialog(
            onDismissRequest = { showPreferredDialog = false },
            confirmButton = {
                TextButton(onClick = { showPreferredDialog = false }) {
                    Text("Done")
                }
            },
            title = { Text("Select Preferred Locations") },
            text = {
                Column {
                    locationOptions.forEach { location ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (location in selectedPreferredLocations)
                                        selectedPreferredLocations.remove(location)
                                    else
                                        selectedPreferredLocations.add(location)
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = location in selectedPreferredLocations,
                                onCheckedChange = null // clickable handles it
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(location)
                        }
                    }
                }
            }
        )
    }
}
