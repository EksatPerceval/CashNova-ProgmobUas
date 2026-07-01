package com.example.cashnova.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cashnova.data.FinanceTransaction
import com.example.cashnova.data.SavingGoal
import com.example.cashnova.data.TransactionType
import com.example.cashnova.ui.theme.CashNovaBlue
import com.example.cashnova.ui.theme.CashNovaDark
import com.example.cashnova.ui.theme.CashNovaGreen
import com.example.cashnova.ui.theme.CashNovaMuted
import com.example.cashnova.ui.theme.CashNovaPink
import com.example.cashnova.ui.theme.CashNovaRed
import com.example.cashnova.ui.theme.CashNovaYellow
import com.example.cashnova.ui.util.formatMoney

@Composable
fun ScreenHeader(
    title: String,
    onBack: () -> Unit,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Kembali"
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
            action?.invoke()
        }
    }
}

@Composable
fun SectionTitle(
    title: String,
    onSeeAll: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
        )

        if (onSeeAll != null) {
            Text(
                text = "See All",
                color = CashNovaBlue,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onSeeAll)
            )
        }
    }
}

@Composable
fun BalanceCard(
    balance: Double,
    onClick: (() -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CashNovaDark),
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 26.dp, vertical = 28.dp)
        ) {
            Text(
                text = "Total Balance",
                color = Color.White.copy(alpha = 0.55f),
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = formatMoney(balance, showCents = true),
                color = Color.White,
                style = MaterialTheme.typography.displaySmall
            )
        }
    }
}

@Composable
fun IncomeExpenseCard(
    title: String,
    amount: Double,
    isIncome: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CashNovaDark),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isIncome) {
                    Icons.Default.ArrowDownward
                } else {
                    Icons.Default.ArrowUpward
                },
                contentDescription = null,
                tint = if (isIncome) CashNovaGreen else CashNovaRed,
                modifier = Modifier.size(23.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 12.sp
                )
                Text(
                    text = formatMoney(amount),
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SavingCompactCard(
    goal: SavingGoal,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier.then(
            if (onClick != null) Modifier.clickable(onClick = onClick)
            else Modifier
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = goalIcon(goal.title),
                    contentDescription = null,
                    tint = CashNovaMuted,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "${(goal.progress * 100).toInt()}%",
                    color = CashNovaMuted,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = goal.title,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Text(
                text = formatMoney(goal.targetAmount),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { goal.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(99.dp)),
                color = savingColor(goal.colorKey),
                trackColor = Color(0xFFF0F1F4)
            )
        }
    }
}

@Composable
fun SavingLargeCard(
    goal: SavingGoal,
    onDeposit: () -> Unit,
    onDelete: () -> Unit
) {
    val background = savingColor(goal.colorKey)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onDeposit)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = goal.title,
                        color = if (goal.colorKey == 3) CashNovaDark else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Balance",
                        color = if (goal.colorKey == 3) {
                            CashNovaDark.copy(alpha = 0.65f)
                        } else {
                            Color.White.copy(alpha = 0.75f)
                        },
                        fontSize = 11.sp
                    )
                    Text(
                        text = "${formatMoney(goal.currentAmount)} of ${formatMoney(goal.targetAmount)}",
                        color = if (goal.colorKey == 3) CashNovaDark else Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus target",
                        tint = if (goal.colorKey == 3) {
                            CashNovaDark.copy(alpha = 0.70f)
                        } else {
                            Color.White.copy(alpha = 0.85f)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { goal.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(99.dp)),
                color = if (goal.colorKey == 3) CashNovaDark else Color.White,
                trackColor = if (goal.colorKey == 3) {
                    CashNovaDark.copy(alpha = 0.20f)
                } else {
                    Color.White.copy(alpha = 0.30f)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "See detail",
                    color = if (goal.colorKey == 3) CashNovaDark else Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${goal.daysLeft} days left",
                    color = if (goal.colorKey == 3) {
                        CashNovaDark.copy(alpha = 0.70f)
                    } else {
                        Color.White.copy(alpha = 0.75f)
                    },
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun TransactionRow(
    transaction: FinanceTransaction,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val iconColor = transactionIconColor(transaction)
    val amountColor = if (transaction.type == TransactionType.INCOME) {
        CashNovaGreen
    } else {
        CashNovaRed
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            )
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(iconColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = transactionIcon(transaction),
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(23.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.title,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = transaction.subtitle,
                color = CashNovaMuted,
                fontSize = 12.sp,
                maxLines = 1
            )
        }

        Text(
            text = buildString {
                append(if (transaction.type == TransactionType.INCOME) "+" else "-")
                append(formatMoney(transaction.amount))
            },
            color = amountColor,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

fun savingColor(key: Int): Color {
    return when (((key % 4) + 4) % 4) {
        0 -> CashNovaRed
        1 -> CashNovaPink
        2 -> CashNovaBlue
        else -> CashNovaYellow
    }
}

fun goalIcon(title: String): ImageVector {
    return when {
        title.contains("phone", ignoreCase = true) ||
            title.contains("iphone", ignoreCase = true) -> Icons.Default.PhoneIphone

        title.contains("mac", ignoreCase = true) ||
            title.contains("laptop", ignoreCase = true) -> Icons.Default.LaptopMac

        title.contains("house", ignoreCase = true) ||
            title.contains("home", ignoreCase = true) -> Icons.Default.Home

        else -> Icons.Default.Savings
    }
}

private fun transactionIcon(transaction: FinanceTransaction): ImageVector {
    val source = "${transaction.title} ${transaction.category}"

    return when {
        source.contains("adobe", ignoreCase = true) -> Icons.Default.Brush
        source.contains("subscription", ignoreCase = true) -> Icons.Default.Subscriptions
        source.contains("house", ignoreCase = true) -> Icons.Default.Home
        source.contains("shopping", ignoreCase = true) -> Icons.Default.ShoppingBag
        source.contains("data", ignoreCase = true) ||
            source.contains("internet", ignoreCase = true) -> Icons.Default.Wifi

        source.contains("saving", ignoreCase = true) -> Icons.Default.Savings
        source.contains("payment", ignoreCase = true) ||
            source.contains("paypal", ignoreCase = true) -> Icons.Default.Payments

        else -> Icons.Default.CreditCard
    }
}

private fun transactionIconColor(transaction: FinanceTransaction): Color {
    return when {
        transaction.type == TransactionType.INCOME -> CashNovaGreen
        transaction.category.equals("Saving", ignoreCase = true) -> CashNovaBlue
        transaction.category.equals("Subscription", ignoreCase = true) -> CashNovaRed
        transaction.category.equals("Shopping", ignoreCase = true) -> CashNovaPink
        else -> CashNovaBlue
    }
}
