package com.example.taro

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TaroJoinGroupPage : ComponentActivity() {

    private lateinit var groupCodeInput: EditText
    private lateinit var joinGroupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.taro_join_group)

        groupCodeInput = findViewById(R.id.groupCodeInput)
        joinGroupButton = findViewById(R.id.joinGroupButton)

        joinGroupButton.setOnClickListener {
            val enteredCode = groupCodeInput.text.toString().trim()
            if (enteredCode.isEmpty()) {
                Toast.makeText(this, "Please enter a group code", Toast.LENGTH_SHORT).show()
            } else {
                joinGroupByCode(enteredCode)
            }
        }
    }

    private fun joinGroupByCode(code: String) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("groups")
            .whereEqualTo("code", code)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "Invalid group code", Toast.LENGTH_SHORT).show()
                } else {
                    val groupDoc = documents.first()
                    val groupRef = db.collection("groups").document(groupDoc.id)

                    // Add user to members array
                    groupRef.update("members", com.google.firebase.firestore.FieldValue.arrayUnion(userId))
                        .addOnSuccessListener {
                            Toast.makeText(this, "Successfully joined group!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to join group: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to find group: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
