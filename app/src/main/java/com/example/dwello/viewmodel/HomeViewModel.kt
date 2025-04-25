package com.example.dwello.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dwello.data.model.Property
import com.example.dwello.data.model.UserProfile
import com.example.dwello.data.network.ApiClient.apiService
import com.example.dwello.datastore.SharedPrefManager
import com.example.dwello.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPrefManager = SharedPrefManager(application)

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    private val _propertyList = MutableStateFlow<List<Property>>(emptyList())
    val propertyList: StateFlow<List<Property>> = _propertyList

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadUserData()
        fetchProperties()
        getUserDetails()
    }

    private fun loadUserData() {
        _userProfile.value = sharedPrefManager.getUserProfile()
    }

    private fun getUserDetails(){
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val email = _userProfile.value?.email
                if (email != null) {
                    val response = apiService.getUserDetails(email)
                    sharedPrefManager.saveUserId(response.id)
                } else {
                    _errorMessage.value = "User email is null"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load properties: ${e.localizedMessage}"
                }
            finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchProperties() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val email = _userProfile.value?.email
                if (email != null) {
                    val response = apiService.getProperties(email)


                    _propertyList.value = response
                } else {
                    _errorMessage.value = "User email is null"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load properties: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private val userRepository = UserRepository()

    fun requestRent(id: String, userId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiService.requestRent(id, userId)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    Log.e("HomeViewModel", "Failed to send rent request: ${response.errorBody()?.string()}")
                    onError("Failed to send rent request")
                }
            } catch (e: Exception) {
                onError("Error: ${e.localizedMessage}")
            }
        }
    }




//    fun toggleLike(propertyId: String) {
//        val email = _userProfile.value?.email ?: return
//        val currentList = _propertyList.value.toMutableList()
//        val propertyIndex = currentList.indexOfFirst { it.id == propertyId }
//        if (propertyIndex != -1) {
//            val property = currentList[propertyIndex]
//            val isLiked = property.isLiked
//
//            viewModelScope.launch {
//                try {
//                    if (isLiked) {
//                        userRepository.unlikeProperty(propertyId, email)
//                    } else {
//                        userRepository.likeProperty(propertyId, email)
//                    }
//
//                    // Update local state
//                    currentList[propertyIndex] = property.copy(isLiked = !isLiked)
//                    _propertyList.value = currentList
//
//                } catch (e: Exception) {
//                    _errorMessage.value = "Failed to ${if (isLiked) "unlike" else "like"} property"
//                }
//            }
//        }
//    }
}
