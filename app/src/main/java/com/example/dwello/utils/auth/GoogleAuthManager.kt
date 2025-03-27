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

class GoogleAuthManager(
    private val context: Context,
    private val credentialManager: CredentialManager,
    private val coroutineScope: CoroutineScope,
    private val onSignInSuccess: (String) -> Unit,
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
                Log.d("GoogleSignIn", "ID Token: $idToken")
                onSignInSuccess(idToken)
            } catch (e: GoogleIdTokenParsingException) {
                Log.e("GoogleSignIn", "Invalid Google ID token", e)
                onSignInFailure("Invalid Google ID token")
            }
        } else {
            Log.e("GoogleSignIn", "Unexpected credential type")
            onSignInFailure("Unexpected credential type")
        }
    }
}