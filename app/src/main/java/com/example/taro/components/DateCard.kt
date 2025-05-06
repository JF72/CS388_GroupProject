package com.example.taro.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//@Preview
@Composable
fun DateCard(
    day: String,
    month: String,
    weekday: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val backgroundColor = if(isSelected) Color(0xFF2C6AFD) else Color(0xFFEFF4FF)
    val textColor = if (isSelected) Color.White else Color.Black

    Card(
        modifier = Modifier
            .size(width = 64.dp, height = 85.dp).padding(1.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = month,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
            Text(
                text = day,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
            Text(
                text = weekday,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )

        }
    }
}


