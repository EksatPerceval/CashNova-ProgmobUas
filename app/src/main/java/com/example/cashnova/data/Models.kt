package com.example.cashnova.data

enum class TransactionType {
    INCOME,
    EXPENSE
}

data class FinanceTransaction(
    val id: Long,
    val title: String,
    val subtitle: String,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val createdAt: Long
)

data class SavingGoal(
    val id: Long,
    val title: String,
    val currentAmount: Double,
    val targetAmount: Double,
    val daysLeft: Int,
    val colorKey: Int
) {
    val progress: Float
        get() = if (targetAmount <= 0.0) {
            0f
        } else {
            (currentAmount / targetAmount).coerceIn(0.0, 1.0).toFloat()
        }
}

data class EarningSource(
    val name: String,
    val amount: Double,
    val letter: String,
    val colorKey: Int
)

data class CashNovaUiState(
    val onboardingCompleted: Boolean = false,
    val profileName: String = "Asep Resing",
    val openingBalance: Double = 22_000.40,
    val transactions: List<FinanceTransaction> = emptyList(),
    val savings: List<SavingGoal> = emptyList()
) {
    val totalIncome: Double
        get() = transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }

    val totalExpense: Double
        get() = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

    val totalBalance: Double
        get() = openingBalance + totalIncome - totalExpense

    val earningSources: List<EarningSource>
        get() {
            val preferred = listOf(
                Triple("Upwork", "U", 0),
                Triple("Freepik", "F", 1),
                Triple("Envato", "E", 2)
            )

            return preferred.map { (source, letter, colorKey) ->
                EarningSource(
                    name = source,
                    amount = transactions
                        .filter {
                            it.type == TransactionType.INCOME &&
                                it.title.equals(source, ignoreCase = true)
                        }
                        .sumOf { it.amount },
                    letter = letter,
                    colorKey = colorKey
                )
            }
        }
}
