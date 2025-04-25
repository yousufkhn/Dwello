package com.example.dwello.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dwello.data.model.Property
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RentedPropertiesViewModel : ViewModel() {

    private val _rentedProperties = MutableStateFlow<List<Property>>(emptyList())
    val rentedProperties: StateFlow<List<Property>> = _rentedProperties


    fun fetchRentedProperties(userId: String) {
        viewModelScope.launch {
            try {
                val properties = withContext(Dispatchers.IO) {
                    val client = OkHttpClient()
                    val request = Request.Builder()
                        .url("https://rjbcjks3-8080.inc1.devtunnels.ms/api/users/$userId/rented-properties/?user_id=$userId")
                        .build()

                    val response = client.newCall(request).execute()
                    val json = response.body?.string()

                    if (response.isSuccessful && json != null) {
                        val type = object : TypeToken<List<Property>>() {}.type
                        Gson().fromJson<List<Property>>(json, type)
                    } else {
                        emptyList()
                    }
                }

                _rentedProperties.value = properties
            } catch (e: Exception) {
                Log.e("RentedPropertiesVM", "Error fetching properties", e)
            }
        }
    }
}