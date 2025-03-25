package com.example.dwello

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        // Delay for splash effect (optional)
        android.os.Handler().postDelayed({
            checkUserStatus()
        }, 1000) // 1 second delay
    }

    private fun checkUserStatus() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is signed in, go to Dashboard
            val intent = Intent(this, Dashboard::class.java)
            intent.putExtra("name", currentUser.displayName)
            intent.putExtra("email", currentUser.email)
            startActivity(intent)
        } else {
            // Not signed in, go to Onboarding
            startActivity(Intent(this, Onboarding::class.java))
        }
        finish() // close splash activity
    }
}