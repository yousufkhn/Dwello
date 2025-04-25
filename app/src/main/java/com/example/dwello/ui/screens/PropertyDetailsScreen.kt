package com.example.dwello.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.dwello.data.model.Property
import com.google.gson.Gson
import com.google.accompanist.pager.* // Keep accompanist pager imports

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class) // Added ExperimentalMaterial3Api OptIn for Scaffold
@Composable
fun PropertyDetailsScreen(navController: NavHostController, propertyJson: String?) {
    val property = remember(propertyJson) {
        propertyJson?.let { Gson().fromJson(it, Property::class.java) }
    }

    // Define the primary theme color
    val primaryColor = Color(0xFF7b4bee)
    val availableColor = Color(0xFF4CAF50) // Green for available
    val rentedColor = Color(0xFFFF9800) // Orange for rented

    if (property != null) {
        val pagerState = rememberPagerState()

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background // Use theme background
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp) // Apply padding from Scaffold
                    .verticalScroll(rememberScrollState())
            ) {
                // --- Image Carousel Section ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp) // Increased height for impact
                ) {
                    HorizontalPager(
                        count = property.pictures.size,
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        Image(
                            painter = rememberAsyncImagePainter(property.pictures[page]),
                            contentDescription = "Property Image ${page + 1}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop // Crop to fill the bounds
                        )
                    }

                    // Gradient overlay for indicator visibility
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                                )
                            )
                            .align(Alignment.BottomCenter)
                    )

                    HorizontalPagerIndicator(
                        pagerState = pagerState,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        activeColor = primaryColor,
                        inactiveColor = Color.White.copy(alpha = 0.7f)
                    )

                    // Optional: Back button overlay
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }

                // --- Main Info Section ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    // Title
                    Text(
                        text = property.title,
                        style = MaterialTheme.typography.headlineMedium, // Larger title
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Price
                    InfoRow(
                        icon = Icons.Default.MailOutline, // More relevant icon for price
                        text = "₹${property.price}",
                        textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold), // Make price stand out
                        iconColor = primaryColor
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Location
                    InfoRow(
                        icon = Icons.Default.LocationOn,
                        text = property.location,
                        iconColor = MaterialTheme.colorScheme.secondary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Status (Available/Rented)
                    InfoRow(
                        icon = if (property.is_rented) Icons.Default.Lock else Icons.Default.CheckCircle,
                        text = if (property.is_rented) "Currently Rented" else "Available for Rent",
                        iconColor = if (property.is_rented) rentedColor else availableColor,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = if (property.is_rented) rentedColor else availableColor
                        )
                    )
                }

                // --- Description Section ---
                SectionCard(title = "Description") {
                    Text(
                        property.description,
                        style = MaterialTheme.typography.bodyLarge, // Slightly larger body text
                        color = MaterialTheme.colorScheme.onSurfaceVariant // Good contrast on surfaceVariant
                    )
                }

                // --- Listed By Section ---
                SectionCard(title = "Listed By") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(property.owner_pic),
                            contentDescription = "Owner ${property.owner_name}",
                            modifier = Modifier
                                .size(56.dp) // Slightly larger picture
                                .clip(CircleShape), // Circular owner image
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                property.owner_name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                property.owner_email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary // Use secondary theme color for less emphasis
                            )
                        }
                    }
                }

                // Add some padding at the very bottom
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    } else {
        // Fallback if property is null
        Scaffold { paddingValues ->
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Property details not available.", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

// Helper Composable for sections like Description, Listed By
@Composable
fun SectionCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val primaryColor = Color(0xFF7b4bee) // Use the same primary color

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        shape = RoundedCornerShape(12.dp), // Consistent rounding
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant // Use a subtle background color from theme
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Subtle elevation
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = primaryColor, // Use primary color for section titles
                modifier = Modifier.padding(bottom = 12.dp)
            )
            content() // Inject the specific content for the section
        }
    }
}


// Updated InfoRow with more flexibility
@Composable
fun InfoRow(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    iconColor: Color = MaterialTheme.colorScheme.secondary, // Default to secondary color
    textStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyLarge // Default text style
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null, // Decorative icon
            tint = iconColor,
            modifier = Modifier.size(24.dp) // Slightly larger icons
        )
        Spacer(modifier = Modifier.width(12.dp)) // Increased spacing
        Text(
            text = text,
            style = textStyle
        )
    }
}