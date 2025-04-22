package com.example.dwello.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
    query: String, // The current search query
    onQueryChanged: (String) -> Unit, // The callback function for when the query changes
    modifier: Modifier = Modifier // Modifier for additional styling
) {
    Surface(
        tonalElevation = 4.dp,
        shadowElevation = 2.dp,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        TextField(
            value = query, // Display the current search query
            onValueChange = { onQueryChanged(it) }, // Callback to update the query
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search Icon") // Search icon on the left
            },
            placeholder = {
                Text("Search by title or location...", style = MaterialTheme.typography.bodyMedium) // Placeholder text
            },
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = MaterialTheme.colorScheme.surface,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxSize()
        )
    }
}
