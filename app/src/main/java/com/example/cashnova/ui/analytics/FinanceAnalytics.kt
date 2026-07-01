package com.example.cashnova.ui.analytics

import com.example.cashnova.data.FinanceTransaction
import com.example.cashnova.data.TransactionType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class MonthlyFinancePoint(
    val year: Int,
    val month: Int,
    val label: String,
    val income: Double,
    val expense: Double
)

enum class ChartTimeRange {
    ONE_WEEK,
    ONE_MONTH,
    THREE_MONTHS,
    SIX_MONTHS,
    ONE_YEAR,
    ALL
}

/**
 * Mengelompokkan transaksi menjadi data grafik berdasarkan rentang waktu.
 */
fun buildFinanceChartData(
    transactions: List<FinanceTransaction>,
    range: ChartTimeRange
): List<MonthlyFinancePoint> {
    val locale = Locale.forLanguageTag("id-ID")
    val current = Calendar.getInstance(locale)

    return when (range) {
        ChartTimeRange.ONE_WEEK -> buildDailyData(transactions, 7, locale)
        ChartTimeRange.ONE_MONTH -> buildMonthlyData(transactions, 1, locale) // Still monthly but only 1? Maybe weekly for 1 month? Let's stick to monthly count for now or adapt.
        ChartTimeRange.THREE_MONTHS -> buildMonthlyData(transactions, 3, locale)
        ChartTimeRange.SIX_MONTHS -> buildMonthlyData(transactions, 6, locale)
        ChartTimeRange.ONE_YEAR -> buildMonthlyData(transactions, 12, locale)
        ChartTimeRange.ALL -> {
            if (transactions.isEmpty()) return emptyList()
            val firstTransaction = transactions.minBy { it.createdAt }
            val firstCalendar = Calendar.getInstance().apply { timeInMillis = firstTransaction.createdAt }
            val monthsBetween = (current.get(Calendar.YEAR) - firstCalendar.get(Calendar.YEAR)) * 12 +
                    (current.get(Calendar.MONTH) - firstCalendar.get(Calendar.MONTH)) + 1
            buildMonthlyData(transactions, monthsBetween.coerceAtLeast(1), locale)
        }
    }
}

private fun buildDailyData(
    transactions: List<FinanceTransaction>,
    daysCount: Int,
    locale: Locale
): List<MonthlyFinancePoint> {
    val dayFormatter = SimpleDateFormat("EEE", locale)
    val current = Calendar.getInstance(locale).apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    return (daysCount - 1 downTo 0).map { offset ->
        val startOfDay = (current.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -offset) }
        val endOfDay = (startOfDay.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }

        val periodTransactions = transactions.filter {
            it.createdAt >= startOfDay.timeInMillis && it.createdAt < endOfDay.timeInMillis
        }

        MonthlyFinancePoint(
            year = startOfDay.get(Calendar.YEAR),
            month = startOfDay.get(Calendar.MONTH),
            label = dayFormatter.format(startOfDay.time),
            income = periodTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount },
            expense = periodTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        )
    }
}

private fun buildMonthlyData(
    transactions: List<FinanceTransaction>,
    monthCount: Int,
    locale: Locale
): List<MonthlyFinancePoint> {
    val monthFormatter = SimpleDateFormat("MMM", locale)
    val currentMonth = Calendar.getInstance(locale).apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    return (monthCount - 1 downTo 0).map { offset ->
        val startOfMonth = (currentMonth.clone() as Calendar).apply { add(Calendar.MONTH, -offset) }
        val endOfMonth = (startOfMonth.clone() as Calendar).apply { add(Calendar.MONTH, 1) }

        val periodTransactions = transactions.filter {
            it.createdAt >= startOfMonth.timeInMillis && it.createdAt < endOfMonth.timeInMillis
        }

        MonthlyFinancePoint(
            year = startOfMonth.get(Calendar.YEAR),
            month = startOfMonth.get(Calendar.MONTH),
            label = monthFormatter.format(startOfMonth.time).replaceFirstChar { it.uppercase() },
            income = periodTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount },
            expense = periodTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        )
    }
}

// Deprecated or keep for compatibility if needed
fun buildMonthlyFinanceData(
    transactions: List<FinanceTransaction>,
    monthCount: Int = 6
): List<MonthlyFinancePoint> {
    return buildMonthlyData(transactions, monthCount, Locale.forLanguageTag("id-ID"))
}
