package com.example.taro

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class GroupDetailActivity : AppCompatActivity() {

    private lateinit var groupTasksList: ListView
    private lateinit var leaderboardList: ListView
    private lateinit var tasksAdapter: ArrayAdapter<String>
    private lateinit var leaderboardAdapter: ArrayAdapter<String>
    private val taskList = mutableListOf<String>()
    private val leaderboard = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.taro_group_detail)

        groupTasksList = findViewById(R.id.groupTasksListView)
        leaderboardList = findViewById(R.id.leaderboardListView)
        findViewById<Button>(R.id.backButton).setOnClickListener {
            finish()
        }

        tasksAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, taskList)
        leaderboardAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, leaderboard)

        groupTasksList.adapter = tasksAdapter
        leaderboardList.adapter = leaderboardAdapter

        val groupId = intent.getStringExtra("groupId") ?: return

        loadGroupTasks(groupId)
        loadGroupLeaderboard(groupId)
    }

    private fun loadGroupTasks(groupId: String) {
        val db = FirebaseFirestore.getInstance()
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
                    val formatted = "$name ($points pts)\nDue: $dueStr\n$description"
                    taskList.add(formatted)
                }
                tasksAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load group tasks", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadGroupLeaderboard(groupId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .whereArrayContains("groupIds", groupId)
            .orderBy("totalPoints", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { users ->
                leaderboard.clear()
                val topUsers = users.documents.take(3)

                for ((index, doc) in topUsers.withIndex()) {
                    val name = doc.getString("username") ?: "User"
                    val points = doc.getLong("totalPoints") ?: 0
                    leaderboard.add("${index + 1}. $name - $points pts")
                }
                leaderboardAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load leaderboard", Toast.LENGTH_SHORT).show()
            }
    }
}
