package com.example.dwello.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.navigation.NavController
import com.example.dwello.utils.auth.GoogleAuthManager

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    val googleAuthManager = remember {
        GoogleAuthManager(
            context,
            credentialManager,
            coroutineScope,
            onSignInSuccess = { navController.navigate("home") },
            onSignInFailure = { /* Handle failure if needed */ }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { googleAuthManager.launchGoogleSignIn() },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Sign in with Google")
        }
    }
}

