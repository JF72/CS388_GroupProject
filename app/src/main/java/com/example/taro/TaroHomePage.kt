package com.example.taro

import DateCardAdapter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taro.Adapters.TaskListComposeAdapter
import com.example.taro.components.AddTaskPopUp
import kotlinx.coroutines.*
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale


class TaroHomePage : ComponentActivity() {

    private lateinit var welcomeText: TextView
    private lateinit var DateComposeRecyclerView : RecyclerView
    private lateinit var adapter : DateCardAdapter

    /** List Item Recycler View */
    private lateinit var  TaskListRecyclerView : RecyclerView
    private lateinit var TaskListAdapter : TaskListComposeAdapter

    /** By Default it will hold the Previous and After 30 days. */
    private var dayContext: MutableMap<String, MutableList<Triple<String, String, String>>>? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.taro_homepage)

        var showAddTaskPopUp = true;

        var taskPopUpComposeView = findViewById<androidx.compose.ui.platform.ComposeView>(R.id.taskAddPopUp)
        val headerComposeView = findViewById<androidx.compose.ui.platform.ComposeView>(R.id.headerNavBar)
        headerComposeView.setContent {
            com.example.taro.components.HeaderBar()
        }

        super.onCreate(savedInstanceState)


        if(showAddTaskPopUp){
            taskPopUpComposeView.setContent{
                AddTaskPopUp (onDismissRequest = {
                    taskPopUpComposeView.setContent {}
                })
            }
        }

            /** Default 30 days*/
        dayContext =  generateDayContext(5) ;

        val userId = FirebaseAuth.getInstance().currentUser?.uid


        /**
         *  Created A list of Triples based on the Previews 30 days and the Upcoming 30 Days
         *  It most Have : Month , DateNumber, Day of the Week
         */


        val data = mutableListOf(
            Triple("11", "April", "Friday"),
            Triple("12", "May", "Saturday"),
            Triple("13", "June", "Sunday"),
            Triple("13", "June", "Sunday"),
            Triple("13", "June", "Sunday"),
            Triple("13", "June", "Sunday"),
            Triple("13", "June", "Sunday")
        )



        val concatenatedList = mutableListOf<Triple<String, String, String>>().apply {
            (dayContext?.get("PreviousDays") as? MutableList<Triple<String, String, String>>)?.let { addAll(it) }
            (dayContext?.get("UpcomingDays") as? MutableList<Triple<String, String, String>>)?.let { addAll(it) }
        }
        Log.e("ConcatenatedList",concatenatedList.size.toString())
        val taskListDummyData = listOf(
            Pair("Task1",false),
            Pair("Task2",false),
            Pair("Task3",false),
            Pair("Task4",false),
            Pair("Task5",false),
            Pair("Task6",false),
            Pair("Task7",false),
            Pair("Task8",false),
            Pair("Task9",false),
            Pair("Task10",false)


        )

        DateComposeRecyclerView = findViewById(R.id.DateRecyclerView);
        adapter = DateCardAdapter(concatenatedList);

        DateComposeRecyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        DateComposeRecyclerView.adapter = adapter;



        TaskListRecyclerView = findViewById(R.id.TaskListRecyclerView);
        TaskListAdapter = TaskListComposeAdapter(taskListDummyData);

        TaskListRecyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        TaskListRecyclerView.adapter = TaskListAdapter


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
fun generateDayContext(amount: Long) : MutableMap<String,MutableList<Triple<String,String,String>>>{


    val today = LocalDateTime.now()

    val previousDaysList : MutableList<Triple<String,String,String>> = mutableListOf() ;
    val upcomingDaysList : MutableList<Triple<String,String,String>> = mutableListOf();

    /** Might need performance update*/
    for (i in 1..amount ){
        val previousDate : LocalDateTime = today.minusDays(i);
        val nextDate : LocalDateTime = today.plusDays(i);

        val previousMonth : String = previousDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault());
        val previousDayName : String =previousDate.dayOfWeek.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault())
        val previousDayNumber : String = previousDate.dayOfMonth.toString().padStart(2 )

        val formattedPreviousDate : Triple<String,String,String> = Triple(previousMonth,previousDayName,previousDayNumber)


        val upcomingMonths : String = nextDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault());
        val upcomingDayName : String =nextDate.dayOfWeek.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault())
        val upcomingDayNumber : String = nextDate.dayOfMonth.toString().padStart(2 )

        val formattedUpcomingDate :  Triple<String,String,String> = Triple(upcomingMonths,upcomingDayName,upcomingDayNumber)

        previousDaysList.add(formattedPreviousDate);

        upcomingDaysList.add(formattedUpcomingDate);

    }


    val generatedContext: MutableMap<String, MutableList<Triple<String, String, String>>> = mutableMapOf();

    generatedContext["PreviousDays"] = previousDaysList;
    generatedContext["UpcomingDays"] = upcomingDaysList;
    Log.e("Fucntion CAll",previousDaysList[0].toString())

    return  generatedContext;


}
