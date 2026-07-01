package com.example.cashnova.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cashnova.data.CashNovaUiState
import com.example.cashnova.data.SavingGoal
import com.example.cashnova.ui.components.AddSavingGoalDialog
import com.example.cashnova.ui.components.DepositDialog
import com.example.cashnova.ui.components.SavingLargeCard
import com.example.cashnova.ui.components.ScreenHeader

@Composable
fun SavingsScreen(
    state: CashNovaUiState,
    onBack: () -> Unit,
    onAddGoal: (
        title: String,
        target: Double,
        initial: Double,
        daysLeft: Int
    ) -> Unit,
    onDeposit: (goalId: Long, amount: Double) -> Unit,
    onDeleteGoal: (Long) -> Unit
) {
    var showAddGoal by remember { mutableStateOf(false) }
    var selectedGoal by remember { mutableStateOf<SavingGoal?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ScreenHeader(
                title = "Savings",
                onBack = onBack,
                action = {
                    IconButton(
                        onClick = { showAddGoal = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah target"
                        )
                    }
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
                bottom = 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(
                items = state.savings,
                key = { it.id }
            ) { goal ->
                SavingLargeCard(
                    goal = goal,
                    onDeposit = { selectedGoal = goal },
                    onDelete = { onDeleteGoal(goal.id) }
                )
            }
        }
    }

    if (showAddGoal) {
        AddSavingGoalDialog(
            onDismiss = { showAddGoal = false },
            onSave = onAddGoal
        )
    }

    selectedGoal?.let { goal ->
        DepositDialog(
            goal = goal,
            availableBalance = state.totalBalance,
            onDismiss = { selectedGoal = null },
            onDeposit = { amount ->
                onDeposit(goal.id, amount)
            }
        )
    }
}
