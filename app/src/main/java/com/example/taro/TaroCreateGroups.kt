package com.example.taro

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class TaroCreateGroupPage : ComponentActivity() {

    private lateinit var groupNameInput: EditText
    private lateinit var createGroupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.taro_create_group)

        groupNameInput = findViewById(R.id.groupNameInput)
        createGroupButton = findViewById(R.id.createGroupButton)

        createGroupButton.setOnClickListener {
            val groupName = groupNameInput.text.toString().trim()
            if (groupName.isEmpty()) {
                Toast.makeText(this, "Please enter a group name", Toast.LENGTH_SHORT).show()
            } else {
                createGroup(groupName)
            }
        }
    }

    private fun createGroup(groupName: String) {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val groupCode = generateGroupCode()

        val groupData = hashMapOf(
            "name" to groupName,
            "code" to groupCode,
            "createdBy" to userId,
            "members" to listOf(userId)
        )

        db.collection("groups")
            .add(groupData)
            .addOnSuccessListener { docRef ->
                val groupId = docRef.id

                db.collection("users").document(userId)
                    .update("groupIds", FieldValue.arrayUnion(groupId))
                    .addOnSuccessListener {
                        Toast.makeText(this, "Group created! Code: $groupCode", Toast.LENGTH_LONG).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Group created but failed to update user: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to create group: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun generateGroupCode(): String {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6)
            .map { allowedChars.random() }
            .joinToString("")
    }
}
