package com.example.dwello.utils.auth

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.credentials.*
import androidx.credentials.exceptions.GetCredentialException
import com.example.dwello.R
import com.google.android.libraries.identity.googleid.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.UUID
import org.json.JSONObject
import android.util.Base64

class GoogleAuthManager(
    private val context: Context,
    private val credentialManager: CredentialManager,
    private val coroutineScope: CoroutineScope,
    private val onSignInSuccess: (GoogleUser) -> Unit,
    private val onSignInFailure: (String) -> Unit
) {
    private val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(true)
        .setServerClientId(context.getString(R.string.web_client_id))
        .setAutoSelectEnabled(true)
        .setNonce(UUID.randomUUID().toString())
        .build()

    fun launchGoogleSignIn() {
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        coroutineScope.launch {
            try {
                val result = credentialManager.getCredential(context, request)
                handleSignIn(result)
            } catch (e: GetCredentialException) {
                Log.e("GoogleSignIn", "Sign-in failed: ${e.message}")
                Toast.makeText(context, "Sign-in failed", Toast.LENGTH_SHORT).show()
                onSignInFailure(e.message ?: "Unknown error")
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        val credential = result.credential
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                // Decode the JWT to extract user info
                val parts = idToken.split(".")
                if (parts.size == 3) {
                    val payloadJson = String(Base64.decode(parts[1], Base64.URL_SAFE))
                    val payload = JSONObject(payloadJson)

                    val email = payload.optString("email")
                    val name = payload.optString("name")
                    val picture = payload.optString("picture")

                    if (email.isNotEmpty() && name.isNotEmpty()) {
                        val user = GoogleUser(email = email, name = name, profilePicUrl = picture)
                        onSignInSuccess(user)
                    } else {
                        onSignInFailure("Missing user info from ID token")
                    }
                } else {
                    onSignInFailure("Invalid ID token format")
                }

            } catch (e: GoogleIdTokenParsingException) {
                Log.e("GoogleSignIn", "Invalid Google ID token", e)
                onSignInFailure("Invalid Google ID token")
            }
        } else {
            Log.e("GoogleSignIn", "Unexpected credential type")
            onSignInFailure("Unexpected credential type")
        }
    }

    fun signOut(onSignOutSuccess: () -> Unit, onSignOutFailure: (String) -> Unit) {
        coroutineScope.launch {
            try {
                credentialManager.clearCredentialState(
                    ClearCredentialStateRequest()
                )
                Toast.makeText(context, "Signed out successfully", Toast.LENGTH_SHORT).show()
                onSignOutSuccess()
            } catch (e: Exception) {
                Log.e("GoogleSignOut", "Sign-out failed: ${e.message}")
                Toast.makeText(context, "Sign-out failed", Toast.LENGTH_SHORT).show()
                onSignOutFailure(e.message ?: "Sign-out error")
            }
        }
    }

}