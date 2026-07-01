package com.example.cashnova

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cashnova.ui.CashNovaApp
import com.example.cashnova.ui.CashNovaViewModel
import com.example.cashnova.ui.theme.CashNovaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: CashNovaViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            
            CashNovaTheme(themeMode = uiState.themeMode) {
                CashNovaApp(viewModel = viewModel)
            }
        }
    }
}
