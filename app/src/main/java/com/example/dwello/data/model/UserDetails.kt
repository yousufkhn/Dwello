package com.example.dwello.data.model

data class UserDetails(
    val id: String,
    val email: String,
    val name: String,
    val profile_pic: String,
    val location: String,
    val posted_properties: List<String>,
    val created_at: String,
    val updated_at: String
)