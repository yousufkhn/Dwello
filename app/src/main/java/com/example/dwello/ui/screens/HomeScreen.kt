package com.example.dwello.ui.screens

import LogoutButton
import android.content.Context
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.navigation.NavHostController
import com.example.dwello.utils.auth.GoogleAuthManager
import com.example.dwello.datastore.SharedPrefManager




@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    val googleAuthManager = remember {
        GoogleAuthManager(
            context = context,
            credentialManager = credentialManager,
            coroutineScope = coroutineScope,
            onSignInSuccess = {}, // Not needed here
            onSignInFailure = {}
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to Home Screen!")

        Spacer(modifier = Modifier.height(32.dp))

        LogoutButton(
            googleAuthManager = googleAuthManager,
            onLogout = {
                // Clear session
                val sharedPrefManager = SharedPrefManager(context)
                sharedPrefManager.clear()

                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            }
        )
    }
}
