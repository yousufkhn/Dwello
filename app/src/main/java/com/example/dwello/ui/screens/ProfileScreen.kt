package com.example.dwello.ui.screens

import LogoutButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.dwello.data.model.Property
import com.example.dwello.datastore.SharedPrefManager
import com.example.dwello.navigation.Screens
import com.example.dwello.ui.viewmodel.HomeViewModel
import com.example.dwello.utils.auth.GoogleAuthManager
import androidx.credentials.CredentialManager
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Define API interface here
interface PropertyApiService {
    @GET("api/users/{email}/posted-properties")
    suspend fun getUserPostedProperties(
        @Path("email") email: String,
        @Query("email") queryEmail: String
    ): List<Property>
}

@Composable
fun ProfileScreen(navController: NavHostController, viewModel: HomeViewModel = viewModel()) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()
    val postedProperties = remember { mutableStateOf<List<Property>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    // Create Retrofit instance
    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("https://rjbcjks3-8080.inc1.devtunnels.ms/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService = remember { retrofit.create(PropertyApiService::class.java) }

    val googleAuthManager = remember {
        GoogleAuthManager(
            context = context,
            credentialManager = CredentialManager.create(context),
            coroutineScope = CoroutineScope(Dispatchers.Main),
            onSignInSuccess = {},
            onSignInFailure = {}
        )
    }

    // Fetch user's posted properties
    LaunchedEffect(userProfile) {
        userProfile?.let { profile ->
            scope.launch {
                try {
                    val email = profile.email
                    // Direct API call
                    val properties = apiService.getUserPostedProperties(email, email)
                    postedProperties.value = properties
                    isLoading.value = false
                } catch (e: Exception) {
                    Log.e("ProfileScreen", "Error fetching properties", e)
                    errorMessage.value = "Failed to load properties: ${e.message}"
                    isLoading.value = false
                }
            }
        }
    }

    userProfile?.let { profile ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(WindowInsets.systemBars.asPaddingValues()),
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "My Properties",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                TextButton(onClick = {
                    // Navigate to add property screen or all properties list
                    navController.navigate(Screens.ADD_PROPERTY.name)
                }) {
                    Text("Add New")
                }
            }

            // Horizontal scrollable property cards
            when {
                isLoading.value -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                errorMessage.value != null -> {
                    Text(
                        text = errorMessage.value ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
                postedProperties.value.isEmpty() -> {
                    Text(
                        "You haven't posted any properties yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
                else -> {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        items(postedProperties.value) { property ->
                            ProfilePropertyCard(
                                property = property,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfilePropertyCard(
    property: Property,
    navController: NavHostController
) {
    var liked by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .width(280.dp)
            .padding(vertical = 8.dp)
            .clickable {
                val propertyJson = Uri.encode(Gson().toJson(property))
                navController.navigate("${Screens.PROPERTY_DETAILS.name}/$propertyJson")
            },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // IMAGE
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = rememberAsyncImagePainter(property.thumbnail),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                )

                // Status badge (rented or not)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(
                            color = if (property.is_rented) Color(0xFF4CAF50) else Color(0xFF2196F3),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (property.is_rented) "Rented" else "Available",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                IconButton(
                    onClick = { liked = !liked },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.White.copy(alpha = 0.9f), CircleShape)
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = if (liked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (liked) Color.Red else Color.Gray
                    )
                }
            }

            // DETAILS
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = property.title,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3A3A3A)
                    ),
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "₹${property.price}",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0064FE)
                    )
                )

                Text(
                    text = property.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}