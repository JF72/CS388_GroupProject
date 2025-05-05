package com.example.taro

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class TaskCreation : AppCompatActivity() {

    private lateinit var taskNameInput: EditText
    private lateinit var taskDescriptionInput: EditText
    private lateinit var dueDateButton: Button
    private lateinit var saveTaskButton: Button
    private lateinit var timeEstimateSeekbar: SeekBar
    private lateinit var difficultySeekbar: SeekBar
    private lateinit var prioritySeekbar: SeekBar
    private lateinit var physicalEffortSeekbar: SeekBar

    private var selectedDueDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.taro_task_creation)

        taskNameInput = findViewById(R.id.taskNameInput)
        taskDescriptionInput = findViewById(R.id.taskDescriptionInput)
        dueDateButton = findViewById(R.id.dueDateButton)
        saveTaskButton = findViewById(R.id.saveTaskButton)
        timeEstimateSeekbar = findViewById(R.id.timeEstimateSeekbar)
        difficultySeekbar = findViewById(R.id.difficultySeekbar)
        prioritySeekbar = findViewById(R.id.prioritySeekbar)
        physicalEffortSeekbar = findViewById(R.id.physicalEffortSeekbar)

        dueDateButton.setOnClickListener {
            showDatePickerDialog()
        }

        /** Change To save on local storage **/
        saveTaskButton.setOnClickListener {
            saveTaskToFirebase()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedDueDate = calendar.time
                dueDateButton.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDueDate!!)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun saveTaskToFirebase() {
        val name = taskNameInput.text.toString().trim()
        val description = taskDescriptionInput.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Task name required", Toast.LENGTH_SHORT).show()
            return
        }

        val timeEstimate = timeEstimateSeekbar.progress
        val difficulty = difficultySeekbar.progress
        val priority = prioritySeekbar.progress
        val physicalEffort = physicalEffortSeekbar.progress

        val points = (timeEstimate * 2) + (difficulty * 3) + (priority * 4) + (physicalEffort * 2)

        val task = hashMapOf(
            "name" to name,
            "description" to description,
            "dueDate" to (selectedDueDate?.time ?: Calendar.getInstance().timeInMillis),
            "timeEstimate" to timeEstimate,
            "difficulty" to difficulty,
            "priority" to priority,
            "physicalEffort" to physicalEffort,
            "points" to points,
            "createdAt" to System.currentTimeMillis()
        )

        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            db.collection("users")
                .document(userId)
                .collection("tasks")
                .add(task)
                .addOnSuccessListener {
                    Toast.makeText(this, "Task Created!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to create task: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}
