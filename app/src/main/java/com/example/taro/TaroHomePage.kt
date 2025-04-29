package com.example.taro

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TaroHomePage : ComponentActivity() {

    private lateinit var welcomeText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.taro_homepage)
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        BottomNav.setupBottomNav(bottomNavView, this)
        bottomNavView.selectedItemId = R.id.nav_home
        welcomeText = findViewById(R.id.welcomeText)

        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val username = document.getString("username") ?: "User"
                        welcomeText.text = "Welcome, $username!"
                    } else {
                        welcomeText.text = "Welcome!"
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    welcomeText.text = "Welcome!"
                    Toast.makeText(this, "Failed to load user: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            welcomeText.text = "Welcome!"
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}
