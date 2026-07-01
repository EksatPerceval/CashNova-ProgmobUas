package com.example.cashnova.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cashnova.data.CashNovaUiState
import com.example.cashnova.ui.analytics.buildMonthlyFinanceData
import com.example.cashnova.ui.components.IncomeExpenseDonutChart
import com.example.cashnova.ui.components.MonthlyIncomeExpenseChart
import com.example.cashnova.ui.components.ScreenHeader
import com.example.cashnova.ui.theme.CashNovaMuted

@Composable
fun AnalyticsScreen(
    state: CashNovaUiState,
    onBack: () -> Unit
) {
    val monthlyData = remember(
        state.transactions
    ) {
        buildMonthlyFinanceData(
            transactions = state.transactions,
            monthCount = 6
        )
    }

    val currentMonthData = monthlyData.lastOrNull()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ScreenHeader(
                title = "Grafik Keuangan",
                onBack = onBack
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(
                    WindowInsets.navigationBars
                ),
            contentPadding = PaddingValues(
                start = 20.dp,
                top = 12.dp,
                end = 20.dp,
                bottom = 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Text(
                    text = "Analisis Pemasukan dan Pengeluaran",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = "Data diperbarui otomatis dari transaksi yang tersimpan.",
                    color = CashNovaMuted,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            item {
                MonthlyIncomeExpenseChart(
                    data = monthlyData
                )
            }

            item {
                IncomeExpenseDonutChart(
                    income = currentMonthData?.income ?: 0.0,
                    expense = currentMonthData?.expense ?: 0.0
                )
            }
        }
    }
}