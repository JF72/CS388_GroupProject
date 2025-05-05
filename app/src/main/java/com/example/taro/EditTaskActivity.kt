package com.example.taro

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class EditTaskActivity : AppCompatActivity() {

    private lateinit var taskNameInput: EditText
    private lateinit var taskDescriptionInput: EditText
    private lateinit var dueDateButton: Button
    private lateinit var saveEditButton: Button
    private lateinit var timeEstimateSeekbar: SeekBar
    private lateinit var difficultySeekbar: SeekBar
    private lateinit var prioritySeekbar: SeekBar
    private lateinit var physicalEffortSeekbar: SeekBar

    private var selectedDueDate: Date? = null
    private var taskId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.taro_task_creation)

        taskNameInput = findViewById(R.id.taskNameInput)
        taskDescriptionInput = findViewById(R.id.taskDescriptionInput)
        dueDateButton = findViewById(R.id.dueDateButton)
        saveEditButton = findViewById(R.id.saveTaskButton) // we’ll rename this button label dynamically
        timeEstimateSeekbar = findViewById(R.id.timeEstimateSeekbar)
        difficultySeekbar = findViewById(R.id.difficultySeekbar)
        prioritySeekbar = findViewById(R.id.prioritySeekbar)
        physicalEffortSeekbar = findViewById(R.id.physicalEffortSeekbar)

        taskId = intent.getStringExtra("taskId")
        loadTaskData()

        dueDateButton.setOnClickListener {
            showDatePickerDialog()
        }

        saveEditButton.setText("Save Changes")
        saveEditButton.setOnClickListener {
            updateTaskInFirestore()
        }
    }

    private fun loadTaskData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .document(userId)
            .collection("tasks")
            .document(taskId ?: return)
            .get()
            .addOnSuccessListener { document ->
                taskNameInput.setText(document.getString("name"))
                taskDescriptionInput.setText(document.getString("description"))
                timeEstimateSeekbar.progress = (document.getLong("timeEstimate") ?: 0).toInt()
                difficultySeekbar.progress = (document.getLong("difficulty") ?: 0).toInt()
                prioritySeekbar.progress = (document.getLong("priority") ?: 0).toInt()
                physicalEffortSeekbar.progress = (document.getLong("physicalEffort") ?: 0).toInt()
                selectedDueDate = document.getLong("dueDate")?.let { Date(it) }
                dueDateButton.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDueDate ?: Date())
            }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        selectedDueDate?.let { calendar.time = it }

        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            selectedDueDate = calendar.time
            dueDateButton.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDueDate!!)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun updateTaskInFirestore() {
        val name = taskNameInput.text.toString().trim()
        val description = taskDescriptionInput.text.toString().trim()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        if (name.isEmpty()) {
            Toast.makeText(this, "Task name required", Toast.LENGTH_SHORT).show()
            return
        }

        val timeEstimate = timeEstimateSeekbar.progress
        val difficulty = difficultySeekbar.progress
        val priority = prioritySeekbar.progress
        val physicalEffort = physicalEffortSeekbar.progress
        val points = (timeEstimate * 2) + (difficulty * 3) + (priority * 4) + (physicalEffort * 2)

        val taskUpdates = mapOf(
            "name" to name,
            "description" to description,
            "dueDate" to (selectedDueDate?.time ?: System.currentTimeMillis()),
            "timeEstimate" to timeEstimate,
            "difficulty" to difficulty,
            "priority" to priority,
            "physicalEffort" to physicalEffort,
            "points" to points
        )

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("tasks")
            .document(taskId ?: return)
            .update(taskUpdates)
            .addOnSuccessListener {
                Toast.makeText(this, "Task updated!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Update failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
