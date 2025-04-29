package com.example.taro

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class TaskDetailPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.taro_task_detail)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val nameView = findViewById<TextView>(R.id.taskDetailName)
        val descriptionView = findViewById<TextView>(R.id.taskDetailDescription)
        val pointsView = findViewById<TextView>(R.id.taskDetailPoints)
        val dueDateView = findViewById<TextView>(R.id.taskDetailDueDate)

        val name = intent.getStringExtra("name")
        val description = intent.getStringExtra("description")
        val points = intent.getIntExtra("points", 0)
        val dueDate = intent.getStringExtra("dueDate")

        nameView.text = name
        descriptionView.text = description
        pointsView.text = "Points: $points"
        dueDateView.text = "Due: $dueDate"
    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}
