package com.example.cashnova.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Tune
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
import com.example.cashnova.data.FinanceTransaction
import com.example.cashnova.data.TransactionType
import com.example.cashnova.ui.components.AddTransactionDialog
import com.example.cashnova.ui.components.ScreenHeader
import com.example.cashnova.ui.components.SectionTitle
import com.example.cashnova.ui.components.TransactionDetailDialog
import com.example.cashnova.ui.components.TransactionRow
import com.example.cashnova.ui.theme.CashNovaDark
import com.example.cashnova.ui.theme.CashNovaMuted
import com.example.cashnova.ui.util.formatMoney

@Composable
fun WalletScreen(
    state: CashNovaUiState,
    onBack: () -> Unit,
    onAddTransaction: (
        title: String,
        subtitle: String,
        amount: Double,
        type: TransactionType,
        category: String
    ) -> Unit,
    onDeleteTransaction: (Long) -> Unit,
    onOpenSavings: () -> Unit
) {
    var showAddTransaction by remember { mutableStateOf(false) }
    var selectedTransaction by remember {
        mutableStateOf<FinanceTransaction?>(null)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ScreenHeader(
                title = "Wallet",
                onBack = onBack,
                action = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddTransaction = true },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                },
                text = {
                    Text("Add")
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.navigationBars),
            contentPadding = PaddingValues(
                start = 24.dp,
                top = 12.dp,
                end = 24.dp,
                bottom = 110.dp
            ),
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            item {
                WalletCard(
                    name = state.profileName,
                    balance = state.totalBalance
                )
                Spacer(modifier = Modifier.height(28.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Transactions",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Filter",
                        tint = CashNovaMuted
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            items(
                items = state.transactions,
                key = { it.id }
            ) { transaction ->
                TransactionRow(
                    transaction = transaction,
                    onClick = { selectedTransaction = transaction }
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                SectionTitle(
                    title = "Savings",
                    onSeeAll = onOpenSavings
                )
                Spacer(modifier = Modifier.height(12.dp))

                state.savings.take(3).forEach { goal ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .clickable(onClick = onOpenSavings)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF0F1F5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = null
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = goal.title,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${formatMoney(goal.currentAmount)} / ${formatMoney(goal.targetAmount)}",
                                color = CashNovaMuted,
                                fontSize = 12.sp
                            )
                        }

                        Text(
                            text = "${(goal.progress * 100).toInt()}%",
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(9.dp))
                }
            }
        }
    }

    if (showAddTransaction) {
        AddTransactionDialog(
            onDismiss = { showAddTransaction = false },
            onSave = onAddTransaction
        )
    }

    selectedTransaction?.let { transaction ->
        TransactionDetailDialog(
            transaction = transaction,
            onDismiss = { selectedTransaction = null },
            onDelete = {
                onDeleteTransaction(transaction.id)
            }
        )
    }
}

@Composable
private fun WalletCard(
    name: String,
    balance: Double
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Total Balance",
                        color = CashNovaMuted,
                        fontSize = 12.sp
                    )
                    Text(
                        text = formatMoney(balance, showCents = true),
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Icon(
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = null,
                    tint = CashNovaDark
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "1234    ••••    ••••    3456",
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Name",
                        color = CashNovaMuted,
                        fontSize = 10.sp
                    )
                    Text(
                        text = name,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Exp",
                        color = CashNovaMuted,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "09/29",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
