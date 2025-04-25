package com.example.dwello.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class RequestRepository {
    suspend fun handleRentalRequest(propertyId: String, renterId: String, action: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://rjbcjks3-8080.inc1.devtunnels.ms/api/users/rental-requests/$propertyId/handle?renter_id=$renterId&action=$action"
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(url)
                    .post(RequestBody.create(null, ByteArray(0))) // Empty POST
                    .build()

                val response = client.newCall(request).execute()
                Log.d("RequestRepository", "Response code: ${response.code}")
                response.isSuccessful
            } catch (e: Exception) {
                Log.e("RequestRepository", "Error handling rental request", e)
                false
            }
        }
    }
}