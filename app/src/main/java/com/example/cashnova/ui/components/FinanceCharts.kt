package com.example.cashnova.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cashnova.ui.analytics.MonthlyFinancePoint
import com.example.cashnova.ui.theme.CashNovaGreen
import com.example.cashnova.ui.theme.CashNovaLine
import com.example.cashnova.ui.theme.CashNovaMuted
import com.example.cashnova.ui.theme.CashNovaRed
import com.example.cashnova.ui.util.formatMoney
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs

private val ChartAreaHeight = 180.dp

/**
 * Grafik batang pemasukan dan pengeluaran per bulan.
 */
@Composable
fun MonthlyIncomeExpenseChart(
    data: List<MonthlyFinancePoint>,
    modifier: Modifier = Modifier
) {
    val maximumValue = data
        .maxOfOrNull { point ->
            maxOf(
                point.income,
                point.expense
            )
        }
        ?.coerceAtLeast(1.0)
        ?: 1.0

    val totalIncome = data.sumOf { it.income }
    val totalExpense = data.sumOf { it.expense }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Grafik 6 Bulan",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Perbandingan pemasukan dan pengeluaran",
                color = CashNovaMuted,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                ChartLegend(
                    title = "Pemasukan",
                    color = CashNovaGreen
                )

                ChartLegend(
                    title = "Pengeluaran",
                    color = CashNovaRed
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                ChartYAxis(
                    maximumValue = maximumValue
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(ChartAreaHeight)
                ) {
                    ChartGrid()

                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        data.forEach { point ->

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                verticalArrangement = Arrangement.Bottom,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement =
                                        Arrangement.spacedBy(4.dp)
                                ) {
                                    FinanceBar(
                                        value = point.income,
                                        maximumValue = maximumValue,
                                        maximumHeight = ChartAreaHeight,
                                        color = CashNovaGreen
                                    )

                                    FinanceBar(
                                        value = point.expense,
                                        maximumValue = maximumValue,
                                        maximumHeight = ChartAreaHeight,
                                        color = CashNovaRed
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.width(70.dp))

                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    data.forEach { point ->
                        Text(
                            text = point.label,
                            color = CashNovaMuted,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider(
                color = CashNovaLine
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FinanceTotalText(
                    title = "Total pemasukan",
                    amount = totalIncome,
                    color = CashNovaGreen
                )

                FinanceTotalText(
                    title = "Total pengeluaran",
                    amount = totalExpense,
                    color = CashNovaRed,
                    alignment = Alignment.End
                )
            }
        }
    }
}

/**
 * Grafik donat pemasukan versus pengeluaran.
 */
@Composable
fun IncomeExpenseDonutChart(
    income: Double,
    expense: Double,
    modifier: Modifier = Modifier
) {
    val safeIncome = income.coerceAtLeast(0.0)
    val safeExpense = expense.coerceAtLeast(0.0)
    val total = safeIncome + safeExpense

    val incomeSweep = if (total > 0.0) {
        ((safeIncome / total) * 360.0).toFloat()
    } else {
        0f
    }

    val expenseSweep = if (total > 0.0) {
        ((safeExpense / total) * 360.0).toFloat()
    } else {
        0f
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Perbandingan Bulan Ini",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Komposisi pemasukan dan pengeluaran bulan berjalan",
                color = CashNovaMuted,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(210.dp)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val strokeWidth = 30.dp.toPx()

                    if (total <= 0.0) {
                        drawArc(
                            color = CashNovaLine,
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(
                                width = strokeWidth,
                                cap = StrokeCap.Butt
                            )
                        )
                    } else {
                        drawArc(
                            color = CashNovaGreen,
                            startAngle = -90f,
                            sweepAngle = incomeSweep,
                            useCenter = false,
                            style = Stroke(
                                width = strokeWidth,
                                cap = StrokeCap.Butt
                            )
                        )

                        drawArc(
                            color = CashNovaRed,
                            startAngle = -90f + incomeSweep,
                            sweepAngle = expenseSweep,
                            useCenter = false,
                            style = Stroke(
                                width = strokeWidth,
                                cap = StrokeCap.Butt
                            )
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Total arus kas",
                        color = CashNovaMuted,
                        fontSize = 12.sp
                    )

                    Text(
                        text = formatCompactRupiah(total),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            FinanceAmountRow(
                title = "Pemasukan",
                amount = safeIncome,
                color = CashNovaGreen
            )

            Spacer(modifier = Modifier.height(12.dp))

            FinanceAmountRow(
                title = "Pengeluaran",
                amount = safeExpense,
                color = CashNovaRed
            )
        }
    }
}

@Composable
private fun ChartGrid() {
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val lineCount = 5

        repeat(lineCount) { index ->
            val y =
                size.height *
                        index.toFloat() /
                        (lineCount - 1).toFloat()

            drawLine(
                color = CashNovaLine,
                start = Offset(
                    x = 0f,
                    y = y
                ),
                end = Offset(
                    x = size.width,
                    y = y
                ),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}

@Composable
private fun ChartYAxis(
    maximumValue: Double
) {
    Column(
        modifier = Modifier
            .width(62.dp)
            .height(ChartAreaHeight),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = formatCompactRupiah(maximumValue),
            color = CashNovaMuted,
            fontSize = 10.sp
        )

        Text(
            text = formatCompactRupiah(maximumValue / 2.0),
            color = CashNovaMuted,
            fontSize = 10.sp
        )

        Text(
            text = "Rp0",
            color = CashNovaMuted,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun FinanceBar(
    value: Double,
    maximumValue: Double,
    maximumHeight: Dp,
    color: Color
) {
    val ratio = if (maximumValue <= 0.0) {
        0f
    } else {
        (value / maximumValue)
            .coerceIn(0.0, 1.0)
            .toFloat()
    }

    val calculatedHeight = when {
        value <= 0.0 -> 0.dp

        else -> {
            (
                    maximumHeight.value *
                            ratio
                    )
                .dp
                .coerceAtLeast(4.dp)
        }
    }

    val animatedHeight by animateDpAsState(
        targetValue = calculatedHeight,
        label = "financeBarHeight"
    )

    Box(
        modifier = Modifier
            .width(11.dp)
            .height(animatedHeight)
            .clip(
                RoundedCornerShape(
                    topStart = 5.dp,
                    topEnd = 5.dp
                )
            )
            .background(color)
    )
}

@Composable
private fun ChartLegend(
    title: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )

        Spacer(modifier = Modifier.width(7.dp))

        Text(
            text = title,
            color = CashNovaMuted,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun FinanceTotalText(
    title: String,
    amount: Double,
    color: Color,
    alignment: Alignment.Horizontal = Alignment.Start
) {
    Column(
        horizontalAlignment = alignment
    ) {
        Text(
            text = title,
            color = CashNovaMuted,
            fontSize = 11.sp
        )

        Text(
            text = formatCompactRupiah(amount),
            color = color,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun FinanceAmountRow(
    title: String,
    amount: Double,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Medium
        )

        Text(
            text = formatMoney(amount),
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun formatCompactRupiah(
    amount: Double
): String {
    val absoluteAmount = abs(amount)

    val symbols = DecimalFormatSymbols(
        Locale.forLanguageTag("id-ID")
    )

    val formatter = DecimalFormat(
        "0.#",
        symbols
    )

    return when {
        absoluteAmount >= 1_000_000_000.0 -> {
            "Rp${formatter.format(absoluteAmount / 1_000_000_000.0)} M"
        }

        absoluteAmount >= 1_000_000.0 -> {
            "Rp${formatter.format(absoluteAmount / 1_000_000.0)} jt"
        }

        absoluteAmount >= 1_000.0 -> {
            "Rp${formatter.format(absoluteAmount / 1_000.0)} rb"
        }

        else -> {
            "Rp${formatter.format(absoluteAmount)}"
        }
    }
}