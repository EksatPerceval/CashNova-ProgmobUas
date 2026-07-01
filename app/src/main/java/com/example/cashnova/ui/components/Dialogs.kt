package com.example.cashnova.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.cashnova.data.FinanceTransaction
import com.example.cashnova.data.SavingGoal
import com.example.cashnova.data.TransactionType
import com.example.cashnova.ui.util.formatMoney
import com.example.cashnova.ui.util.formatTransactionDate

@Composable
fun AddTransactionDialog(
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        subtitle: String,
        amount: Double,
        type: TransactionType,
        category: String
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Other") }
    var type by remember { mutableStateOf(TransactionType.EXPENSE) }

    val amount = amountText.toDoubleOrNull() ?: 0.0
    val isValid = title.isNotBlank() && amount > 0.0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add Transaction")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilterChip(
                        selected = type == TransactionType.INCOME,
                        onClick = { type = TransactionType.INCOME },
                        label = { Text("Income") }
                    )
                    FilterChip(
                        selected = type == TransactionType.EXPENSE,
                        onClick = { type = TransactionType.EXPENSE },
                        label = { Text("Expense") }
                    )
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = subtitle,
                    onValueChange = { subtitle = it },
                    label = { Text("Description") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it.filter { char ->
                            char.isDigit() || char == '.'
                        }
                    },
                    label = { Text("Amount") },
                    prefix = { Text("Rp") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        title,
                        subtitle,
                        amount,
                        type,
                        category
                    )
                    onDismiss()
                },
                enabled = isValid
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddSavingGoalDialog(
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        target: Double,
        initial: Double,
        daysLeft: Int
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var targetText by remember { mutableStateOf("") }
    var initialText by remember { mutableStateOf("0") }
    var daysText by remember { mutableStateOf("30") }

    val target = targetText.toDoubleOrNull() ?: 0.0
    val initial = initialText.toDoubleOrNull() ?: 0.0
    val days = daysText.toIntOrNull() ?: 30
    val isValid = title.isNotBlank() && target > 0.0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("New Saving Goal")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Goal name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = targetText,
                    onValueChange = {
                        targetText = it.filter { char ->
                            char.isDigit() || char == '.'
                        }
                    },
                    label = { Text("Target amount") },
                    prefix = { Text("Rp") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = initialText,
                    onValueChange = {
                        initialText = it.filter { char ->
                            char.isDigit() || char == '.'
                        }
                    },
                    label = { Text("Initial balance") },
                    prefix = { Text("Rp") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = daysText,
                    onValueChange = {
                        daysText = it.filter(Char::isDigit)
                    },
                    label = { Text("Days left") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(title, target, initial, days)
                    onDismiss()
                },
                enabled = isValid
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DepositDialog(
    goal: SavingGoal,
    availableBalance: Double,
    onDismiss: () -> Unit,
    onDeposit: (Double) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    val amount = amountText.toDoubleOrNull() ?: 0.0
    val remaining = (goal.targetAmount - goal.currentAmount).coerceAtLeast(0.0)
    val isValid = amount > 0.0 &&
        amount <= remaining &&
        amount <= availableBalance

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Deposit to ${goal.title}")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Remaining: ${formatMoney(remaining)}")
                Text("Available balance: ${formatMoney(availableBalance)}")

                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it.filter { char ->
                            char.isDigit() || char == '.'
                        }
                    },
                    label = { Text("Deposit amount") },
                    prefix = { Text("Rp") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (amount > remaining) {
                    Text("Amount exceeds the remaining target.")
                } else if (amount > availableBalance) {
                    Text("Insufficient balance.")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDeposit(amount)
                    onDismiss()
                },
                enabled = isValid
            ) {
                Text("Deposit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun TransactionDetailDialog(
    transaction: FinanceTransaction,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(transaction.title)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(transaction.subtitle)
                Text("Category: ${transaction.category}")
                Text("Date: ${formatTransactionDate(transaction.createdAt)}")
                Text(
                    "Amount: ${
                        if (transaction.type == TransactionType.INCOME) "+" else "-"
                    }${formatMoney(transaction.amount)}"
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDelete()
                    onDismiss()
                }
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
