package com.example.equityspin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.equityspin.ui.theme.EquityspinTheme
import com.google.firebase.FirebaseApp



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Initialize the Firebase for authentication, database storage, ...
        FirebaseApp.initializeApp(this)
//         For navigation and status bars
        enableEdgeToEdge()
//        The composable content for the activity
//        With this we dont need to use XML layouts, we just use JetpackCompose UI
        setContent {
            EquityspinTheme {
                MainScreen() // Launch the main UI entry point of the app
            }
        }
    }
}