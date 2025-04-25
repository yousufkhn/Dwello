// RentalRequest.kt
package com.example.dwello.data.model

data class RentalRequestProperty(
    val _id: String,
    val title: String,
    val description: String,
    val price: Int,
    val location: String,
    val owner_email: String,
    val owner_name: String,
    val owner_pic: String,
    val is_rented: Boolean,
    val thumbnail: String,
    val rental_requests: List<String>,
    val requesting_users: List<RequestingUser>
)

data class RequestingUser(
    val _id: String,
    val name: String,
    val email: String,
    val location: String,
    val profile_pic: String
)
