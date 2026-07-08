package com.example.cashnova.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cashnova.data.CashNovaRepository
import com.example.cashnova.data.CashNovaUiState
import com.example.cashnova.data.FinanceTransaction
import com.example.cashnova.data.SavingGoal
import com.example.cashnova.data.ThemeMode
import com.example.cashnova.data.TransactionType
import com.example.cashnova.data.User
import com.example.cashnova.data.Wallet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CashNovaViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        CashNovaRepository(application)

    /*
     * Data awal berisi profil, onboarding,
     * saldo pembuka, dan tabungan.
     */
    private val _uiState =
        MutableStateFlow(
            repository.loadPreferencesState()
        )

    val uiState: StateFlow<CashNovaUiState> =
        _uiState.asStateFlow()

    init {
        observeRoomTransactions()
    }

    /*
     * Memulai Room dan mengamati transaksi.
     */
    private fun observeRoomTransactions() {

        viewModelScope.launch {

            /*
             * Memigrasikan transaksi lama atau
             * memasukkan data demo pada penggunaan pertama.
             */
            repository.initializeTransactions()

            /*
             * Mengamati perubahan tabel transaksi.
             */
            repository
                .observeTransactions()
                .collectLatest { transactions ->

                    _uiState.update { currentState ->
                        currentState.copy(
                            transactions = transactions
                        )
                    }
                }
        }
    }

    fun updateProfileName(name: String) {
        val cleanedName = name.trim()
        if (cleanedName.isBlank()) return

        updatePreferences { currentState ->
            currentState.copy(
                profileName = cleanedName
            )
        }
    }

    fun completeOnboarding() {
        updatePreferences { it.copy(onboardingCompleted = true) }
    }

    fun updateThemeMode(themeMode: ThemeMode) {
        updatePreferences { it.copy(themeMode = themeMode) }
    }

    fun login(username: String, pin: String, rememberMe: Boolean, onSuccess: () -> Unit, onError: (String) -> Unit) {
        // In a real app, this would verify against a database. For now we simulate success.
        if (username.isNotBlank() && pin.isNotBlank()) {
            val user = User(username = username, pin = pin)
            
            viewModelScope.launch {
                val previousUsername = repository.getLastUsername()
                
                if (previousUsername != null && previousUsername != username) {
                    val clearedState = repository.clearDataForNewUser()
                    _uiState.value = clearedState.copy(currentUser = user, rememberMe = rememberMe)
                    repository.savePreferences(_uiState.value)
                } else {
                    updatePreferences { it.copy(currentUser = user, rememberMe = rememberMe) }
                }
                
                repository.saveLastUsername(username)
                onSuccess()
            }
        } else {
            onError("Username and PIN cannot be empty.")
        }
    }

    fun register(username: String, pin: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (username.isNotBlank() && pin.isNotBlank()) {
            // Save newly registered user to Preferences for now
            val user = User(username = username, pin = pin)
            // Just register, don't login immediately
            onSuccess()
        } else {
            onError("Username and PIN cannot be empty.")
        }
    }

    fun logout() {
        updatePreferences { it.copy(currentUser = null, rememberMe = false) }
    }

    /*
     * Menambah transaksi baru ke Room.
     */
    fun addTransaction(
        title: String,
        subtitle: String,
        amount: Double,
        type: TransactionType,
        category: String
    ) {

        val cleanedTitle =
            title.trim()

        if (
            cleanedTitle.isBlank() ||
            amount <= 0.0
        ) {
            return
        }

        val transaction =
            FinanceTransaction(
                /*
                 * ID 0 membuat Room menghasilkan
                 * primary key secara otomatis.
                 */
                id = 0L,

                title = cleanedTitle,

                subtitle =
                    subtitle
                        .trim()
                        .ifBlank {
                            if (
                                type ==
                                TransactionType.INCOME
                            ) {
                                "Income"
                            } else {
                                "Expense"
                            }
                        },

                amount = amount,

                type = type,

                category =
                    category
                        .trim()
                        .ifBlank {
                            "Other"
                        },

                createdAt =
                    System.currentTimeMillis(),

                walletId = _uiState.value.selectedWalletId
            )

        viewModelScope.launch {
            repository.insertTransaction(
                transaction
            )
        }
    }

    /*
     * Memperbarui transaksi Room.
     */
    fun updateTransaction(
        transaction: FinanceTransaction
    ) {

        if (
            transaction.id <= 0L ||
            transaction.title.isBlank() ||
            transaction.amount <= 0.0
        ) {
            return
        }

        viewModelScope.launch {
            repository.updateTransaction(
                transaction.copy(
                    title =
                        transaction.title.trim(),

                    subtitle =
                        transaction.subtitle.trim(),

                    category =
                        transaction.category
                            .trim()
                            .ifBlank {
                                "Other"
                            }
                )
            )
        }
    }

    /*
     * Menghapus transaksi dari Room.
     */
    fun deleteTransaction(
        id: Long
    ) {

        if (id <= 0L) {
            return
        }

        viewModelScope.launch {
            repository.deleteTransaction(id)
        }
    }

    fun addSavingGoal(
        title: String,
        targetAmount: Double,
        initialAmount: Double,
        daysLeft: Int
    ) {

        if (
            title.isBlank() ||
            targetAmount <= 0.0
        ) {
            return
        }

        val safeInitialAmount =
            initialAmount.coerceIn(
                minimumValue = 0.0,
                maximumValue = targetAmount
            )

        val newGoal =
            SavingGoal(
                id =
                    System.currentTimeMillis(),

                title =
                    title.trim(),

                currentAmount =
                    safeInitialAmount,

                targetAmount =
                    targetAmount,

                daysLeft =
                    daysLeft.coerceAtLeast(1),

                colorKey =
                    _uiState
                        .value
                        .savings
                        .size % 4
            )

        updatePreferences { currentState ->
            currentState.copy(
                savings =
                    listOf(newGoal) +
                            currentState.savings
            )
        }
    }

    fun depositToSaving(
        goalId: Long,
        amount: Double,
        category: String
    ) {

        if (amount <= 0.0) {
            return
        }

        val currentState =
            _uiState.value

        val goal =
            currentState
                .savings
                .firstOrNull {
                    it.id == goalId
                }
                ?: return

        val remainingAmount =
            (
                    goal.targetAmount -
                            goal.currentAmount
                    ).coerceAtLeast(0.0)

        val safeAmount =
            amount.coerceAtMost(
                remainingAmount
            )

        if (
            safeAmount <= 0.0 ||
            safeAmount >
            currentState.totalBalance
        ) {
            return
        }

        /*
         * Memperbarui target tabungan.
         */
        updatePreferences { state ->
            state.copy(
                savings =
                    state.savings.map {
                            savingGoal ->

                        if (
                            savingGoal.id ==
                            goalId
                        ) {
                            savingGoal.copy(
                                currentAmount =
                                    savingGoal
                                        .currentAmount +
                                            safeAmount
                            )
                        } else {
                            savingGoal
                        }
                    }
            )
        }

        /*
         * Deposit tabungan juga dicatat
         * sebagai transaksi pengeluaran.
         */
        val savingTransaction =
            FinanceTransaction(
                id = 0L,
                title = goal.title,
                subtitle = "Saving deposit",
                amount = safeAmount,
                type = TransactionType.EXPENSE,
                category = category.ifBlank { "Saving" },
                createdAt =
                    System.currentTimeMillis(),
                walletId = currentState.selectedWalletId
            )

        viewModelScope.launch {
            repository.insertTransaction(
                savingTransaction
            )
        }
    }

    fun deleteSavingGoal(
        id: Long
    ) {

        updatePreferences { currentState ->
            currentState.copy(
                savings =
                    currentState
                        .savings
                        .filterNot {
                            it.id == id
                        }
            )
        }
    }

    fun selectWallet(walletId: Long) {
        updatePreferences { it.copy(selectedWalletId = walletId) }
    }

    fun addWallet(name: String, initialBalance: Double) {
        if (name.isBlank()) return

        val newWallet = Wallet(
            id = System.currentTimeMillis(),
            name = name.trim(),
            balance = initialBalance,
            colorKey = _uiState.value.wallets.size % 4
        )

        updatePreferences { it.copy(wallets = it.wallets + newWallet) }
    }

    fun deleteWallet(walletId: Long) {
        val currentState = _uiState.value
        if (currentState.wallets.size <= 1) return // Prevent deleting the last wallet

        viewModelScope.launch {
            // Delete all transactions associated with this wallet
            repository.deleteTransactionsByWalletId(walletId)

            updatePreferences { state ->
                val newWallets = state.wallets.filterNot { it.id == walletId }
                val newSelectedWalletId = if (state.selectedWalletId == walletId) {
                    newWallets.first().id
                } else {
                    state.selectedWalletId
                }

                state.copy(
                    wallets = newWallets,
                    selectedWalletId = newSelectedWalletId
                )
            }
        }
    }

    fun addCustomCategory(category: String) {
        val cleaned = category.trim()
        if (cleaned.isBlank()) return

        updatePreferences { currentState ->
            if (currentState.allCategories.any { it.equals(cleaned, ignoreCase = true) }) {
                currentState
            } else {
                currentState.copy(
                    customCategories = currentState.customCategories + cleaned
                )
            }
        }
    }

    /*
     * Mengembalikan data demo.
     */
    fun resetDemoData() {

        viewModelScope.launch {

            val resetState =
                repository.resetAllData()

            /*
             * Flow Room akan mengisi kembali
             * daftar transaksi sesudah reset.
             */
            _uiState.value =
                resetState.copy(
                    transactions =
                        _uiState
                            .value
                            .transactions
                )
        }
    }

    /*
     * Helper untuk memperbarui state
     * yang masih memakai SharedPreferences.
     */
    private fun updatePreferences(
        transform:
            (
            CashNovaUiState
        ) -> CashNovaUiState
    ) {

        val updatedState =
            transform(_uiState.value)

        _uiState.value =
            updatedState

        repository.savePreferences(
            updatedState
        )
    }
}