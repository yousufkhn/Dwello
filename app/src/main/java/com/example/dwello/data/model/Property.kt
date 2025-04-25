package com.example.dwello.data.model

import com.google.gson.annotations.SerializedName


data class Property(
    val id: String, // no @SerializedName needed since key is "id"
    val title: String,
    val description: String,
    val price: Int,
    val location: String,
    val owner_email: String,
    val owner_name: String,
    val owner_pic: String,
    val is_rented: Boolean,
    @SerializedName("rental_requests")
    val rental_requests: List<String> = emptyList(), // since it's a list of user ID strings
    val thumbnail: String,
    val pictures: List<String>,
    val created_at: String,
    val updated_at: String,
    @SerializedName("liked_by")
    val liked_by: List<String> = emptyList(), // optional, defaults to empty if missing
    @SerializedName("rented_by_id")
    val rented_by_id: String? = null // optional, could be null
)
