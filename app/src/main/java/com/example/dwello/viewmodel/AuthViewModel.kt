package com.example.dwello.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dwello.data.model.UserProfile
import com.example.dwello.datastore.SharedPrefManager
import com.example.dwello.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository()
    private val sharedPrefManager = SharedPrefManager(application)

    private val _isRegistering = MutableStateFlow(false)
    val isRegistering: StateFlow<Boolean> = _isRegistering

    private val _registrationSuccess = MutableStateFlow<Boolean?>(null)
    val registrationSuccess: StateFlow<Boolean?> = _registrationSuccess

    private val _isLoggedIn = MutableStateFlow(sharedPrefManager.getLoginState())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _userProfile = MutableStateFlow(sharedPrefManager.getUserProfile())
    val userProfile: StateFlow<UserProfile?> = _userProfile

    fun registerUser(user: UserProfile) {
        viewModelScope.launch {
            _isRegistering.value = true
            try {
                val response = userRepository.registerUser(user)
                if (response.isSuccessful) {
                    sharedPrefManager.saveUserProfile(user)
                    sharedPrefManager.saveLoginState(true)
                    _userProfile.value = user
                    _isLoggedIn.value = true
                    _registrationSuccess.value = true
                } else {
                    _registrationSuccess.value = false
                }
                Log.d("AuthViewModel", "API Response: $response")
            } catch (e: Exception) {
                _registrationSuccess.value = false
                Log.e("AuthViewModel", "Error registering user", e)
            } finally {
                _isRegistering.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            sharedPrefManager.clear()
            _isLoggedIn.value = false
            _userProfile.value = null
        }
    }

    fun resetRegistrationStatus() {
        _registrationSuccess.value = null
    }
}
