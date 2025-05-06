package com.example.taro.components.TaroPath

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.ComposeView
import com.example.taro.R

class TaroPathPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set content view first
        setContentView(R.layout.taro_path_page)
        // Then find ComposeView and set its content
        val taskPathComposeView = findViewById<ComposeView>(R.id.taskPath)
        taskPathComposeView.setContent {

            TaroPathScreen(listOf())
        }
    }
}