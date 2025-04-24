package com.example.dwello.data.model

data class PropertyPayload(
    val title: String,
    val description: String,
    val price: Int,
    val location: String,
    val thumbnail: String,
    val pictures: List<String>
)
