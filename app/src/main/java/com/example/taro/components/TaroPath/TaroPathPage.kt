package com.example.taro.components.TaroPath

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import com.example.taro.Dao.UserTaskDb
import com.example.taro.R
import com.example.taro.TaroHomePage
import com.example.taro.TaroTasksManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaroPathPage : ComponentActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set content view first
        val taroQuotesView = R.layout.taro_quotes;


        setContentView(taroQuotesView);

        Handler(Looper.getMainLooper()).postDelayed({
            setContentView(R.layout.taro_path_page)
            val context = this

            // Then find ComposeView and set its content
            val headerComposeView = findViewById<androidx.compose.ui.platform.ComposeView>(R.id.headerNavBar)
            headerComposeView.setContent {
                com.example.taro.components.HeaderBar(onProfileClick ={
                    val intent = Intent(context, TaroHomePage::class.java)
                    context.startActivity(intent)
                })
            }
            val taskPathComposeView = findViewById<ComposeView>(R.id.taskPath)


            val pathSelectedDate = intent.getStringExtra("selectedDate");
            // Use remember and mutableStateOf for state management
            if (pathSelectedDate != null) {
                Log.d("DATE SELECTED",pathSelectedDate);
            }
            taskPathComposeView.setContent {
                // Pass the list to the Composable function
                if (pathSelectedDate != null) {
                    TaroPathScreen(pathSelectedDate)
                }
            }
        }, 3000)




    }
}

