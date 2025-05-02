package com.example.taro

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class TaroLogin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.taro_login)

        // Link views to code
        val emailInput = findViewById<EditText>(R.id.email_input)
        val nextButton = findViewById<Button>(R.id.logIn_next_btn)

        val googleBtn = findViewById<LinearLayout>(R.id.googleBtn)
        val facebookBtn = findViewById<LinearLayout>(R.id.facebookBtn)

        // Next button functionality
        nextButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            } else {
                val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
                auth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val signInMethods = task.result?.signInMethods
                            if (signInMethods.isNullOrEmpty()) {
                                Toast.makeText(this, "No account found for this email", Toast.LENGTH_SHORT).show()
                            } else {
                                // Email is registered — proceed to password screen
                                val intent = Intent(this, TaroPassword::class.java)
                                intent.putExtra("email", email)
                                startActivity(intent)
                            }
                        } else {
                            Toast.makeText(this, "Error checking email: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }


        val goToRegister = findViewById<TextView>(R.id.goToRegister)

        goToRegister.setOnClickListener {
            val intent = Intent(this, TaroRegister::class.java)
            startActivity(intent)
        }

        googleBtn.setOnClickListener {
            Toast.makeText(this, "Google Login not implemented yet.", Toast.LENGTH_SHORT).show()
        }

        facebookBtn.setOnClickListener {
            Toast.makeText(this, "Facebook Login not implemented yet.", Toast.LENGTH_SHORT).show()
        }
    }
}
