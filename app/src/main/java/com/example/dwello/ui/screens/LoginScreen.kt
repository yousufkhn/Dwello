package com.example.dwello.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.navigation.NavController
import com.example.dwello.R
import com.example.dwello.ui.components.loginscreen.GoogleSignInButton
import com.example.dwello.ui.components.loginscreen.LoginHeader
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
            onSignInFailure = { /* Handle failure */ }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // 1. Background Image
        Image(
            painter = painterResource(id = R.drawable.login_bg), // Replace with your image
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Fading Effect Overlay (top transparent -> bottom dark)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.8f)),
                        startY = 500f // tweak this as needed
                    )
                )
        )

        // 3. Google Sign-In Button at Bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            GoogleSignInButton(
                onClick = { googleAuthManager.launchGoogleSignIn() }
            )
        }
    }
}