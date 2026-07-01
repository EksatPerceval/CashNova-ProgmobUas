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

/**
 * Mengelompokkan transaksi menjadi data bulanan.
 *
 * Secara default mengambil enam bulan terakhir,
 * termasuk bulan yang sedang berjalan.
 */
fun buildMonthlyFinanceData(
    transactions: List<FinanceTransaction>,
    monthCount: Int = 6
): List<MonthlyFinancePoint> {

    val safeMonthCount = monthCount.coerceAtLeast(1)
    val locale = Locale.forLanguageTag("id-ID")

    val currentMonth = Calendar.getInstance(locale).apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val monthFormatter = SimpleDateFormat(
        "MMM",
        locale
    )

    return (safeMonthCount - 1 downTo 0).map { offset ->

        val startOfMonth =
            (currentMonth.clone() as Calendar).apply {
                add(Calendar.MONTH, -offset)
            }

        val endOfMonth =
            (startOfMonth.clone() as Calendar).apply {
                add(Calendar.MONTH, 1)
            }

        var totalIncome = 0.0
        var totalExpense = 0.0

        transactions.forEach { transaction ->

            val isInsideMonth =
                transaction.createdAt >= startOfMonth.timeInMillis &&
                        transaction.createdAt < endOfMonth.timeInMillis

            if (isInsideMonth) {
                when (transaction.type) {
                    TransactionType.INCOME -> {
                        totalIncome += transaction.amount
                    }

                    TransactionType.EXPENSE -> {
                        totalExpense += transaction.amount
                    }
                }
            }
        }

        val monthLabel = monthFormatter
            .format(startOfMonth.time)
            .replaceFirstChar { character ->
                character.uppercaseChar().toString()
            }

        MonthlyFinancePoint(
            year = startOfMonth.get(Calendar.YEAR),
            month = startOfMonth.get(Calendar.MONTH),
            label = monthLabel,
            income = totalIncome,
            expense = totalExpense
        )
    }
}