package com.example.taro.components
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun TaskListItem(
    taskName : String,
    completed : Boolean
){
    /** This should be fetched from the backend */

    var checked by remember { mutableStateOf(completed) }
    Card(
        modifier = Modifier
            .size(width = 326.dp, height = 52.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2C6AFD)
        )
   ){

        Column(){
            Row(){
                Checkbox(checked= checked, onCheckedChange = { checked = it })
                Text(text = taskName)
            }
        }


    }
}