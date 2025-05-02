package com.example.taro

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
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
    private lateinit var groupTaskSwitch: Switch
    private lateinit var groupSelector: Spinner
    private var groupMap: Map<String, String> = emptyMap()

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
        groupTaskSwitch = findViewById(R.id.groupTaskSwitch)
        groupSelector = findViewById(R.id.groupSelector)

        groupSelector.visibility = View.GONE

        dueDateButton.setOnClickListener {
            showDatePickerDialog()
        }

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
        val dueDateMillis = selectedDueDate?.time ?: Calendar.getInstance().timeInMillis

        val task = hashMapOf(
            "name" to name,
            "description" to description,
            "dueDate" to dueDateMillis,
            "timeEstimate" to timeEstimate,
            "difficulty" to difficulty,
            "priority" to priority,
            "physicalEffort" to physicalEffort,
            "points" to points,
            "createdAt" to System.currentTimeMillis()
        )

        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        if (groupTaskSwitch.isChecked) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { userDoc ->
                    val userGroupIds = userDoc.get("groupIds") as? List<String> ?: emptyList()

                    if (userGroupIds.isEmpty()) {
                        Toast.makeText(this, "You are not in a group", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    db.collection("groups")
                        .whereIn(FieldPath.documentId(), userGroupIds)
                        .get()
                        .addOnSuccessListener { groupDocs ->
                            val groupNameToId = mutableMapOf<String, String>()
                            for (doc in groupDocs) {
                                val groupName = doc.getString("name") ?: doc.id
                                groupNameToId[groupName] = doc.id
                            }
                            groupMap = groupNameToId

                            if (groupMap.size > 1) {
                                groupSelector.visibility = View.VISIBLE
                                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, groupMap.keys.toList())
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                groupSelector.adapter = adapter
                            }

                            val selectedGroupName = if (groupMap.size > 1) {
                                groupSelector.selectedItem as String
                            } else {
                                groupMap.keys.first()
                            }

                            val selectedGroupId = groupMap[selectedGroupName] ?: return@addOnSuccessListener

                            task["groupId"] = selectedGroupId
                            task["completed"] = false
                            task["createdBy"] = userId

                            db.collection("group_tasks")
                                .add(task)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Group Task Created!", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                }
        } else {
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
        }
    }
}