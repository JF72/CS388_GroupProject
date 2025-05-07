package com.example.taro.components.TaroPath
import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.taro.Dao.UserTaskDb
import com.example.taro.TaroTasksManager
import kotlin.math.min


@Composable
fun TaroPathTaskPoint(
    task: UserTaskDb = UserTaskDb(
    name = "Test",
    description = "",
    difficulty = 3,
    priority = 4,
    urgency = 2,
    dueDate = "2025-05-06",
    isCompleted = true,
    expectedDuration = 2.0,
    taroScore = 0.0
), onClick: () -> Unit = {},
){
    var showDialog by remember { mutableStateOf(false) }
    ExtendedFloatingActionButton(

        onClick = {
            showDialog = true },
        icon = { if(task.isCompleted) Icon(Icons.Filled.CheckCircle, "Extended floating action button.") else Icon(Icons.Filled.Pending, "Extended floating action button.")
        },
        text = { Text(text = task.name) },
        containerColor = if(!task.isCompleted) Color.White else Color(0xFF8BF6AE)
    );

    if (showDialog){
        MinimalDialog( onDismissRequest = {showDialog = false}, task = task)
    }
}

@Preview
@Composable
fun MinimalDialog(
    onDismissRequest: () -> Unit = {}, task: UserTaskDb = UserTaskDb(
name = "Test",
description = "",
difficulty = 3,
priority = 4,
urgency = 2,
dueDate = "2025-05-06",
isCompleted = false,
        expectedDuration = 1.0,
taroScore = 0.0)) {
    val context = LocalContext.current;
    val taroManager = TaroTasksManager();

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(Alignment.CenterVertically)
                .height(400.dp),

            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = if(!task.name.isNullOrEmpty()) task.name else "",
                    modifier = Modifier.padding(15.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp
                )
                if(!task.description.isNullOrEmpty()){
                    Text(
                        text = if(!task.description.isNullOrEmpty()) task.description else "",
                        modifier = Modifier.padding(20.dp)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ){
                    Column (  horizontalAlignment = Alignment.CenterHorizontally,

                    ){
                        task.difficulty?.let {
                            DonutChart(
                                value = it.toFloat(),
                                maxValue = 5f,
                                primaryColor = Color(0xFF6200EA),
                                secondaryColor = Color(0xFFE0E0E0),
                                modifier = Modifier.padding(15.dp)
                            )
                        }
                        Text(
                            text = "Difficulty",
                            modifier = Modifier.wrapContentSize(Alignment.Center),
                            textAlign = TextAlign.Center,
                        )
                        task.urgency?.let {
                            DonutChart(
                                value = it.toFloat(),
                                maxValue = 5f,
                                primaryColor = Color(0xFF6200EA),
                                secondaryColor = Color(0xFFE0E0E0),
                                modifier = Modifier.padding(15.dp)
                            )
                        }
                        Text(
                            text = "Urgency",
                            modifier = Modifier.wrapContentSize(Alignment.Center),
                            textAlign = TextAlign.Center,
                        )
                    }
                    Column(  horizontalAlignment = Alignment.CenterHorizontally,
                    ){
                        task.priority?.let {
                            DonutChart(
                                value = it.toFloat(),
                                maxValue = 5f,
                                primaryColor = Color(0xFF6200EA),
                                secondaryColor = Color(0xFFE0E0E0),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        Text(
                            text = "Priority",
                            modifier = Modifier.wrapContentSize(Alignment.Center),
                            textAlign = TextAlign.Center,
                        )

                        task.taroScore?.let {

                            DonutChart(value = it.toFloat(),
                                maxValue = 80f,
                                primaryColor = Color(0xFF6200EA),
                                secondaryColor = Color(0xFFE0E0E0),
                                modifier = Modifier.padding(16.dp))

                            /*
                            DonutChart(
                                value = it.toFloat(),
                                maxValue = 5f,
                                primaryColor = Color(0xFF6200EA),
                                secondaryColor = Color(0xFFE0E0E0),
                                modifier = Modifier.padding(16.dp)
                            )*/
                        }
                        Text(
                            text = "TaroScore",
                            modifier = Modifier.wrapContentSize(Alignment.Center),
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                if(!task.isCompleted){
                    Button(onClick = {
                        taroManager.updateTaskStatus(context,true,task.uid)

                    }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C6AFD))) {
                        Text("Mark as Complete")
                    }
                }
            }
        }
    }
}




@SuppressLint("RememberReturnType")
@Composable
fun DonutChart(
    value: Float,
    maxValue: Float = 5f,
    size: Float = 80f,
    thickness: Float = 20f,
    animationDuration: Int = 1000,
    primaryColor: Color = Color(0xFF4DDEFD),
    secondaryColor: Color = Color.Green,
    modifier: Modifier = Modifier
) {
    // Ensure value doesn't exceed maxValue
    val boundedValue = remember(value, maxValue) {
        min(value, maxValue)
    }

    // Calculate percentage filled
    val percentage = value / maxValue

    // Animation
    val animatedPercentage = remember { Animatable(percentage) }

    LaunchedEffect(percentage) {
        animatedPercentage.animateTo(
            targetValue = percentage,
            animationSpec = tween(durationMillis = animationDuration)
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size.dp)
    ) {
        Canvas(modifier = Modifier.size(size.dp)) {
            val canvasWidth = this.size.width
            val canvasHeight = this.size.height
            val circleSize = min(canvasWidth, canvasHeight)

            var arcColor : Color = generateArcColor(value,maxValue);


            // Background circle (track)
            drawArc(
                color = Color.Black,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(
                    (canvasWidth - circleSize) / 2 + thickness / 2,
                    (canvasHeight - circleSize) / 2 + thickness / 2
                ),
                size = Size(circleSize - thickness, circleSize - thickness),
                style = Stroke(width = thickness, cap = StrokeCap.Round)
            )

            // Progress arc
            drawArc(
                color = arcColor,
                startAngle = -90f,
                sweepAngle = 360f * animatedPercentage.value,
                useCenter = false,
                topLeft = Offset(
                    (canvasWidth - circleSize) / 2 + thickness / 2,
                    (canvasHeight - circleSize) / 2 + thickness / 2
                ),
                size = Size(circleSize - thickness, circleSize - thickness),
                style = Stroke(width = thickness, cap = StrokeCap.Round)
            )
        }

        // Value text in the center
        Text(
            text = String.format("%.1f", boundedValue),
            style = TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = (size / 4).sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )
    }
}




fun generateArcColor(value : Float, maxValue : Float): Color {

    if (value <= maxValue/3){
        return Color(0xFF4DDEFD)
    } else if (maxValue/3 < value && value < maxValue/2){
        return Color(0xFFE59318)
    }
    else{
        return Color.Red;
    }



}




@Composable
fun PositionedTaskPoint(
    scaledX: Float,
    scaledY: Float,
    content: @Composable () -> Unit
) {
    // Get the density to convert pixels to dp
    val density = LocalDensity.current
    val offsetX = with(density) { scaledX.toDp() }
    val offsetY = with(density) { scaledY.toDp() }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .offset(x = offsetX, y = offsetY)
        ) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DonutChartPreview() {
    DonutChart(
        value = 5.0.toFloat(),
        maxValue = 5f,
        primaryColor = Color(0xFF6200EA),
        secondaryColor = Color(0xFFE0E0E0),
        modifier = Modifier.padding(16.dp)
    )
}