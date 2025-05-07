package com.example.taro

import DateCardAdapter
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taro.Adapters.TaskListComposeAdapter
import com.example.taro.components.AddTaskPopUp
import com.example.taro.Dao.UserTaskDb
import com.example.taro.components.TaroPath.TaroPathPage
import kotlinx.coroutines.*
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDateTime
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


class TaroHomePage : ComponentActivity() {
    private lateinit var welcomeText: TextView
    private lateinit var DateComposeRecyclerView : RecyclerView
    private lateinit var adapter : DateCardAdapter

    private val taroManager : TaroTasksManager = TaroTasksManager()
    /** List Item Recycler View */
    private lateinit var  TaskListRecyclerView : RecyclerView
    private lateinit var TaskListAdapter : TaskListComposeAdapter

    private  lateinit var pathSelectedDate : String

    /** By Default it will hold the Previous and After 30 days. */
    private var dayContext: MutableMap<String, MutableList<Triple<String, String, String>>>? = null

    private fun centerOnDate(position: Int) {
        val itemWidthPx = (64 * resources.displayMetrics.density).toInt()
        val screenWidth = resources.displayMetrics.widthPixels
        val offset = (screenWidth / 2) - (itemWidthPx / 2)

        (DateComposeRecyclerView.layoutManager as? LinearLayoutManager)
            ?.scrollToPositionWithOffset(position, offset)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.taro_homepage)

        val today = LocalDateTime.now()
        val dayCards = generateDayContext(today).toMutableList()

        var taskPopUpComposeView = findViewById<androidx.compose.ui.platform.ComposeView>(R.id.taskAddPopUp)
        val headerComposeView = findViewById<androidx.compose.ui.platform.ComposeView>(R.id.headerNavBar)
        headerComposeView.setContent {
            com.example.taro.components.HeaderBar()
        }
        val taroPathIntent = Intent(this, TaroPathPage::class.java);

        val initializeTaskPopUp = findViewById<androidx.compose.ui.platform.ComposeView>(R.id.initializePopUp)


        super.onCreate(savedInstanceState)

        /** Default 30 days*/
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        /**
         *  Created A list of Triples based on the Previews 30 days and the Upcoming 30 Days
         *  It most Have : Month , DateNumber, Day of the Week
         */


        /**Mocking Database **/

        DateComposeRecyclerView = findViewById(R.id.DateRecyclerView);

        adapter = DateCardAdapter(
            items = dayCards,
            selectedDate = 5 // Initial center index
        ) { index, _ ->
            // Calculate the new center date from the clicked card
            val oldCenterDate = adapter.items[5] // Middle item before update
            val daysFromNow = index - 5L

            val newCenterDate = generateDateFromTriple(oldCenterDate).plusDays(daysFromNow)

            // Generate new 11-day window around the selected date
            val newDayCards = generateDayContext(newCenterDate)

            // Update adapter with new list and reselect center item
            adapter.updateItems(newDayCards, 5)

            // Recenter scroll view
            DateComposeRecyclerView.post {
                centerOnDate(5)
            }

            // Fetch and display tasks for the new selected date
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val selectedDate = newCenterDate.format(formatter)

            pathSelectedDate = selectedDate;

            mockFetch(this, selectedDate) { taskData ->
                TaskListAdapter = TaskListComposeAdapter(taskData)
                TaskListRecyclerView.adapter = TaskListAdapter
            }
        }





        DateComposeRecyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        DateComposeRecyclerView.adapter = adapter;

        val selectedIndex = 5
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        DateComposeRecyclerView.layoutManager = layoutManager
        DateComposeRecyclerView.adapter = adapter

        DateComposeRecyclerView.post {
            val itemWidthPx = (64 * resources.displayMetrics.density).toInt()
            val screenWidth = resources.displayMetrics.widthPixels
            val offset = (screenWidth / 2) - (itemWidthPx / 2)
            (DateComposeRecyclerView.layoutManager as? LinearLayoutManager)
                ?.scrollToPositionWithOffset(5, offset)
        }




        TaskListRecyclerView = findViewById(R.id.TaskListRecyclerView);

        TaskListRecyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        initializeTaskPopUp.setContent{

            Box(modifier = Modifier.fillMaxSize()) {

                // Centered StartTasksButton
                LargeFloatingActionButton(
                    onClick = {
                        taroPathIntent.putExtra("selectedDate",pathSelectedDate)
                        startActivity(taroPathIntent) },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(24.dp),
                    containerColor = Color(0xFF4DDEFD)

                ) {
                    Icon(Icons.Filled.PlayCircle, "Start Tasks")
                }

                // FloatingActionButton at the bottom end
                FloatingActionButton(
                    onClick = {
                        taskPopUpComposeView.setContent {
                            AddTaskPopUp(onDismissRequest = {
                                taskPopUpComposeView.setContent {}
                            })
                        }

                    },
                    containerColor = Color(0xFF00BFFF),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(24.dp)
                ) {
                    Text("+", fontSize = 24.sp, color = Color.White)
                }
            }



        }
        /* Deleted Welcome Message

        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val username = document.getString("username") ?: "User"
                        welcomeText.text = "Welcome, $username!"
                    } else {
                        welcomeText.text = "Welcome!"
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    welcomeText.text = "Welcome!"
                    Toast.makeText(this, "Failed to load user: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            welcomeText.text = "Welcome!"
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        } */

    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun generateDateFromTriple(triple: Triple<String, String, String>): LocalDateTime {
    val day = triple.first.toInt()
    val month = Month.valueOf(triple.second.uppercase(Locale.getDefault()))
    val year = LocalDateTime.now().year
    return LocalDateTime.of(year, month, day, 0, 0)
}

@RequiresApi(Build.VERSION_CODES.O)
fun generateDayContext(centerDate: LocalDateTime): List<Triple<String, String, String>> {
    val fullList = mutableListOf<Triple<String, String, String>>()

    for (i in -5..5) {
        val date = centerDate.plusDays(i.toLong())
        val month = date.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        val day = date.dayOfMonth.toString().padStart(2, '0')
        val weekday = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        fullList.add(Triple(day, month, weekday))
    }
    return fullList
}

/** Adds Tasks to the Database **/
@RequiresApi(Build.VERSION_CODES.O)
suspend fun mockDatabase(context: Context) {
    val taskManager = TaroTasksManager()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val formattedDueDate = LocalDateTime.now().format(formatter)

    val taskList = listOf(
        UserTaskDb(name = "Do Dishes", difficulty = 2, priority = 5, urgency = 3, expectedDuration = 0.5, isCompleted = false, dueDate = formattedDueDate),
        UserTaskDb(name = "Linear", difficulty = 5, priority = 5, urgency = 4, expectedDuration = 2.5, isCompleted = false, dueDate = formattedDueDate),

        UserTaskDb(name = "Gym Workout", difficulty = 5, priority = 5, urgency = 2, expectedDuration = 3.5, isCompleted = false, dueDate = formattedDueDate),
    )

    withContext(Dispatchers.IO) {
        taskManager.insertUserTasks(context, taskList)
    }
}


fun mockFetch(context: Context, date: String, callback: (List<Pair<String, Boolean>>) -> Unit)
{
    val taskManager = TaroTasksManager()

    CoroutineScope(Dispatchers.IO).launch {
        val tasks = taskManager.getTasksByDate(context, date)
        val dummyData = tasks.map { it.name to it.isCompleted }

        withContext(Dispatchers.Main) {
            callback(dummyData)
        }
    }

}