package com.example.taro

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TaroRegister : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.taro_register)

        auth = FirebaseAuth.getInstance()

        val usernameInput = findViewById<EditText>(R.id.username_input)
        val emailInput = findViewById<EditText>(R.id.email_input)
        val passwordInput = findViewById<EditText>(R.id.password_input)
        val confirmPasswordInput = findViewById<EditText>(R.id.confirm_password_input)
        val registerBtn = findViewById<Button>(R.id.register_button)
        val showHidePassword = findViewById<ImageButton>(R.id.show_hide_password)
        val showHideConfirmPassword = findViewById<ImageButton>(R.id.show_hide_confirm_password)
        val goToLogin = findViewById<TextView>(R.id.goToLogin) // ✅ Move this here

        var isPasswordVisible = false
        var isConfirmPasswordVisible = false

        // ⭐ Setup click listener for "Go to Login" immediately
        goToLogin.setOnClickListener {
            val intent = Intent(this, TaroLogin::class.java)
            startActivity(intent)
        }

        // Show/hide password logic
        showHidePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            passwordInput.inputType = if (isPasswordVisible) {
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            passwordInput.setSelection(passwordInput.text.length)
        }

        showHideConfirmPassword.setOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
            confirmPasswordInput.inputType = if (isConfirmPasswordVisible) {
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            confirmPasswordInput.setSelection(confirmPasswordInput.text.length)
        }

        registerBtn.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$")

            when {
                username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ->
                    Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()

                password != confirmPassword ->
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()

                !password.matches(passwordRegex) ->
                    Toast.makeText(
                        this,
                        "Password must be 8+ chars and include uppercase, lowercase, number, and symbol",
                        Toast.LENGTH_LONG
                    ).show()

                else -> {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                                val db = FirebaseFirestore.getInstance()
                                val userMap = hashMapOf(
                                    "username" to username,
                                    "email" to email
                                )
                                db.collection("users").document(userId).set(userMap)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this,
                                            "Registered successfully!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        startActivity(Intent(this, TaroLogin::class.java))
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            this,
                                            "Failed to save user: ${e.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                            } else {
                                Toast.makeText(
                                    this,
                                    "Registration failed: ${task.exception?.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                }
            }
        }
    }
}

