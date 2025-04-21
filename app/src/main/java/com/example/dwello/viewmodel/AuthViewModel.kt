package com.example.dwello.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dwello.data.model.UserProfile
import com.example.dwello.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val userRepository = UserRepository()

    private val _isRegistering = MutableStateFlow(false)
    val isRegistering: StateFlow<Boolean> = _isRegistering

    private val _registrationSuccess = MutableStateFlow<Boolean?>(null)
    val registrationSuccess: StateFlow<Boolean?> = _registrationSuccess

    fun registerUser(user: UserProfile) {
        viewModelScope.launch {
            _isRegistering.value = true
            try {
                val response = userRepository.registerUser(user)
                _registrationSuccess.value = response.isSuccessful
                Log.d("AuthViewModel", "API Response: $response")
            } catch (e: Exception) {
                _registrationSuccess.value = false
                Log.e("AuthViewModel", "Error registering user", e)
            } finally {
                _isRegistering.value = false
            }
        }
    }
}
