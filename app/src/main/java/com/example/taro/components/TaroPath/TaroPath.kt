package com.example.taro.components.TaroPath

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.random.Random

data class Task(
    val title: String,
    val description: String,
    val status: String
)
@Composable
fun TaroPathScreen(sortedTasks: List<Task>) {
    // Sample tasks
    val tasks = listOf(
        Task("Complete Project Planning", "Define project scope and requirements", "Completed"),
        Task("Design UI Mockups", "Create wireframes for the main screens", "In Progress"),
        Task("Implement Authentication", "Set up user login and registration", "Not Started"),
        Task("Implement Authentication", "Set up user login and registration", "Not Started"),
        Task("Implement Authentication", "Set up user login and registration", "Not Started"),
        Task("Implement Authentication", "Set up user login and registration", "Not Started")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        if (sortedTasks.isNotEmpty()){
            PathWithTasks(sortedTasks)
        }else{
            PathWithTasks(tasks)
        }

    }
}

@Composable
fun PathWithTasks(tasks: List<Task>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height((200 * tasks.size).dp)
    ) {
        TaroPath(tasks.size, Modifier.fillMaxSize())
    }
}

@Composable
fun TaroPath(pointCount: Int, modifier: Modifier = Modifier) {
    val pointsList: MutableList<Offset> = generateCoordinates(pointCount.coerceAtLeast(3))
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
    pointsList.forEach { coordinate ->
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
            TaroPathTaskPoint()
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