package com.example.taro.components

import android.graphics.Paint.Align
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.taro.UserTaskDb


enum class TaskFormPage{
    Description, Details
}

@Composable
fun AddTaskPopUp(onDismissRequest: () -> Unit) {
    var currentPage by remember { mutableStateOf(TaskFormPage.Description) }

    // Shared state
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }

    var difficulty by remember { mutableStateOf(0) }
    var priority by remember { mutableStateOf(0) }
    var urgency by remember { mutableStateOf(0) }
    var expectedDuration by remember { mutableStateOf(0.0) }
    var isCompleted by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            modifier = Modifier
                .background(Color.White)
                .padding(24.dp)
                .widthIn(min = 320.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                when (currentPage) {
                    TaskFormPage.Description -> {
                        Text("Create Task", fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(16.dp))

                        androidx.compose.material3.OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Task Name*") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        androidx.compose.material3.OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description (optional)") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        androidx.compose.material3.OutlinedTextField(
                            value = dueDate,
                            onValueChange = { dueDate = it },
                            label = { Text("Due Date (optional)") }
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        androidx.compose.material3.Button(
                            onClick = {
                                if (name.isNotBlank()) {
                                    currentPage = TaskFormPage.Details
                                }
                            }
                        ) {
                            Text("Next")
                        }
                    }

                    TaskFormPage.Details -> {
                        AddTaskActivity(
                            difficulty = difficulty,
                            onDifficultyChange = { difficulty = it },
                            priority = priority,
                            onPriorityChange = { priority = it },
                            urgency = urgency,
                            onUrgencyChange = { urgency = it },
                            expectedDuration = expectedDuration,
                            onDurationChange = { expectedDuration = it }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row {
                            androidx.compose.material3.TextButton(
                                onClick = { currentPage = TaskFormPage.Description }
                            ) {
                                Text("Back")
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            androidx.compose.material3.Button(
                                onClick = {
                                    val task = UserTaskDb(
                                        uid = (0..999999).random(),
                                        name = name,
                                        description = description,
                                        difficulty = difficulty,
                                        priority = priority,
                                        urgency = urgency,
                                        dueDate = dueDate, // parse later
                                        isCompleted = isCompleted,
                                        taroScore = expectedDuration
                                    )
                                    println("Saving task: $task")
                                    onDismissRequest()
                                }
                            ) {
                                Text("Save")
                            }
                        }
                    }
                }
            }
        }
    }
}



@Composable
// @Preview
fun AddTaskActivity(
    difficulty: Int,
    onDifficultyChange: (Int) -> Unit,
    priority: Int,
    onPriorityChange: (Int) -> Unit,
    urgency: Int,
    onUrgencyChange: (Int) -> Unit,
    expectedDuration: Double,
    onDurationChange: (Double) -> Unit

) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp)
                .widthIn(min = 320.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF00FF9D))
                    .padding(8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Add your task",
                    fontSize = 20.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            RatingRow("How Challenging is this task?", difficulty, onDifficultyChange)
            RatingRow("How Important is this task?", priority, onPriorityChange)
            RatingRow("How soon must the task be done?", urgency, onUrgencyChange)

            Spacer(modifier = Modifier.height(16.dp))

            Text("Time needed to complete the task")

            Slider(
                value = expectedDuration.toFloat(),
                onValueChange = { onDurationChange(it.toDouble()) },
                valueRange = 1f..12f,
                steps = 10,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF00FF9D),
                    activeTrackColor = Color(0xFF00BFFF),
                    inactiveTrackColor = Color.LightGray
                )
            )

            Text(
                text = "${expectedDuration.toInt()} hours",
                fontStyle = FontStyle.Italic,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}


@Composable
fun RatingRow(
    label: String,
    rating: Int,
    onRatingChange: (Int) -> Unit
){
    Column(modifier = Modifier
        .padding(vertical = 8.dp)
        .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label)

        Row(modifier = Modifier.padding(top = 4.dp)) {
            for (i in 1..5) {
                val filled = i <= rating
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(if (filled) Color(0xFF00EFFF) else Color.Transparent)
                        .border(1.dp, Color(0xFF00EFFF), CircleShape)
                        .clickable { onRatingChange(i) }


                )
            }
        }
    }
}
