package com.example.dwello.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.dwello.data.model.Property
import com.example.dwello.datastore.SharedPrefManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LikedPropertiesScreen(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPrefManager = SharedPrefManager(context)
    val userProfile = sharedPrefManager.getUserProfile()
    val userEmail = userProfile?.email ?: "tinyframes.contact@gmail.com" // Default if not found

    // State for liked properties
    var likedProperties by remember { mutableStateOf<List<Property>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Coroutine scope
    val coroutineScope = rememberCoroutineScope()

    // Function to fetch liked properties
    fun fetchLikedProperties() {
        isLoading = true
        error = null

        coroutineScope.launch {
            try {
                val client = OkHttpClient()
                val url = "https://rjbcjks3-8080.inc1.devtunnels.ms/api/properties/liked-properties/?email=$userEmail"

                val request = Request.Builder()
                    .url(url)
                    .get()
                    .build()

                withContext(Dispatchers.IO) {
                    client.newCall(request).execute().use { response ->
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                val responseBody = response.body?.string()
                                if (responseBody != null) {
                                    val gson = Gson()
                                    val typeToken = object : TypeToken<List<Property>>() {}.type
                                    likedProperties = gson.fromJson(responseBody, typeToken)
                                    Log.d("LikedPropertiesScreen", "Fetched ${likedProperties.size} properties")
                                }
                            } else {
                                error = "Failed to load properties: ${response.message}"
                                Log.e("LikedPropertiesScreen", "API error: ${response.code} ${response.message}")
                            }
                            isLoading = false
                        }
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    error = "Network error: ${e.message}"
                    isLoading = false
                    Log.e("LikedPropertiesScreen", "Network error", e)
                }
            }
        }
    }

    // Fetch properties on first composition
    LaunchedEffect(key1 = userEmail) {
        fetchLikedProperties()
    }

    // UI
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorites") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF006EFF),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Loading state
            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF006EFF),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading your favorites...",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            }

            // Error state
            else if (error != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Oops!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error ?: "Something went wrong",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { fetchLikedProperties() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF006EFF)
                        )
                    ) {
                        Text("Retry")
                    }
                }
            }

            // Empty state
            else if (likedProperties.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "No favorites",
                        modifier = Modifier.size(72.dp),
                        tint = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Favorites Yet",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Properties you like will appear here",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Content - List of properties
            else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    item {
                        Text(
                            text = "${likedProperties.size} Favorite ${if (likedProperties.size == 1) "Property" else "Properties"}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(likedProperties) { property ->
                        com.example.dwello.ui.components.homescreen.PropertyCard(
                            property = property,
                            navController = navController
                        )

                        // Callback for when a property is unliked directly from this screen
                        // This would require modification to PropertyCard to accept a callback
                        // Alternatively, you could implement pull-to-refresh functionality
                    }

                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }
}