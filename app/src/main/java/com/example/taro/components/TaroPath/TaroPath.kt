package com.example.taro.components.TaroPath

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.taro.Dao.UserTaskDb
import com.example.taro.TaroTasksManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

data class Task(
    val title: String,
    val description: String,
    val status: String
)
@Composable
fun TaroPathScreen(selectedDate : String) {
    // Sample tasks
    val tasks = listOf(
        UserTaskDb(
            name = "Write report",
            description = "Complete the monthly status report",
            difficulty = 3,
            priority = 2,
            urgency = 4,
            expectedDuration = 90.0, // in minutes
            dueDate = "2025-05-07",
            isCompleted = false,
            taroScore = 72.5,
            completedOn = null
        ),
        UserTaskDb(
            name = "Fix login bug",
            description = "Resolve issue with user login timeout",
            difficulty = 4,
            priority = 3,
            urgency = 5,
            expectedDuration = 120.0,
            dueDate = "2025-05-07",
            isCompleted = false,
            taroScore = 85.0,
            completedOn = null
        ),
    )

    val context = LocalContext.current
    val sortedTasks = remember { mutableStateOf<List<UserTaskDb>>(listOf()) }
    val isLoading = remember { mutableStateOf(true) }
    val isGeneratingScore = remember { mutableStateOf(true) }

    LaunchedEffect(selectedDate) {
        isGeneratingScore.value = true
        isLoading.value = true

        // Step 1: Run generateScore (must be suspend or wrapped in coroutine)
        generateScore(context, selectedDate)

        // Step 2: Fetch data
        fetchData(context, selectedDate) { taskData ->
            sortedTasks.value = taskData
            isLoading.value = false
            isGeneratingScore.value = false
        }
    }


    if (isGeneratingScore.value){
        CircularProgressIndicator()
    }
    else{
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (isLoading.value) {
                CircularProgressIndicator()
            } else {
                if (sortedTasks.value.isNotEmpty()){
                    PathWithTasks(sortedTasks.value)
                }else{
                    PathWithTasks(tasks)
                }
            }

        }

    }


}


fun fetchData(context: Context, pathSelectedDate:String, callback : (List<UserTaskDb>)->Unit){
    val taroTasksManager = TaroTasksManager()
    CoroutineScope(Dispatchers.IO).launch {
        val tasks = pathSelectedDate.let { taroTasksManager.getTasksByDate(context, it) }
        withContext(Dispatchers.Main) {
            callback(tasks)
        }
    }
}

suspend fun generateScore(context: Context, pathSelectedDate: String) {
    val taroTasksManager = TaroTasksManager()
    withContext(Dispatchers.IO) {
        taroTasksManager.setTasksTaroScore(context, pathSelectedDate, 3)
    }
}

@Composable
fun PathWithTasks(tasks: List<UserTaskDb>,) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height((200 * tasks.size).dp)
    ) {

        TaroPath(tasks.size, Modifier.fillMaxSize(),tasks)
    }
}

@Composable
fun TaroPath(pointCount: Int, modifier: Modifier = Modifier, tasks : List<UserTaskDb>) {
    val pointsList: MutableList<Offset> = generateCoordinates(pointCount)
    var stateTasks by remember { mutableStateOf<List<UserTaskDb>>(tasks) }

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Calculate scaling factors to fit all points within the canvas
        val xValues = pointsList.map { it.x }
        val yValues = pointsList.map { it.y }

        val minX = xValues.minOrNull() ?: 0f
        val maxX = xValues.maxOrNull() ?: 0f
        val minY = yValues.minOrNull() ?: 0f
        val maxY = yValues.maxOrNull() ?: 0f

        val xRange = maxX - minX
        val yRange = maxY - minY

        // Include padding to avoid edges
        val padding = 20f
        val xScale = (canvasWidth - 2 * padding) / xRange.coerceAtLeast(1f)
        val yScale = 30f
        // Create path for connecting lines
        val path = Path()

        pointsList.forEachIndexed { index, coordinate ->
            // Scale and center the coordinates
            val scaledX = (coordinate.x - minX) * xScale + padding
            val scaledY = (coordinate.y - minY) * yScale + padding

            // Add point to path
            if (index == 0) {
                path.moveTo(scaledX  , scaledY)
            } else {
                path.lineTo(scaledX, scaledY)
            }
            // Draw points

            drawCircle(
                Color.Blue,
                radius = 10.dp.toPx(),
                center = Offset(scaledX, scaledY)
            )
        }


        // Draw connecting lines
        /** In order for this to be more smooth create series of invisible offset points in the map */

        drawPath(
            path = path,
            color = Color(0xFF4DDEFD),
            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round,
                join= StrokeJoin.Round)
        )
    }


    pointsList.zip(stateTasks).forEach { (coordinate, task) ->
        // Calculate scaling factors to fit all points within the canvas
        val xValues = pointsList.map { it.x }
        val yValues = pointsList.map { it.y }

        val minX = xValues.minOrNull() ?: 0f
        val maxX = xValues.maxOrNull() ?: 0f
        val minY = yValues.minOrNull() ?: 0f
        val maxY = yValues.maxOrNull() ?: 0f
        val xRange = maxX - minX
        val yRange = maxY - minY

        val widthPx = with(LocalDensity.current) { 200.dp.toPx() }
        val heightPx = with(LocalDensity.current) {762.dp.toPx()}

        val xScale = (widthPx - 2) / xRange.coerceAtLeast(1f)
        val yScale = 30f

        // Include padding to avoid edges
        val padding = 20f
        val scaledX = (coordinate.x - minX) * xScale + padding
        val scaledY = ((coordinate.y - minY) * yScale + padding)

        PositionedTaskPoint(scaledX = scaledX, scaledY = scaledY) {
            TaroPathTaskPoint(task = task)
        }

    }

}


fun generateCoordinates(totalCoordinates: Int): MutableList<Offset> {
    val x_distance = 15f
    val y_distance = 15f
    var coordinatesList: MutableList<Offset> = mutableListOf(Offset(0f, 0f))

    for (i in 1 until totalCoordinates) {
        val prevCoordinates = coordinatesList[i-1]

        // Calculate new position
        // For zig-zag: we're alternating left and right (+x and -x)
        // while consistently moving down (+y)

        val direction = if (i % 2 == 0) -1 else 1

        val random_x_offset = Random.nextFloat() * x_distance

        val new_y = prevCoordinates.y + y_distance
        val new_x = prevCoordinates.x + (random_x_offset * direction);
        val newCoordinates = Offset(new_x, new_y)
        coordinatesList.add(newCoordinates)
    }

    return coordinatesList
}