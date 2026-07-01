package com.example.cashnova

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cashnova.ui.CashNovaApp
import com.example.cashnova.ui.theme.CashNovaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CashNovaTheme {
                CashNovaApp()
            }
        }
    }
}
