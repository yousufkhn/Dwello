package com.example.dwello.datastore

import android.content.Context
import android.content.SharedPreferences
import com.example.dwello.data.model.UserProfile
import com.google.gson.Gson

class SharedPrefManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("dwello_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveLoginState(isLoggedIn: Boolean) {
        prefs.edit().putBoolean("is_logged_in", isLoggedIn).apply()
    }

    fun getLoginState(): Boolean {
        return prefs.getBoolean("is_logged_in", false)
    }

    fun saveUserProfile(user: UserProfile) {
        val json = gson.toJson(user)
        prefs.edit().putString("user_profile", json).apply()
    }

    fun getUserProfile(): UserProfile? {
        val json = prefs.getString("user_profile", null)
        return if (json != null) gson.fromJson(json, UserProfile::class.java) else null
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
