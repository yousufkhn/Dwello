package com.example.dwello.ui.screens

import LogoutButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.dwello.datastore.SharedPrefManager
import com.example.dwello.ui.viewmodel.HomeViewModel
import com.example.dwello.utils.auth.GoogleAuthManager
import androidx.credentials.CredentialManager
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Composable
fun ProfileScreen(navController: NavHostController, viewModel: HomeViewModel = viewModel()) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()

    val googleAuthManager = remember {
        GoogleAuthManager(
            context = context,
            credentialManager = CredentialManager.create(context),
            coroutineScope = CoroutineScope(Dispatchers.Main),
            onSignInSuccess = {},
            onSignInFailure = {}
        )
    }

    userProfile?.let { profile ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(WindowInsets.systemBars.asPaddingValues()) // respects system bars
            ,

            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(profile.profile_pic),
                contentDescription = "User Profile",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = profile.name, style = MaterialTheme.typography.titleLarge)
            Text(text = profile.email, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(32.dp))

            LogoutButton(
                googleAuthManager = googleAuthManager,
                onLogout = {
                    SharedPrefManager(context).clear()
                    navController.navigate("login") {
                        popUpTo("profile") { inclusive = true }
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
            Text("My Properties", style = MaterialTheme.typography.titleMedium)
            // Placeholder for future property list
        }
    }
}
