package com.example.cashnova.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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

/*
 * Header standar untuk screen yang memiliki tombol kembali.
 * Slot `action` bersifat opsional untuk ikon aksi di sisi kanan.
 */
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
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali"
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
            action?.invoke()
        }
    }
}

/*
 * Judul section reusable.
 * `onSeeAll` opsional untuk kasus daftar dengan aksi "See All".
 */
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
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
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

/*
 * Kartu saldo utama pada dashboard/wallet.
 * Menampilkan total balance dan nama wallet aktif.
 */
@Composable
fun BalanceCard(
    balance: Double,
    walletName: String = "My Wallet",
    onClick: (() -> Unit)? = null
) {
    // Dark Premium Style matching Image 2
    val cardBg = if (isSystemInDarkTheme()) Color(0xFF151516) else Color(0xFF1E1E1F)

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBg,
            contentColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(28.dp))
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Decorative shapes matching image 2
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .offset(x = 70.dp, y = (-50).dp)
                    .clip(CircleShape)
                    .background(Color(0xFF3787D4))
                    .align(Alignment.TopEnd)
            )
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .offset(x = 80.dp, y = 30.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF6DD64))
                    .align(Alignment.TopEnd)
            )
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .offset(x = (-30).dp, y = 50.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF18C93A).copy(alpha = 0.4f))
                    .align(Alignment.BottomStart)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(26.dp)
            ) {
                Text(
                    text = "Total Balance",
                    color = Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = formatMoney(balance, showCents = true),
                    color = Color.White,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = walletName,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

/*
 * Kartu ringkasan pemasukan vs pengeluaran.
 * Dipakai sebagai insight cepat tanpa membuka halaman analytics.
 */
@Composable
fun IncomeOutcomeCard(
    income: Double,
    outcome: Double,
    modifier: Modifier = Modifier
) {
    val cardBg = if (isSystemInDarkTheme()) Color(0xFF151516) else Color(0xFF1E1E1F)

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBg,
            contentColor = Color.White
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp))) {
            // Decorative shapes matching image 2
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .offset(x = (-25).dp, y = (-25).dp)
                    .clip(CircleShape)
                    .background(Color(0xFFA684EB).copy(alpha = 0.45f))
                    .align(Alignment.TopStart)
            )
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .offset(x = 20.dp, y = 20.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1D1C5).copy(alpha = 0.45f))
                    .align(Alignment.BottomEnd)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Income
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = Color(0xFF18C93A),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Income",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = formatMoney(income),
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                // Vertical Divider
                Box(
                    modifier = Modifier
                        .height(42.dp)
                        .width(1.dp)
                        .background(Color.White.copy(alpha = 0.25f))
                )

                // Outcome
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = Color(0xFFEB5745),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Outcome",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = formatMoney(outcome),
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

/*
 * Kartu target tabungan versi compact untuk list/grid ringkas.
 */
@Composable
fun SavingCompactCard(
    goal: SavingGoal,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
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
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "${(goal.progress * 100).toInt()}%",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = goal.title,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = formatMoney(goal.targetAmount),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { goal.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(99.dp)),
                color = savingColor(goal.colorKey),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

/*
 * Kartu target tabungan versi besar.
 * Seluruh card dapat ditekan untuk aksi setor, ikon delete untuk hapus target.
 */
@Composable
fun SavingLargeCard(
    goal: SavingGoal,
    onDeposit: () -> Unit,
    onDelete: () -> Unit
) {
    val background = savingColor(goal.colorKey)
    val isLightColor = goal.colorKey == 3 // Yellow is light

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
                        color = if (isLightColor) Color.Black else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Balance",
                        color = if (isLightColor) {
                            Color.Black.copy(alpha = 0.65f)
                        } else {
                            Color.White.copy(alpha = 0.75f)
                        },
                        fontSize = 11.sp
                    )
                    Text(
                        text = "${formatMoney(goal.currentAmount)} of ${formatMoney(goal.targetAmount)}",
                        color = if (isLightColor) Color.Black else Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus target",
                        tint = if (isLightColor) {
                            Color.Black.copy(alpha = 0.70f)
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
                color = if (isLightColor) Color.Black else Color.White,
                trackColor = if (isLightColor) {
                    Color.Black.copy(alpha = 0.20f)
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
                    color = if (isLightColor) Color.Black else Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${goal.daysLeft} days left",
                    color = if (isLightColor) {
                        Color.Black.copy(alpha = 0.70f)
                    } else {
                        Color.White.copy(alpha = 0.75f)
                    },
                    fontSize = 11.sp
                )
            }
        }
    }
}

/*
 * Baris transaksi reusable.
 * Menampilkan ikon kategori, judul/subtitle, dan nominal dengan tanda +/−.
 */
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
            .background(MaterialTheme.colorScheme.surface)
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
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = transaction.subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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

/*
 * Mapping color key target tabungan ke warna tema.
 * Modulo menjaga hasil tetap stabil walau key di luar rentang.
 */
fun savingColor(key: Int): Color {
    return when (((key % 4) + 4) % 4) {
        0 -> CashNovaRed
        1 -> CashNovaPink
        2 -> CashNovaBlue
        else -> CashNovaYellow
    }
}

/*
 * Menentukan ikon target tabungan dari judul (heuristic berbasis keyword).
 */
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

/*
 * Menentukan ikon transaksi berdasarkan title + category.
 * Pendekatan keyword ini menjaga UI tetap informatif tanpa field ikon eksplisit di DB.
 */
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

/*
 * Menentukan warna ikon transaksi berdasarkan type/kategori utama.
 */
private fun transactionIconColor(transaction: FinanceTransaction): Color {
    return when {
        transaction.type == TransactionType.INCOME -> CashNovaGreen
        transaction.category.equals("Saving", ignoreCase = true) -> CashNovaBlue
        transaction.category.equals("Subscription", ignoreCase = true) -> CashNovaRed
        transaction.category.equals("Shopping", ignoreCase = true) -> CashNovaPink
        else -> CashNovaBlue
    }
}
