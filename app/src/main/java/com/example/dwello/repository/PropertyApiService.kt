package com.example.dwello.repository

import com.example.dwello.data.model.PropertyPayload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

/**
 * Service to handle property API operations
 */
class PropertyApiService {
    private val client = OkHttpClient()
    private val baseUrl = "https://rjbcjks3-8080.inc1.devtunnels.ms/api"
    private val jsonMediaType = "application/json; charset=utf-8".toMediaTypeOrNull()

    /**
     * Creates a new property
     */
    suspend fun createProperty(payload: PropertyPayload, email: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            // Create JSON payload
            val jsonPayload = JSONObject().apply {
                put("title", payload.title)
                put("description", payload.description)
                put("price", payload.price)
                put("location", payload.location)
                put("thumbnail", payload.thumbnail)
                put("pictures", JSONObject.wrap(payload.pictures))
            }.toString()

            // Create request
            val requestBody = jsonPayload.toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url("$baseUrl/properties?email=$email")
                .post(requestBody)
                .build()

            // Execute request
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(IOException("API call failed with code ${response.code}"))
                }

                return@withContext Result.success(true)
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
}