// RequestsViewModel.kt
package com.example.dwello.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dwello.data.model.RentalRequestProperty
import com.example.dwello.datastore.SharedPrefManager
import com.example.dwello.repository.RequestRepository
import com.example.dwello.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RequestsViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPrefManager = SharedPrefManager(application)
    private val userRepository = UserRepository()
    private val repository = RequestRepository() // ✅ Make sure this is your correct repo


    private val _rentalRequests = MutableStateFlow<List<RentalRequestProperty>>(emptyList())
    val rentalRequests: StateFlow<List<RentalRequestProperty>> = _rentalRequests

    init {
        fetchRentalRequests()
    }

    private fun fetchRentalRequests() {
        val email = sharedPrefManager.getUserProfile()?.email ?: return
        viewModelScope.launch {
            try {
                val response = userRepository.getRentalRequests(email)
                _rentalRequests.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun handleRequest(propertyId: String, renterId: String, action: String) {
        viewModelScope.launch {
            val success = repository.handleRentalRequest(propertyId, renterId, action)
            if (success) {
                _rentalRequests.update { current ->
                    if (action == "accept") {
                        current.filterNot { it._id == propertyId }
                    } else {
                        current.map { property ->
                            if (property._id == propertyId) {
                                property.copy(
                                    requesting_users = property.requesting_users.filterNot { it._id == renterId }
                                )
                            } else property
                        }
                    }
                }
            }
        }
    }
}
