package com.example.taro

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class GroupTaskList : AppCompatActivity() {

    private lateinit var taskListView: ListView
    private lateinit var taskAdapter: ArrayAdapter<String>
    private val taskList = mutableListOf<String>()
    private val taskIdMap = mutableMapOf<String, String>() // maps task string to Firestore ID
    private val TAG = "GroupTaskList"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.taro_group_task)

        taskListView = findViewById(R.id.groupTaskListView)
        taskAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, taskList)
        taskListView.adapter = taskAdapter

        taskListView.setOnItemClickListener { _, _, position, _ ->
            val selectedTask = taskList[position]
            Log.d(TAG, "Clicked on task: $selectedTask")

            val taskId = taskIdMap[selectedTask]
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            if (taskId != null && userId != null) {
                Log.d(TAG, "Attempting to claim task with ID: $taskId")

                val taskRef = FirebaseFirestore.getInstance().collection("group_tasks").document(taskId)

                // First, get the current task data to check its status
                taskRef.get().addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val currentClaim = document.getString("claimedBy")

                        if (currentClaim == null) {
                            // Task is unclaimed, so we can claim it
                            taskRef.update("claimedBy", userId)
                                .addOnSuccessListener {
                                    Log.d(TAG, "Task claimed successfully")
                                    Toast.makeText(this, "Task claimed successfully!", Toast.LENGTH_SHORT).show()
                                    // Reload the task list to reflect changes
                                    loadGroupTasks()
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Error claiming task", e)
                                    Toast.makeText(this, "Failed to claim task: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else if (currentClaim == userId) {
                            // User has already claimed this task
                            Toast.makeText(this, "You've already claimed this task", Toast.LENGTH_SHORT).show()
                        } else {
                            // Task is claimed by someone else
                            Toast.makeText(this, "This task has already been claimed by someone else", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.d(TAG, "Task document doesn't exist")
                        Toast.makeText(this, "Error: Task not found", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener { e ->
                    Log.e(TAG, "Error getting task document", e)
                    Toast.makeText(this, "Error accessing task: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e(TAG, "Missing taskId or userId. TaskId: $taskId, UserId: $userId")
                Toast.makeText(this, "Error: Could not identify task or user", Toast.LENGTH_SHORT).show()
            }
        }

        loadGroupTasks()
    }

    private fun loadGroupTasks() {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        Log.d(TAG, "Loading group tasks for user: $userId")

        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { userDoc ->
                    Log.d(TAG, "User document fetched")

                    val groupId = userDoc.getString("groupId")
                    Log.d(TAG, "User is in group: $groupId")

                    if (groupId != null) {
                        db.collection("group_tasks")
                            .whereEqualTo("groupId", groupId)
                            .whereEqualTo("completed", false)
                            .get()
                            .addOnSuccessListener { tasks ->
                                Log.d(TAG, "Fetched ${tasks.size()} group tasks")

                                taskList.clear()
                                taskIdMap.clear()

                                val claimedByIds = mutableSetOf<String>()
                                val taskDocs = mutableListOf<Map<String, Any?>>()

                                for (doc in tasks) {
                                    val taskData: Map<String, Any?> = mapOf(
                                        "id" to doc.id,
                                        "name" to (doc.getString("name") ?: "Unnamed Task"),
                                        "points" to (doc.getLong("points") ?: 0),
                                        "description" to (doc.getString("description") ?: ""),
                                        "dueDate" to (doc.getLong("dueDate") ?: 0L),
                                        "claimedBy" to doc.getString("claimedBy")
                                    )
                                    taskDocs.add(taskData)
                                    (doc.getString("claimedBy"))?.let { claimedByIds.add(it) }
                                }

                                if (claimedByIds.isEmpty()) {
                                    Log.d(TAG, "Rendering tasks without claimers")
                                    renderTasks(taskDocs, emptyMap())
                                } else {
                                    db.collection("users")
                                        .whereIn(FieldPath.documentId(), claimedByIds.toList())
                                        .get()
                                        .addOnSuccessListener { userDocs ->
                                            Log.d(TAG, "Fetched ${userDocs.size()} user documents for claimers")

                                            val nameMap = userDocs.associate {
                                                it.id to (it.getString("username") ?: "Unknown")
                                            }
                                            renderTasks(taskDocs, nameMap)
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e(TAG, "Failed to load claimers", e)
                                            Toast.makeText(this, "Failed to load claimers", Toast.LENGTH_SHORT).show()
                                            renderTasks(taskDocs, emptyMap())
                                        }
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Failed to fetch group tasks", e)
                                Toast.makeText(this, "Failed to load group tasks", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Log.d(TAG, "No groupId found for user")
                        Toast.makeText(this, "You are not in a group", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error fetching user document", e)
                    Toast.makeText(this, "Error fetching user data", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun renderTasks(
        taskDocs: List<Map<String, Any?>>,
        nameMap: Map<String, String>
    ) {
        taskList.clear()
        taskIdMap.clear()

        for (task in taskDocs) {
            val name = task["name"] as String
            val points = (task["points"] as Long).toInt()
            val description = task["description"] as String
            val due = task["dueDate"] as Long
            val claimedBy = task["claimedBy"] as String?

            val dueStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(due))
            val claimedStatus = if (claimedBy == null) {
                "Unclaimed"
            } else {
                "Claimed by ${nameMap[claimedBy] ?: "Unknown"}"
            }

            val formattedTask = "$name ($points pts) [$claimedStatus]\nDue: $dueStr\n$description"

            taskList.add(formattedTask)
            taskIdMap[formattedTask] = task["id"] as String
        }

        taskAdapter.notifyDataSetChanged()
        Log.d(TAG, "Tasks loaded into ListView: ${taskList.size}")
    }
}