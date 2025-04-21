package com.example.dwello.data.network

import com.example.dwello.data.model.UserProfile
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/users/register") // Adjust endpoint as needed
    suspend fun registerUser(@Body user: UserProfile): Response<Unit>
}