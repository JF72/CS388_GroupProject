package com.example.taro

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class TaroPassword : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.taro_password)
        auth = FirebaseAuth.getInstance()
        val passwordInput = findViewById<EditText>(R.id.password_input)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val forgotPassword = findViewById<TextView>(R.id.forgotPassword)
        val email = intent.getStringExtra("email")
        loginButton.setOnClickListener {
            val password = passwordInput.text.toString()
            if (email.isNullOrEmpty()){
                Toast.makeText(this, "No email provided, please log in again", Toast.LENGTH_SHORT).show()
                finish()
            } else if (password.isEmpty()) {
                Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this,TaroHomePage::class.java))
                        finish()
                    }.addOnFailureListener { e ->
                        Toast.makeText(this, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        forgotPassword.setOnClickListener {
            Toast.makeText(this, "Forgot Password flow not implemented yet.", Toast.LENGTH_SHORT).show()
            // TODO: Start password reset flow if you want
        }
    }
}
