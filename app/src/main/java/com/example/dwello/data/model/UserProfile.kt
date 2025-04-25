package com.example.dwello.data.model

data class UserProfile(
    val email: String,
    val name: String,
    val profile_pic: String,
    val location: String,
    val preferred_locations: List<String>
)