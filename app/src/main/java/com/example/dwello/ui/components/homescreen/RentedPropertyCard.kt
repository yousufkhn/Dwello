package com.example.dwello.ui.components.homescreen

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.dwello.data.model.Property
import com.example.dwello.navigation.Screens
import com.google.gson.Gson

@Composable
fun RentedPropertyCard(
    property: Property,
    navController: NavHostController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                val propertyJson = Uri.encode(Gson().toJson(property))
                navController.navigate("${Screens.PROPERTY_DETAILS.name}/$propertyJson")
            },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // IMAGE
            Image(
                painter = rememberAsyncImagePainter(property.thumbnail),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            )

            // DETAILS
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = property.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C1C1E)
                )

                Text(
                    text = "₹${property.price}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0064FE),
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Text(
                    text = property.location,
                    fontSize = 16.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Owner:",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(
                        painter = rememberAsyncImagePainter(property.owner_pic),
                        contentDescription = "Owner Image",
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = property.owner_name,
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}
