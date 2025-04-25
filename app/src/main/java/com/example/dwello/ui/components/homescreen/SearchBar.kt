package com.example.dwello.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // --- Define Custom Colors from Hex Codes ---
    // Replace these hex codes with your actual desired colors
    val searchBarBackgroundColor = Color.White // Light Gray background
    val searchBarTextColor = Color(0xFF424242)       // Dark Gray text
    val searchBarPlaceholderColor = Color(0xFF757575) // Medium Gray placeholder/icons
    val searchBarAccentColor = Color(0xFF7b4bee)      // Purple accent

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        // Use a fully rounded shape (pill shape)
        shape = RoundedCornerShape(percent = 50),
        // Apply custom background color
        color = searchBarBackgroundColor,
        // tonalElevation removed as we're using custom colors
        shadowElevation = 2.dp // Keep shadow for depth
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChanged,
            modifier = Modifier.fillMaxSize(), // Fill the Surface
            placeholder = {
                Text(
                    "Search by title or location...",
                    style = MaterialTheme.typography.bodyLarge,
                    // Use custom placeholder color
                    color = searchBarPlaceholderColor
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon",
                    // Use custom placeholder/icon color
                    tint = searchBarPlaceholderColor
                )
            },
            // Add a clear button icon when text exists
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChanged("") /* Clear the query */ }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear Search",
                            // Use custom placeholder/icon color
                            tint = searchBarPlaceholderColor
                        )
                    }
                }
            },
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                // Explicitly define all colors using custom hex values
                containerColor = Color.Transparent, // Keep TextField container transparent
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = searchBarAccentColor, // Use accent color for cursor
                focusedLeadingIconColor = searchBarAccentColor, // Use accent for focused icon
                unfocusedLeadingIconColor = searchBarPlaceholderColor, // Use placeholder color for unfocused icon
                focusedTrailingIconColor = searchBarPlaceholderColor, // Keep clear button consistent
                unfocusedTrailingIconColor = searchBarPlaceholderColor, // Keep clear button consistent
                // Disabled colors can be set similarly if needed
                // disabledTextColor = ...,
                // disabledPlaceholderColor = ...,
            ),
        )
    }
}
