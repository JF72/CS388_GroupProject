package com.example.taro

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class TaroPassword : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.taro_password)

        val passwordInput = findViewById<EditText>(R.id.password_input)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val forgotPassword = findViewById<TextView>(R.id.forgotPassword)

        loginButton.setOnClickListener {
            val password = passwordInput.text.toString()
            if (password.isEmpty()) {
                Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show()
            } else {
                // TODO: Authenticate password with Firebase
                Toast.makeText(this, "Password entered: $password", Toast.LENGTH_SHORT).show()
                // If success, go to Dashboard/Homepage
            }
        }

        forgotPassword.setOnClickListener {
            Toast.makeText(this, "Forgot Password flow not implemented yet.", Toast.LENGTH_SHORT).show()
            // TODO: Start password reset flow if you want
        }
    }
}
