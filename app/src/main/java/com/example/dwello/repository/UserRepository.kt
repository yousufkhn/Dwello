package com.example.dwello.repository

import com.example.dwello.data.model.UserProfile
import com.example.dwello.data.network.ApiClient
import retrofit2.Response

class UserRepository {
    suspend fun registerUser(user: UserProfile): Response<Unit> {
        return ApiClient.apiService.registerUser(user)
    }
}