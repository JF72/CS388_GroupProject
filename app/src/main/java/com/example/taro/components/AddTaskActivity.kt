package com.example.taro.components

import android.content.Context
import android.graphics.Paint.Align
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.taro.Dao.UserTaskDb
import com.example.taro.TaroTasksManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.logging.SimpleFormatter


enum class TaskFormPage{
    Description, Details
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddTaskPopUp(onDismissRequest: () -> Unit) {
    var currentPage by remember { mutableStateOf(TaskFormPage.Description) }

    // Shared state
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember {mutableStateOf<String?>(null) }
    var showDatePickerModal by remember { mutableStateOf(false) }

    var difficulty by remember { mutableStateOf(0) }
    var priority by remember { mutableStateOf(0) }
    var urgency by remember { mutableStateOf(0) }
    var expectedDuration by remember { mutableStateOf(0.0) }
    var isCompleted by remember { mutableStateOf(false) }

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val context = LocalContext.current;

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

                        Button(onClick = {showDatePickerModal = true}) {
                            Text("Pick Due Date : Optional")
                        }
                        if(showDatePickerModal){
                            DatePickerModal(
                                onDateSelected = { timeStamp : Long? ->
                                    timeStamp.let {
                                        val date = it?.let { it1 ->
                                            Instant.ofEpochMilli(it1)
                                                .atZone(ZoneOffset.UTC)
                                                .toLocalDate()
                                        }

                                        val formattedDate = date?.format(formatter);
                                        if (formattedDate != null) {
                                            Log.d("DATESELECTED",formattedDate)
                                        }
                                        dueDate = formattedDate;
                                    }

                                    showDatePickerModal = false
                                },


                                onDismiss = { showDatePickerModal= false}
                            )
                        }

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
                                        name = name,
                                        description = description,
                                        difficulty = difficulty,
                                        priority = priority,
                                        urgency = urgency,
                                        dueDate = dueDate,
                                        isCompleted = isCompleted,
                                        expectedDuration = expectedDuration
                                    );
                                    println("Saving task: $task")
                                    CoroutineScope(Dispatchers.IO).launch {
                                        //Adding To Db
                                        addTaskToDb(context,task);


                                    }

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



@RequiresApi(Build.VERSION_CODES.O)
suspend  fun addTaskToDb(context : Context, newTask: UserTaskDb){
    val taskManager = TaroTasksManager()

    val taskListHolder : List<UserTaskDb> = listOf(newTask)
    withContext(Dispatchers.IO){
        taskManager.insertUserTasks(context,taskListHolder);

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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

