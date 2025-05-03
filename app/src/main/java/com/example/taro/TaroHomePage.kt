package com.example.taro

import DateCardAdapter
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taro.Adapters.TaskListComposeAdapter

import com.example.taro.components.DateCard
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date


class TaroHomePage : ComponentActivity() {

    private lateinit var welcomeText: TextView
    private lateinit var DateComposeRecyclerView : RecyclerView
    private lateinit var adapter : DateCardAdapter



    /** List Item Recycler View */
    private lateinit var  TaskListRecyclerView : RecyclerView
    private lateinit var TaskListAdapter : TaskListComposeAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.taro_homepage)

        super.onCreate(savedInstanceState)

        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        BottomNav.setupBottomNav(bottomNavView, this)
        bottomNavView.selectedItemId = R.id.nav_home
        welcomeText = findViewById(R.id.welcomeText)

        val userId = FirebaseAuth.getInstance().currentUser?.uid

        val data = listOf(
            Triple("11", "April", "Friday"),
            Triple("12", "May", "Saturday"),
            Triple("13", "June", "Sunday"),
            Triple("13", "June", "Sunday"),
            Triple("13", "June", "Sunday"),
            Triple("13", "June", "Sunday"),
            Triple("13", "June", "Sunday")
        )

        val taskListDummyData = listOf(
            Pair("Task1",false),
            Pair("Task2",false),
            Pair("Task3",false),
            Pair("Task4",false),
            Pair("Task5",false),
            Pair("Task6",false)
        )

        DateComposeRecyclerView = findViewById(R.id.DateRecyclerView);
        adapter = DateCardAdapter(data);

        DateComposeRecyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        DateComposeRecyclerView.adapter = adapter;



        TaskListRecyclerView = findViewById(R.id.TaskListRecyclerView);
        TaskListAdapter = TaskListComposeAdapter(taskListDummyData);

        TaskListRecyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        TaskListRecyclerView.adapter = TaskListAdapter



        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val username = document.getString("username") ?: "User"
                        welcomeText.text = "Welcome, $username!"
                    } else {
                        welcomeText.text = "Welcome!"
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    welcomeText.text = "Welcome!"
                    Toast.makeText(this, "Failed to load user: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            welcomeText.text = "Welcome!"
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }

    }
}
