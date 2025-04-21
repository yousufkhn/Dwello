package com.example.dwello.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    val authViewModel: AuthViewModel = viewModel()

    var location by remember { mutableStateOf("") }
    var preferredLocation by remember { mutableStateOf("") }
    val registrationSuccess by authViewModel.registrationSuccess.collectAsState()

    // Handle navigation after API response
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
                    location = location,
                    preferred_location = preferredLocation
                )
                authViewModel.registerUser(userProfile)
                // log the payload
                Log.d("GoogleSignIn", "User: $userProfile")
            },
            onSignInFailure = {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()

            }
        )
    }

    // UI
    Box(modifier = Modifier.fillMaxSize()) {
        // Your background and overlay...

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Location input (you can replace with dropdowns too)
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Your Location") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = preferredLocation,
                onValueChange = { preferredLocation = it },
                label = { Text("Preferred Location") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            GoogleSignInButton(
                onClick = {
                    if (location.isNotBlank() && preferredLocation.isNotBlank()) {
                        googleAuthManager.launchGoogleSignIn()
                    } else {
                        // Show toast to enter locations
                    }
                }
            )
        }
    }
}
