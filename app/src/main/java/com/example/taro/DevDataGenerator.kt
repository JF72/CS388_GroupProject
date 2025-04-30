package com.example.taro

import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

object DevDataGenerator {

    fun generateDummyTasks(context: Context, userId: String, weeks: Int = 4, tasksPerWeek: Int = 5) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(userId)
        val tasksRef = userRef.collection("tasks")

        // Step 1: Delete all existing tasks
        tasksRef.get().addOnSuccessListener { snapshot ->
            val batch = db.batch()
            for (doc in snapshot.documents) {
                batch.delete(doc.reference)
            }

            batch.commit().addOnSuccessListener {
                // Step 2: Reset totalPoints to 0
                userRef.update("totalPoints", 0)
                    .addOnSuccessListener {
                        // Step 3: Generate new dummy data
                        actuallyGenerateTasks(context, db, userId, weeks, tasksPerWeek)
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to reset totalPoints", Toast.LENGTH_SHORT).show()
                    }
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to fetch existing tasks", Toast.LENGTH_SHORT).show()
        }
    }

    private fun actuallyGenerateTasks(
        context: Context,
        db: FirebaseFirestore,
        userId: String,
        weeks: Int,
        tasksPerWeek: Int
    ) {
        val now = System.currentTimeMillis()
        val weekMillis = 7 * 24 * 60 * 60 * 1000L
        var earnedPoints = 0
        val userRef = db.collection("users").document(userId)

        for (weekOffset in 0 until weeks) {
            val baseTime = now - (weekOffset * weekMillis)

            for (i in 1..tasksPerWeek) {
                val createdAt = baseTime + (i * 2 * 60 * 60 * 1000L)
                val dueDate = createdAt + (24 * 60 * 60 * 1000L)
                val isCompleted = (0..1).random() == 1

                val timeEstimate = (1..5).random()
                val difficulty = (1..5).random()
                val priority = (1..5).random()
                val physicalEffort = (1..5).random()
                val points = (timeEstimate * 2) + (difficulty * 3) + (priority * 4) + (physicalEffort * 2)

                if (isCompleted) {
                    earnedPoints += points
                }

                val task = hashMapOf(
                    "name" to "Task ${weekOffset + 1}.$i",
                    "description" to "Auto-generated dummy task",
                    "completed" to isCompleted,
                    "createdAt" to createdAt,
                    "dueDate" to dueDate,
                    "points" to points,
                    "timeEstimate" to timeEstimate,
                    "difficulty" to difficulty,
                    "priority" to priority,
                    "physicalEffort" to physicalEffort
                )

                db.collection("users")
                    .document(userId)
                    .collection("tasks")
                    .add(task)
            }
        }

        // Step 4: Update totalPoints with earned points from new tasks
        userRef.update("totalPoints", earnedPoints)
            .addOnSuccessListener {
                Toast.makeText(context, "Dummy data added! +$earnedPoints points", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Tasks added, but failed to update totalPoints", Toast.LENGTH_SHORT).show()
            }
    }
}
