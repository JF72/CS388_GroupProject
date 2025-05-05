package com.example.taro.components

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
import com.example.taro.UserTaskDb


@Composable
@Preview
fun AddTaskActivity(
    //TODO SQL UPLOAD NEEDS TO BE MADE
    ){
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf(0) }
    var priority by remember { mutableStateOf(0) }
    var urgency by remember { mutableStateOf(0) }
    var expectedDuration by remember { mutableStateOf(0.0) }
    var dueDate by remember { mutableStateOf("") }
    var isCompleted by remember { mutableStateOf(false) }
    var taroScore by remember { mutableStateOf(0.0) }

    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(16.dp)
            .widthIn(min = 320.dp)
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF00FF9D))
                .padding(8.dp)
        ){
            Text(
                text = "Add your task",
                fontSize = 20.sp,
                color = Color.Black
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        RatingRow("How Challenging is this task?", difficulty) { difficulty = it }
        RatingRow("How Important is this task?", priority) { priority = it }
        RatingRow("How soon must the task be done?", urgency) { urgency = it }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Time needed to complete the task")

        Slider(
            value = expectedDuration.toFloat(),
            onValueChange = { expectedDuration = it.toDouble() },
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


@Composable
fun RatingRow(label: String, rating: Int, onRatingChange: (Int) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
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
