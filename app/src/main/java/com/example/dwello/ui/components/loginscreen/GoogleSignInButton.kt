package com.example.dwello.ui.components.loginscreen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dwello.ui.theme.Poppins
import com.example.dwello.ui.theme.PrimaryBlue

@Composable
fun GoogleSignInButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(16.dp),
    ) {
        Text(
            text = "Get Started",
            fontFamily = Poppins,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            color = PrimaryBlue
        )
    }
}