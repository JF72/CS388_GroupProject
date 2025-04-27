package com.example.taro

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
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
                Toast.makeText(this, "Email entered: $email", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, TaroPassword::class.java)
                intent.putExtra("email", email)
                startActivity(intent)
            }
        }

        // Temp

        googleBtn.setOnClickListener {
            Toast.makeText(this, "Google Login not implemented yet.", Toast.LENGTH_SHORT).show()
        }

        facebookBtn.setOnClickListener {
            Toast.makeText(this, "Facebook Login not implemented yet.", Toast.LENGTH_SHORT).show()
        }
    }
}
