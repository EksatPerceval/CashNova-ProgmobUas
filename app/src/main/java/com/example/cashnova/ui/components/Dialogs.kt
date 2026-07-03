package com.example.cashnova.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.cashnova.data.FinanceTransaction
import com.example.cashnova.data.SavingGoal
import com.example.cashnova.data.TransactionType
import com.example.cashnova.data.Wallet
import com.example.cashnova.ui.util.formatMoney
import com.example.cashnova.ui.util.formatTransactionDate

/*
 * Dialog tambah transaksi (income/expense).
 * Mendukung pemilihan kategori existing + penambahan kategori custom.
 */
@Composable
fun AddTransactionDialog(
    categories: List<String>,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        subtitle: String,
        amount: Double,
        type: TransactionType,
        category: String
    ) -> Unit,
    onAddCustomCategory: (String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(categories.firstOrNull() ?: "Other") }
    var type by remember { mutableStateOf(TransactionType.EXPENSE) }
    
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var showAddCustomCategory by remember { mutableStateOf(false) }
    var customCategoryName by remember { mutableStateOf("") }

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

                Column {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { },
                        label = { Text("Category") },
                        readOnly = true,
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { showAddCustomCategory = true }) {
                                    Icon(Icons.Default.Add, contentDescription = "Add Custom")
                                }
                                IconButton(onClick = { showCategoryDropdown = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Category")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCategoryDropdown = true }
                    )
                    
                    DropdownMenu(
                        expanded = showCategoryDropdown,
                        onDismissRequest = { showCategoryDropdown = false },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    showCategoryDropdown = false
                                }
                            )
                        }
                    }
                }
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

    if (showAddCustomCategory) {
        AlertDialog(
            onDismissRequest = { showAddCustomCategory = false },
            title = { Text("Add Custom Category") },
            text = {
                OutlinedTextField(
                    value = customCategoryName,
                    onValueChange = { customCategoryName = it },
                    label = { Text("Category Name") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customCategoryName.isNotBlank()) {
                            onAddCustomCategory(customCategoryName)
                            category = customCategoryName
                            customCategoryName = ""
                            showAddCustomCategory = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCustomCategory = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/*
 * Dialog tambah wallet baru.
 * Input: nama wallet + saldo awal.
 */
@Composable
fun AddWalletDialog(
    onDismiss: () -> Unit,
    onSave: (String, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var balanceText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Wallet") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Wallet Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = balanceText,
                    onValueChange = { balanceText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Initial Balance") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(name, balanceText.toDoubleOrNull() ?: 0.0)
                    onDismiss()
                },
                enabled = name.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/*
 * Dialog membuat target tabungan baru.
 * Meminta nama target, nominal target, saldo awal, dan estimasi hari.
 */
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

/*
 * Dialog setor dana ke target tabungan.
 *
 * Validasi utama:
 * - nominal > 0
 * - tidak melebihi sisa target
 * - tidak melebihi saldo tersedia
 */
@Composable
fun DepositDialog(
    goal: SavingGoal,
    availableBalance: Double,
    categories: List<String>,
    onDismiss: () -> Unit,
    onDeposit: (Double, String) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Saving") }
    var showCategoryDropdown by remember { mutableStateOf(false) }

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

                Column {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { },
                        label = { Text("Category") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showCategoryDropdown = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Category")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCategoryDropdown = true }
                    )
                    
                    DropdownMenu(
                        expanded = showCategoryDropdown,
                        onDismissRequest = { showCategoryDropdown = false },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    showCategoryDropdown = false
                                }
                            )
                        }
                    }
                }

                if (amount > remaining) {
                    Text("Amount exceeds the remaining target.", color = MaterialTheme.colorScheme.error)
                } else if (amount > availableBalance) {
                    Text("Insufficient balance.", color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDeposit(amount, category)
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

/*
 * Dialog detail transaksi.
 * Menampilkan informasi ringkas dan menyediakan aksi hapus.
 */
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