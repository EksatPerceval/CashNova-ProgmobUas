package com.example.cashnova.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cashnova.data.CashNovaUiState
import com.example.cashnova.data.EarningSource
import com.example.cashnova.data.TransactionType
import com.example.cashnova.ui.components.AddTransactionDialog
import com.example.cashnova.ui.components.BalanceCard
import com.example.cashnova.ui.components.IncomeExpenseCard
import com.example.cashnova.ui.components.SavingCompactCard
import com.example.cashnova.ui.components.SectionTitle
import com.example.cashnova.ui.components.TransactionRow
import com.example.cashnova.ui.theme.CashNovaBlue
import com.example.cashnova.ui.theme.CashNovaMuted
import com.example.cashnova.ui.theme.CashNovaPink
import com.example.cashnova.ui.theme.CashNovaRed
import com.example.cashnova.ui.util.formatMoney

@Composable
fun DashboardScreen(
    state: CashNovaUiState,
    onOpenSettings: () -> Unit,
    onOpenSavings: () -> Unit,
    onOpenWallet: () -> Unit,
    onOpenAnalytics: () -> Unit,
    onAddTransaction: (
        title: String,
        subtitle: String,
        amount: Double,
        type: TransactionType,
        category: String
    ) -> Unit
) {
    var showAddTransaction by remember {
        mutableStateOf(false)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    showAddTransaction = true
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                },
                text = {
                    Text("Transaksi")
                }
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.statusBars),
            contentPadding = PaddingValues(
                start = 24.dp,
                top = 16.dp,
                end = 24.dp,
                bottom = 112.dp
            )
        ) {
            item {
                ProfileHeader(
                    name = state.profileName,
                    onOpenSettings = onOpenSettings
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )
            }

            item {
                BalanceCard(
                    balance = state.totalBalance,
                    onClick = onOpenWallet
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(14.dp)
                ) {
                    IncomeExpenseCard(
                        title = "Pemasukan",
                        amount = state.totalIncome,
                        isIncome = true,
                        modifier = Modifier.weight(1f)
                    )

                    IncomeExpenseCard(
                        title = "Pengeluaran",
                        amount = state.totalExpense,
                        isIncome = false,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(
                    modifier = Modifier.height(28.dp)
                )
            }

            item {
                AnalyticsShortcutCard(
                    onClick = onOpenAnalytics
                )

                Spacer(
                    modifier = Modifier.height(34.dp)
                )
            }

            item {
                SectionTitle(
                    title = "Pendapatan",
                    onSeeAll = onOpenWallet
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                EarningsRow(
                    earnings = state.earningSources
                )

                Spacer(
                    modifier = Modifier.height(34.dp)
                )
            }

            item {
                SectionTitle(
                    title = "Tabungan",
                    onSeeAll = onOpenSavings
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Row(
                    modifier = Modifier.horizontalScroll(
                        rememberScrollState()
                    ),
                    horizontalArrangement =
                        Arrangement.spacedBy(14.dp)
                ) {
                    state.savings
                        .take(4)
                        .forEach { goal ->

                            SavingCompactCard(
                                goal = goal,
                                modifier = Modifier.width(174.dp),
                                onClick = onOpenSavings
                            )
                        }
                }

                Spacer(
                    modifier = Modifier.height(34.dp)
                )
            }

            item {
                SectionTitle(
                    title = "Transaksi",
                    onSeeAll = onOpenWallet
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Text(
                    text = "Terbaru",
                    color = CashNovaMuted,
                    fontSize = 14.sp
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }

            items(
                items = state.transactions.take(4),
                key = { transaction ->
                    transaction.id
                }
            ) { transaction ->

                TransactionRow(
                    transaction = transaction,
                    onClick = onOpenWallet
                )

                Spacer(
                    modifier = Modifier.height(11.dp)
                )
            }
        }
    }

    if (showAddTransaction) {
        AddTransactionDialog(
            onDismiss = {
                showAddTransaction = false
            },
            onSave = onAddTransaction
        )
    }
}

@Composable
private fun ProfileHeader(
    name: String,
    onOpenSettings: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(Color(0xFFD1D2D6)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profil",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(
            modifier = Modifier.width(14.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Selamat datang!",
                color = CashNovaMuted,
                fontSize = 13.sp
            )

            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge
            )
        }

        IconButton(
            onClick = onOpenSettings
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Pengaturan",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

/**
 * Kartu untuk membuka halaman grafik.
 */
@Composable
private fun AnalyticsShortcutCard(
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(
                        CashNovaBlue.copy(alpha = 0.12f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                MiniBarChartIcon()
            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Grafik Keuangan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    text = "Lihat pemasukan dan pengeluaran",
                    color = CashNovaMuted,
                    fontSize = 12.sp
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Buka grafik",
                tint = CashNovaMuted
            )
        }
    }
}

/**
 * Ikon grafik sederhana tanpa material-icons-extended.
 */
@Composable
private fun MiniBarChartIcon() {
    Row(
        modifier = Modifier
            .size(
                width = 28.dp,
                height = 26.dp
            ),
        horizontalArrangement =
            Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Box(
            modifier = Modifier
                .width(6.dp)
                .height(11.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 3.dp,
                        topEnd = 3.dp
                    )
                )
                .background(CashNovaBlue)
        )

        Box(
            modifier = Modifier
                .width(6.dp)
                .height(19.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 3.dp,
                        topEnd = 3.dp
                    )
                )
                .background(CashNovaBlue)
        )

        Box(
            modifier = Modifier
                .width(6.dp)
                .height(25.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 3.dp,
                        topEnd = 3.dp
                    )
                )
                .background(CashNovaBlue)
        )
    }
}

/**
 * Menampilkan kartu sumber pendapatan.
 *
 * Fungsi ini sebelumnya hilang sehingga EarningsRow
 * menjadi unresolved reference.
 */
@Composable
private fun EarningsRow(
    earnings: List<EarningSource>
) {
    val colors = listOf(
        CashNovaRed,
        CashNovaPink,
        CashNovaBlue
    )

    Row(
        modifier = Modifier.horizontalScroll(
            rememberScrollState()
        ),
        horizontalArrangement =
            Arrangement.spacedBy(14.dp)
    ) {
        earnings.forEachIndexed { index, earning ->

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor =
                        colors[index % colors.size]
                ),
                modifier = Modifier
                    .width(132.dp)
                    .height(154.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                Color.White.copy(
                                    alpha = 0.18f
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = earning.letter,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = earning.name,
                        color = Color.White.copy(
                            alpha = 0.82f
                        ),
                        fontSize = 13.sp
                    )

                    Text(
                        text = formatMoney(earning.amount),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}