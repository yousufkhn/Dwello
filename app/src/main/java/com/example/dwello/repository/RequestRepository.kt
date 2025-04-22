package com.example.dwello.repository

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class RequestRepository {
    suspend fun handleRentalRequest(propertyId: String, renterId: String, action: String): Boolean {
        val url = "https://rjbcjks3-8080.inc1.devtunnels.ms/api/users/rental-requests/$propertyId/handle?renter_id=$renterId&action=$action"
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create(null, ByteArray(0))) // Empty POST
            .build()

        return try {
            val response = client.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

}