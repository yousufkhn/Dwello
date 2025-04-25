package com.example.dwello.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.dwello.datastore.SharedPrefManager
import com.example.dwello.ui.components.homescreen.AnimatedPreloader
import com.example.dwello.ui.components.homescreen.RentedPropertyCard
import com.example.dwello.viewmodel.RentedPropertiesViewModel

@Composable
fun RentedPropertiesScreen(navController: NavHostController) {
    val viewModel: RentedPropertiesViewModel = viewModel()
    val properties by viewModel.rentedProperties.collectAsState()
    val context = LocalContext.current

    // Fetch on first composition
    LaunchedEffect(Unit) {
        val sharedPrefManager = SharedPrefManager(context)
        sharedPrefManager.getUserId()?.let { userId ->
            Log.d("RentedPropertiesScreen", "Fetching rented properties for user: $userId")
            viewModel.fetchRentedProperties(userId)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "My rented properties",
            fontSize = 24.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (properties.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Box {
                    AnimatedPreloader(modifier = Modifier.size(200.dp).align(Alignment.Center))
                }
            }
        } else {

            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(properties) { property ->
                    RentedPropertyCard(property = property, navController = navController)
                }
            }
        }
    }
}
