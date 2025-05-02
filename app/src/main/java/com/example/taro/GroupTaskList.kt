package com.example.taro

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class GroupTaskList : AppCompatActivity() {

    private lateinit var taskListView: ListView
    private lateinit var taskAdapter: ArrayAdapter<String>
    private val taskList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.taro_group_task)

        taskListView = findViewById(R.id.groupTaskListView)
        taskAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, taskList)
        taskListView.adapter = taskAdapter

        loadGroupTasks()
    }

    private fun loadGroupTasks() {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { userDoc ->
                    val groupId = userDoc.getString("groupId")
                    if (groupId != null) {
                        db.collection("group_tasks")
                            .whereEqualTo("groupId", groupId)
                            .whereEqualTo("completed", false)
                            .get()
                            .addOnSuccessListener { tasks ->
                                taskList.clear()
                                for (doc in tasks) {
                                    val name = doc.getString("name") ?: "Unnamed Task"
                                    val points = doc.getLong("points") ?: 0
                                    val description = doc.getString("description") ?: ""
                                    val due = doc.getLong("dueDate") ?: 0L
                                    val dueStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(due))

                                    val formattedTask = "$name ($points pts)\nDue: $dueStr\n$description"
                                    taskList.add(formattedTask)
                                }
                                taskAdapter.notifyDataSetChanged()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to load group tasks", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "You are not in a group", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error fetching user data", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
