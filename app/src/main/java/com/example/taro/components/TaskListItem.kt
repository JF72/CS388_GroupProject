package com.example.taro.components
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
@Preview
fun TaskListItem(
    taskName : String = "Random Task",
    completed : Boolean = false
){
    /** This should be fetched from the backend */

    var checked by remember { mutableStateOf(completed) }
    ElevatedCard (
        modifier = Modifier
            .size(width = 326.dp, height = 52.dp).shadow(elevation = 9.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.19f),
                spotColor = Color.Black.copy(alpha = 0.19f)),
        colors = CardDefaults.cardColors(containerColor = Color.White),

    ){

        Column(){
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 23.dp)
            ){
                Checkbox(checked= checked, onCheckedChange = { checked = it }, colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF2C6AFD),         // Fill when checked
                    uncheckedColor = Color(0xFF80FAFE),         // Outline when unchecked
                    checkmarkColor = Color.White              // Tick mark color
                ))
                Text(text = taskName)
            }
        }


    }
}