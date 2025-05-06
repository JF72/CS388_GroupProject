package com.example.taro

import DateCardAdapter
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taro.Adapters.TaskListComposeAdapter
import com.example.taro.components.AddTaskPopUp
import com.example.taro.Dao.UserTaskDb
import kotlinx.coroutines.*
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDateTime
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

    /** By Default it will hold the Previous and After 30 days. */
    private var dayContext: MutableMap<String, MutableList<Triple<String, String, String>>>? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.taro_homepage)


        var taskPopUpComposeView = findViewById<androidx.compose.ui.platform.ComposeView>(R.id.taskAddPopUp)
        val headerComposeView = findViewById<androidx.compose.ui.platform.ComposeView>(R.id.headerNavBar)
        headerComposeView.setContent {
            com.example.taro.components.HeaderBar()
        }
        val initializeTaskPopUp = findViewById<androidx.compose.ui.platform.ComposeView>(R.id.initializePopUp)
        initializeTaskPopUp.setContent{
            Box(modifier = Modifier.fillMaxSize()) {
                FloatingActionButton(
                    onClick = {
                        // Trigger dialog or new task popup
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

        super.onCreate(savedInstanceState)

        /** Default 30 days*/



        val userId = FirebaseAuth.getInstance().currentUser?.uid


        /**
         *  Created A list of Triples based on the Previews 30 days and the Upcoming 30 Days
         *  It most Have : Month , DateNumber, Day of the Week
         */

//
//        val data = mutableListOf(
//            Triple("11", "April", "Friday"),
//            Triple("12", "May", "Saturday"),
//            Triple("13", "June", "Sunday"),
//            Triple("13", "June", "Sunday"),
//            Triple("13", "June", "Sunday"),
//            Triple("13", "June", "Sunday"),
//            Triple("13", "June", "Sunday")
//        )

//        val concatenatedList = mutableListOf<Triple<String, String, String>>().apply {
//            (dayContext?.get("PreviousDays") as? MutableList<Triple<String, String, String>>)?.let { addAll(it) }
//            (dayContext?.get("UpcomingDays") as? MutableList<Triple<String, String, String>>)?.let { addAll(it) }
//        }
//        Log.e("ConcatenatedList",concatenatedList.size.toString())

        /** Auto Generate Tasks  to the Database **/
        CoroutineScope(Dispatchers.IO).launch{
        }

        /**Mocking Database **/

        DateComposeRecyclerView = findViewById(R.id.DateRecyclerView);
//        adapter = DateCardAdapter(concatenatedList);
        val dayCards = generateDayContext().toMutableList()
        adapter = DateCardAdapter(
            items = dayCards,
            selectedDate = 5 // Today
        ) { index, selectedDateTriple ->
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val selectedDate = LocalDateTime.now().plusDays(index - 5L).format(formatter)

            // Fetch tasks for this specific date
//            mockFetch(this, selectedDate) { taskData ->
//                TaskListAdapter = TaskListComposeAdapter(taskData)
//                TaskListRecyclerView.adapter = TaskListAdapter
//            }
        }


        DateComposeRecyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        DateComposeRecyclerView.adapter = adapter;


        TaskListRecyclerView = findViewById(R.id.TaskListRecyclerView);

        TaskListRecyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        mockFetch(this) { dummyData ->
            // Now you can use taskListDummyData here
            // You can now pass this to your adapter or whatever you need
            TaskListAdapter = TaskListComposeAdapter(dummyData);
            TaskListRecyclerView.adapter = TaskListAdapter
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
fun generateDayContext() : List<Triple<String, String, String>> {
    val today = LocalDateTime.now()
    val fullList = mutableListOf<Triple<String, String, String>>()

    /** Might need performance update*/
    for (i in -5..5 ){
        val date = today.plusDays(i.toLong())
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

fun mockFetch(context: Context, callback: (List<Pair<String, Boolean>>) -> Unit) {
    val taskManager = TaroTasksManager()

    CoroutineScope(Dispatchers.IO).launch {
        val tasks = taskManager.getTasksByDate(context,"2025-05-05")
        val dummyData = tasks.map { it.name to it.isCompleted }

        withContext(Dispatchers.Main) {
            callback(dummyData) // Return to caller on Main thread
        }
    }
}