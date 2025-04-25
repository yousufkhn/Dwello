package com.example.dwello.data.network

import com.example.dwello.data.model.Property
import com.example.dwello.data.model.RentalRequestProperty
import com.example.dwello.data.model.UserDetails
import com.example.dwello.data.model.UserProfile
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("api/users/register") // Adjust endpoint as needed
    suspend fun registerUser(@Body user: UserProfile): Response<Unit>

    @GET("api/properties/homescreen")
    suspend fun getProperties(@Query("email") email: String): List<Property>

    @GET("api/users/{email}")
    suspend fun getUserDetails(@Path("email") email: String): UserDetails

    @POST("api/properties/{id}/like")
    suspend fun likeProperty(@Path("id") propertyId: String,@Query("email") email: String): Response<Unit>

    @POST("api/properties/{id}/unlike")
    suspend fun unlikeProperty(@Path("id") propertyId: String,@Query("email") email: String): Response<Unit>

    @POST("api/properties/{id}/rent")
    suspend fun requestRent(
        @Path("id") id: String,
        @Query("user_id") userId: String
    ): Response<Unit>

    @GET("api/users/{email}/rental-requests/")
    suspend fun getRentalRequests(@Path("email") email: String, @Query("email") queryEmail: String): List<RentalRequestProperty>

}