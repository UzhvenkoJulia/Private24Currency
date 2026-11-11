package com.example.private24

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.private24.ui.ExchangeArchiveScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                // Викликаємо екран без зайвого modifier, передаючи лише відступи Scaffold
                ExchangeArchiveScreen(
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}