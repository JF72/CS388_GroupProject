package com.example.taro

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.example.taro.ui.theme.TaroTheme
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val db = FirebaseFirestore.getInstance()
        val testData = hashMapOf(
            "title" to "Test Chore",
            "assignedTo" to "TestUser123",
            "completed" to false
        )
        db.collection("chores").add(testData).addOnSuccessListener { documentReference ->
            Log.d("FirestoreTest", "DocumentSnapshot added with ID: ${documentReference.id}")
        }.addOnFailureListener { e ->
            Log.w("FirestoreTest", "Error adding document", e)
        }
        super.onCreate(savedInstanceState)

        val TaroQuotesView = R.layout.taro_quotes;
        val TaroInitialQuestionareView = R.layout.initial_questionarie;
        enableEdgeToEdge()
        setContentView(TaroInitialQuestionareView);

        val YesBtn = findViewById<Button>(R.id.YesBtn);
        val NoBtn = findViewById<Button>(R.id.NoBtn);

        NoBtn.setOnClickListener{
            setContentView(TaroQuotesView);
        }
        YesBtn.setOnClickListener{
            Toast.makeText(it.context, "Login Page has not been set up!", Toast.LENGTH_SHORT).show()

        }
    }
}

