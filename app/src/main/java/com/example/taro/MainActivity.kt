package com.example.taro

import android.content.Intent
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
import com.example.taro.components.TaroPath.TaroPath
import com.example.taro.components.TaroPath.TaroPathPage
import com.example.taro.ui.theme.TaroTheme
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val db = FirebaseFirestore.getInstance()
        super.onCreate(savedInstanceState)

        val TaroQuotesView = R.layout.taro_quotes;
        val TaroInitialQuestionareView = R.layout.initial_questionarie;
        enableEdgeToEdge()
        setContentView(TaroInitialQuestionareView);

        val YesBtn = findViewById<Button>(R.id.YesBtn);
        val NoBtn = findViewById<Button>(R.id.NoBtn);

        val homeIntent = Intent(this,TaroHomePage::class.java)

        val taroPathIntent = Intent(this, TaroPathPage::class.java);

        NoBtn.setOnClickListener{
            startActivity(taroPathIntent);
        }
        YesBtn.setOnClickListener{
            startActivity(homeIntent)
        }

        /** Overwriting for testing purposes
        NoBtn.setOnClickListener{
            setContentView(TaroQuotesView);
            val intent1 = Intent(this, TaroLogin::class.java)
            startActivity(intent1)
        }
        YesBtn.setOnClickListener{
            val intent = Intent(this, TaroRegister::class.java)
            startActivity((intent))

        } **/
    }

    override fun onStart() {
        super.onStart()
        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        if (currentUser != null){
            val intent = Intent(this, TaroHomePage::class.java)
            startActivity(intent)
            finish()
        }
    }
}

