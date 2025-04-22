package com.example.dwello.repository

import com.example.dwello.data.model.Property
import com.example.dwello.data.model.RentalRequestProperty
import com.example.dwello.data.model.UserDetails
import com.example.dwello.data.model.UserProfile
import com.example.dwello.data.network.ApiClient
import com.example.dwello.data.network.ApiClient.apiService
import retrofit2.Response

class UserRepository {
    suspend fun registerUser(user: UserProfile): Response<Unit> {
        return apiService.registerUser(user)
    }
    suspend fun getHomeProperties(email: String): List<Property> {
        return apiService.getProperties(email)
    }
    suspend fun getUserDetails(email: String) : UserDetails {
        return apiService.getUserDetails(email)
    }
    suspend fun likeProperty(propertyId: String, email: String): Response<Unit> {
        return apiService.likeProperty(propertyId,email)
    }
    suspend fun unlikeProperty(propertyId: String, email: String): Response<Unit> {
        return apiService.unlikeProperty(propertyId,email)
    }
    suspend fun getRentalRequests(email: String): List<RentalRequestProperty> {
        return apiService.getRentalRequests(email, email)
    }

}