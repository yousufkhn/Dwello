package com.example.dwello.ui.screens

import LogoutButton
import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.dwello.datastore.SharedPrefManager
import com.example.dwello.ui.components.PropertyCard
import com.example.dwello.ui.components.homescreen.AnimatedPreloader
import com.example.dwello.ui.viewmodel.HomeViewModel
import com.example.dwello.utils.auth.GoogleAuthManager
import com.example.dwello.ui.components.SearchBar
import com.example.dwello.viewmodel.AuthViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {

    val locationOptions = listOf("All", "Mumbai", "Delhi", "Bangalore", "Hyderabad", "Chennai", "Kolkata", "Pune")
    val selectedLocations = remember { mutableStateListOf<String>() }



    val context = LocalContext.current
    val application = context.applicationContext as Application

    var searchQuery by remember { mutableStateOf("") }


    val viewModel: HomeViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(application) as T
        }
    })

    val userProfile by viewModel.userProfile.collectAsState()
    val propertyList by viewModel.propertyList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    val googleAuthManager = remember {
        GoogleAuthManager(
            context = context,
            credentialManager = credentialManager,
            coroutineScope = coroutineScope,
            onSignInSuccess = {}, // No-op
            onSignInFailure = {}
        )
    }

    Column(modifier = Modifier.fillMaxSize()
        .padding(16.dp)


    ) {
        userProfile?.let {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hi, ${it.name.split(" ").first()} 👋",
                    style = MaterialTheme.typography.titleLarge
                )

                Image(
                    painter = rememberAsyncImagePainter(it.profile_pic),
                    contentDescription = "User Profile",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape) // Makes image circular
                        .clickable {
                            navController.navigate("profile")
                        }
                )

            }
            SearchBar(
                query = searchQuery,
                onQueryChanged = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )


//            LogoutButton(
//                googleAuthManager = googleAuthManager,
//                onLogout = {
//                    SharedPrefManager(context).clear()
//                    navController.navigate("login") {
//                        popUpTo("home") { inclusive = true }
//                    }
//                }
//            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(locationOptions) { location ->
                val isSelected = location in selectedLocations || (location == "All" && selectedLocations.isEmpty())
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (location == "All") {
                            selectedLocations.clear()
                        } else {
                            if (selectedLocations.contains(location)) {
                                selectedLocations.remove(location)
                            } else {
                                selectedLocations.add(location)
                            }
                        }
                    },
                    label = { Text(location) }
                )
            }
        }

//        Button(
//            onClick = {
//                val sharedPrefManager = SharedPrefManager(context)
//                val userId = sharedPrefManager.getUserId()
//                Log.d("HomeScreen", "User ID: $userId")
//            },
//            modifier = Modifier.align(Alignment.End)
//        ) {
//            Text("Get User ID")
//        }


        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (errorMessage != null) {
            Text(
                text = errorMessage ?: "Unknown error",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            val filteredProperties by remember(propertyList, selectedLocations, searchQuery) {
                derivedStateOf {
                    propertyList.filter { property ->
                        val matchesLocation = selectedLocations.isEmpty() || selectedLocations.any {
                            property.location.trim().equals(it.trim(), ignoreCase = true)
                        }

                        val matchesQuery = searchQuery.isBlank() ||
                                property.title.contains(searchQuery, ignoreCase = true) ||
                                property.location.contains(searchQuery, ignoreCase = true)

                        matchesLocation && matchesQuery
                    }
                }
            }


            if (filteredProperties.isEmpty()) {
                Text("No properties match the selected location(s).", style = MaterialTheme.typography.bodyLarge)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box {
                        AnimatedPreloader(modifier = Modifier.size(200.dp).align(Alignment.Center))
                    }
                }
            }

            LazyColumn {
                items(filteredProperties) { property ->
                    PropertyCard(
                        property = property
                        , navController = navController
                    )
                }
            }
        }
    }
}
