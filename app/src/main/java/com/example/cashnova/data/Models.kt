package com.example.cashnova.data

enum class TransactionType {
    INCOME,
    EXPENSE
}

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

data class Wallet(
    val id: Long,
    val name: String,
    val balance: Double,
    val colorKey: Int = 0
)

data class FinanceTransaction(
    val id: Long,
    val title: String,
    val subtitle: String,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val createdAt: Long,
    val walletId: Long = 0L
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
    val savings: List<SavingGoal> = emptyList(),
    val wallets: List<Wallet> = listOf(Wallet(0L, "Main Wallet", 0.0)),
    val selectedWalletId: Long = 0L,
    val customCategories: List<String> = emptyList(),
    val themeMode: ThemeMode = ThemeMode.SYSTEM
) {
    val defaultCategories = listOf("Salary", "Freelance", "Food", "Transport", "Shopping", "Utility", "Subscription", "Payment", "Saving", "Other")
    
    val allCategories: List<String>
        get() = (defaultCategories + customCategories).distinct()

    val currentWallet: Wallet?
        get() = wallets.find { it.id == selectedWalletId }

    val filteredTransactions: List<FinanceTransaction>
        get() = transactions.filter { it.walletId == selectedWalletId }

    val totalIncome: Double
        get() = filteredTransactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }

    val totalExpense: Double
        get() = filteredTransactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

    val totalBalance: Double
        get() {
            val wallet = currentWallet ?: return 0.0
            return getWalletBalance(wallet)
        }
        
    fun getWalletBalance(wallet: Wallet): Double {
        val walletTransactions = transactions.filter { it.walletId == wallet.id }
        val income = walletTransactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }
        val expense = walletTransactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }
        return wallet.balance + income - expense
    }

    val earningSources: List<EarningSource>
        get() {
            // Ambil semua transaksi pemasukan untuk wallet yang sedang aktif
            val incomeTransactions = filteredTransactions.filter { it.type == TransactionType.INCOME }
            
            // Kelompokkan berdasarkan Kategori agar pengguna bisa melihat rincian sumber pemasukan
            val groupedByCategory = incomeTransactions
                .groupBy { it.category.trim().ifBlank { "Other" } }
                .mapValues { entry -> entry.value.sumOf { it.amount } }
                .toList()
                .sortedByDescending { it.second } // Urutkan dari nominal terbesar

            if (groupedByCategory.isEmpty()) {
                return listOf(
                    EarningSource("No Income", 0.0, "?", 0)
                )
            }

            return groupedByCategory.take(6).mapIndexed { index, (category, amount) ->
                EarningSource(
                    name = category,
                    amount = amount,
                    letter = category.firstOrNull()?.toString()?.uppercase() ?: "I",
                    colorKey = index % 3
                )
            }
        }
}
