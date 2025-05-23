package com.example.dwello.ui.components.homescreen

import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.dwello.data.model.Property
import com.example.dwello.datastore.SharedPrefManager
import com.example.dwello.navigation.Screens
import com.example.dwello.ui.viewmodel.HomeViewModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

@Composable
fun PropertyCard(
    property: Property,
    navController: NavHostController
) {
    val context = LocalContext.current
    val sharedPrefManager = SharedPrefManager(context)
    val homeViewModel: HomeViewModel = viewModel()
    val coroutineScope = rememberCoroutineScope()
    val client = OkHttpClient()

    // Get user email from SharedPreferences
    val userProfile = sharedPrefManager.getUserProfile()
    val userEmail = userProfile?.email ?: "khanyousuf2144@gmail.com" // Default email if not found

    // Check if property is liked from SharedPreferences
    val likeKey = "property_like_${property.id}"
    var liked by remember {
        mutableStateOf(sharedPrefManager.prefs.getBoolean(likeKey, false))
    }
    var requestSent by remember { mutableStateOf(false) }

    fun toggleLike() {
        coroutineScope.launch {
            try {
                val isLiking = !liked
                val endpoint = if (isLiking) "like" else "unlike"
                val url = "https://rjbcjks3-8080.inc1.devtunnels.ms/api/properties/${property.id}/$endpoint?email=$userEmail"

                val request = Request.Builder()
                    .url(url)
                    .post(okhttp3.RequestBody.create(null, ByteArray(0)))
                    .build()

                withContext(Dispatchers.IO) {
                    client.newCall(request).execute().use { response ->
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                liked = isLiking
                                // Save like state to SharedPreferences
                                sharedPrefManager.prefs.edit().putBoolean(likeKey, liked).apply()

                                val message = if (isLiking) "Property added to favorites" else "Property removed from favorites"
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Failed to update like status", Toast.LENGTH_SHORT).show()
                                Log.e("PropertyCard", "API error: ${response.code} ${response.message}")
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("PropertyCard", "Network error", e)
                }
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
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
                        .height(200.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                )

                IconButton(
                    onClick = { toggleLike() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.White.copy(alpha = 0.9f), CircleShape)
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = if (liked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (liked) "Unlike" else "Like",
                        tint = if (liked) Color.Red else Color.Gray
                    )
                }
            }

            // DETAILS
            Column(modifier = Modifier.padding(16.dp)) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = property.title,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3A3A3A)
                    )
                )

                Text(
                    text = "₹${property.price}/month",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0064FE)
                    )
                )

                Text(
                    text = property.location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Owner : ",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Image(
                        painter = rememberAsyncImagePainter(property.owner_pic),
                        contentDescription = "Owner Profile",
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(24.dp)
                            .clip(CircleShape)
                    )
                    Text(
                        text = property.owner_name,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val userId = sharedPrefManager.getUserId()

                        if (userId != null) {
                            homeViewModel.requestRent(
                                id = property.id,
                                userId = userId,
                                onSuccess = {
                                    requestSent = true
                                    Toast.makeText(context, "Rent request sent!", Toast.LENGTH_SHORT).show()
                                },
                                onError = {
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    },
                    enabled = !requestSent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (requestSent) Color.Gray else Color(0xFF006EFF)
                    )
                ) {
                    Text(
                        text = if (requestSent) "Request Sent" else "Request for Lease",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

// Extension property to make SharedPrefManager.prefs accessible
val SharedPrefManager.prefs: SharedPreferences
    get() = this.javaClass.getDeclaredField("prefs").apply { isAccessible = true }.get(this) as SharedPreferences