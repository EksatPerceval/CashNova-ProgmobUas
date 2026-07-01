package com.example.cashnova.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cashnova.data.CashNovaUiState
import com.example.cashnova.ui.analytics.ChartTimeRange
import com.example.cashnova.ui.analytics.buildFinanceChartData
import com.example.cashnova.ui.components.IncomeExpenseDonutChart
import com.example.cashnova.ui.components.MonthlyIncomeExpenseChart
import com.example.cashnova.ui.components.ScreenHeader

@Composable
fun AnalyticsScreen(
    state: CashNovaUiState,
    onBack: () -> Unit
) {
    var selectedWalletId by remember { mutableStateOf(-1L) } // -1L for "All Wallets"
    var selectedRange by remember { mutableStateOf(ChartTimeRange.SIX_MONTHS) }

    val filteredTransactions = remember(state.transactions, selectedWalletId) {
        if (selectedWalletId == -1L) {
            state.transactions
        } else {
            state.transactions.filter { it.walletId == selectedWalletId }
        }
    }

    val chartData = remember(filteredTransactions, selectedRange) {
        buildFinanceChartData(
            transactions = filteredTransactions,
            range = selectedRange
        )
    }

    val chartTitle = when (selectedRange) {
        ChartTimeRange.ONE_WEEK -> "Grafik 1 Minggu"
        ChartTimeRange.ONE_MONTH -> "Grafik 1 Bulan"
        ChartTimeRange.THREE_MONTHS -> "Grafik 3 Bulan"
        ChartTimeRange.SIX_MONTHS -> "Grafik 6 Bulan"
        ChartTimeRange.ONE_YEAR -> "Grafik 1 Tahun"
        ChartTimeRange.ALL -> "Semua Data"
    }

    // Hitung total untuk periode yang dipilih
    val totalIncome = remember(chartData) { chartData.sumOf { it.income } }
    val totalExpense = remember(chartData) { chartData.sumOf { it.expense } }

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
                Column {
                    Text(
                        text = "Analisis Pemasukan dan Pengeluaran",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Filter berdasarkan wallet:",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedWalletId == -1L,
                        onClick = { selectedWalletId = -1L },
                        label = { Text("Semua Wallet") }
                    )
                    
                    state.wallets.forEach { wallet ->
                        FilterChip(
                            selected = selectedWalletId == wallet.id,
                            onClick = { selectedWalletId = wallet.id },
                            label = { Text(wallet.name) }
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Rentang Waktu:",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TimeRangeChip(ChartTimeRange.ONE_WEEK, "1 Mgu", selectedRange) { selectedRange = it }
                    TimeRangeChip(ChartTimeRange.ONE_MONTH, "1 Bln", selectedRange) { selectedRange = it }
                    TimeRangeChip(ChartTimeRange.THREE_MONTHS, "3 Bln", selectedRange) { selectedRange = it }
                    TimeRangeChip(ChartTimeRange.SIX_MONTHS, "6 Bln", selectedRange) { selectedRange = it }
                    TimeRangeChip(ChartTimeRange.ONE_YEAR, "1 Thn", selectedRange) { selectedRange = it }
                    TimeRangeChip(ChartTimeRange.ALL, "Semua", selectedRange) { selectedRange = it }
                }
            }

            item {
                MonthlyIncomeExpenseChart(
                    data = chartData,
                    title = chartTitle
                )
            }

            item {
                // Sekarang menampilkan ringkasan TOTAL dari rentang waktu yang dipilih
                IncomeExpenseDonutChart(
                    income = totalIncome,
                    expense = totalExpense
                )
            }
            
            item {
                Text(
                    text = "Data diperbarui otomatis dari transaksi yang tersimpan.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun TimeRangeChip(
    range: ChartTimeRange,
    label: String,
    selectedRange: ChartTimeRange,
    onClick: (ChartTimeRange) -> Unit
) {
    FilterChip(
        selected = selectedRange == range,
        onClick = { onClick(range) },
        label = { Text(label) }
    )
}
