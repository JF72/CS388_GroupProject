package com.example.taro

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaroTasksPage : ComponentActivity() {

    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private val taskList = mutableListOf<UserTask>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.taro_tasks)

        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        BottomNav.setupBottomNav(bottomNavView, this)
        bottomNavView.selectedItemId = R.id.nav_tasks

        val createTasks = findViewById<FloatingActionButton>(R.id.createTask)
        createTasks.setOnClickListener {
            val intent = Intent(this, TaskCreation::class.java)
            startActivity(intent)
        }

        tasksRecyclerView = findViewById(R.id.tasksRecyclerView)
        tasksRecyclerView.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter(
            taskList,
            onTaskClicked = { task ->
                val intent = Intent(this, TaskDetailPage::class.java)
                intent.putExtra("name", task.name)
                intent.putExtra("description", task.description)
                intent.putExtra("points", task.points)
                intent.putExtra(
                    "dueDate",
                    task.dueDate?.let {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .format(Date(it))
                    } ?: "No Due Date"
                )
                startActivity(intent)
            },
            onCompleteClicked = { task ->
                markTaskComplete(task)
            },
            onEditClicked = { task ->
                val intent = Intent(this, EditTaskActivity::class.java)
                intent.putExtra("taskId", task.id)
                intent.putExtra("name", task.name)
                intent.putExtra("description", task.description)
                intent.putExtra("points", task.points)
                task.dueDate?.let { intent.putExtra("dueDate", it) }
                startActivity(intent)
            }
        )
        tasksRecyclerView.adapter = taskAdapter
    }

    override fun onResume() {
        super.onResume()
        loadTasks()
    }

    private fun loadTasks() {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            db.collection("users")
                .document(userId)
                .collection("tasks")
                .get()
                .addOnSuccessListener { documents ->
                    taskList.clear()
                    for (document in documents) {
                        val task = UserTask(
                            id = document.id,
                            name = document.getString("name") ?: "",
                            description = document.getString("description") ?: "",
                            dueDate = document.getLong("dueDate"),
                            points = (document.getLong("points") ?: 0).toInt(),
                            completed = document.getBoolean("completed") ?: false
                        )
                        taskList.add(task)
                    }
                    taskList.sortWith(compareBy({ it.completed }, { it.dueDate ?: Long.MAX_VALUE }))
                    taskAdapter.notifyDataSetChanged()
                }
        }
    }

    private fun markTaskComplete(task: UserTask) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val userRef = db.collection("users").document(userId)

            db.collection("users")
                .document(userId)
                .collection("tasks")
                .document(task.id)
                .update("completed", true)
                .addOnSuccessListener {
                    userRef.get()
                        .addOnSuccessListener { userDoc ->
                            val currentPoints = userDoc.getLong("totalPoints") ?: 0
                            val updatedPoints = currentPoints + task.points

                            userRef.update("totalPoints", updatedPoints)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Task completed! Points added!", Toast.LENGTH_SHORT).show()
                                    loadTasks()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Failed to update points: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                }
        }
    }
}
