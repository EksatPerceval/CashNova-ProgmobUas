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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/*
 * ViewModel utama aplikasi CashNova.
 * Mengelola seluruh state UI dan logika bisnis,
 * serta berkomunikasi dengan CashNovaRepository untuk operasi data.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CashNovaViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = CashNovaRepository(application)

    /*
     * State utama aplikasi.
     * Diinisialisasi dari SharedPreferences untuk menentukan state awal (login/onboarding).
     */
    private val _uiState = MutableStateFlow(repository.loadInitialState())
    val uiState: StateFlow<CashNovaUiState> = _uiState.asStateFlow()

    init {
        observeUserData()
    }

    /*
     * Mengamati semua data reaktif dari Room berdasarkan pengguna yang sedang login.
     * Saat currentUser berubah (login/logout), semua Flow data secara otomatis diperbarui.
     */
    private fun observeUserData() {
        viewModelScope.launch {
            _uiState.flatMapLatest { state ->
                val username = state.currentUser?.username ?: return@flatMapLatest flowOf(emptyList())
                repository.observeTransactionsByUser(username)
            }.collect { transactions ->
                _uiState.update { it.copy(transactions = transactions) }
            }
        }

        viewModelScope.launch {
            _uiState.flatMapLatest { state ->
                val username = state.currentUser?.username ?: return@flatMapLatest flowOf(emptyList())
                repository.observeWalletsByUser(username)
            }.collect { wallets ->
                _uiState.update { current ->
                    val validSelectedId = if (wallets.any { it.id == current.selectedWalletId }) {
                        current.selectedWalletId
                    } else {
                        wallets.firstOrNull()?.id ?: 0L
                    }
                    current.copy(wallets = wallets, selectedWalletId = validSelectedId)
                }
            }
        }

        viewModelScope.launch {
            _uiState.flatMapLatest { state ->
                val username = state.currentUser?.username ?: return@flatMapLatest flowOf(emptyList())
                repository.observeSavingsByUser(username)
            }.collect { savings ->
                _uiState.update { it.copy(savings = savings) }
            }
        }

        viewModelScope.launch {
            _uiState.flatMapLatest { state ->
                val username = state.currentUser?.username ?: return@flatMapLatest flowOf(emptyList<String>())
                repository.observeCategoriesByUser(username)
            }.collect { categories ->
                _uiState.update { it.copy(customCategories = categories) }
            }
        }
    }

    /* ============================================================
     * Auth: Login, Register, Logout
     * ============================================================ */

    /*
     * Menangani proses login pengguna.
     * Memvalidasi kredensial ke database Room.
     * Jika berhasil, memuat semua data pengguna tersebut secara reaktif.
     */
    fun login(
        username: String,
        pin: String,
        rememberMe: Boolean,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (username.isBlank() || pin.isBlank()) {
            onError("Username dan PIN tidak boleh kosong.")
            return
        }

        viewModelScope.launch {
            val userEntity = repository.loginUser(username, pin)
            if (userEntity != null) {
                val user = User(username = userEntity.username, pin = userEntity.pin)
                _uiState.update {
                    it.copy(
                        currentUser = user,
                        rememberMe = rememberMe,
                        profileName = userEntity.profileName,
                        onboardingCompleted = true
                    )
                }
                repository.saveSession(userEntity.username, userEntity.profileName, rememberMe, _uiState.value.themeMode)
                onSuccess()
            } else {
                onError("Username atau PIN salah.")
            }
        }
    }

    /*
     * Mendaftarkan akun baru ke dalam database Room.
     * Jika username sudah terdaftar, menampilkan pesan error.
     */
    fun register(
        username: String,
        pin: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (username.isBlank() || pin.isBlank()) {
            onError("Username dan PIN tidak boleh kosong.")
            return
        }

        viewModelScope.launch {
            val success = repository.registerUser(
                username = username,
                pin = pin,
                profileName = username
            )
            if (success) {
                onSuccess()
            } else {
                onError("Username '$username' sudah terdaftar. Silakan gunakan username lain.")
            }
        }
    }

    /*
     * Melakukan logout: menghapus sesi dari SharedPreferences
     * dan mengosongkan state UI.
     */
    fun logout() {
        repository.clearSession()
        _uiState.update {
            CashNovaUiState(onboardingCompleted = true, themeMode = it.themeMode)
        }
    }

    /* ============================================================
     * Onboarding & Settings
     * ============================================================ */

    /*
     * Menandai bahwa onboarding telah selesai.
     */
    fun completeOnboarding() {
        repository.completeOnboarding()
        _uiState.update { it.copy(onboardingCompleted = true) }
    }

    /*
     * Memperbarui nama profil pengguna.
     */
    fun updateProfileName(name: String) {
        val cleanedName = name.trim()
        if (cleanedName.isBlank()) return
        val username = _uiState.value.currentUser?.username ?: return
        repository.saveProfileName(username, cleanedName)
        _uiState.update { it.copy(profileName = cleanedName) }
    }

    /*
     * Memperbarui preferensi tema aplikasi.
     */
    fun updateThemeMode(themeMode: ThemeMode) {
        repository.saveThemeMode(themeMode)
        _uiState.update { it.copy(themeMode = themeMode) }
    }

    /* ============================================================
     * Transactions
     * ============================================================ */

    /*
     * Menambah transaksi baru ke Room.
     * Data transaksi otomatis terasosiasi dengan pengguna dan wallet yang aktif.
     */
    fun addTransaction(
        title: String,
        subtitle: String,
        amount: Double,
        type: TransactionType,
        category: String
    ) {
        val cleanedTitle = title.trim()
        if (cleanedTitle.isBlank() || amount <= 0.0) return
        val username = _uiState.value.currentUser?.username ?: return

        val transaction = FinanceTransaction(
            id = 0L,
            title = cleanedTitle,
            subtitle = subtitle.trim().ifBlank { if (type == TransactionType.INCOME) "Income" else "Expense" },
            amount = amount,
            type = type,
            category = category.trim().ifBlank { "Other" },
            createdAt = System.currentTimeMillis(),
            walletId = _uiState.value.selectedWalletId
        )

        viewModelScope.launch {
            repository.insertTransaction(transaction, username)
        }
    }

    /*
     * Menghapus transaksi berdasarkan ID.
     */
    fun deleteTransaction(id: Long) {
        if (id <= 0L) return
        viewModelScope.launch {
            repository.deleteTransaction(id)
        }
    }

    /* ============================================================
     * Wallets
     * ============================================================ */

    /*
     * Memilih wallet aktif untuk menampilkan data yang relevan.
     */
    fun selectWallet(walletId: Long) {
        _uiState.update { it.copy(selectedWalletId = walletId) }
    }

    /*
     * Menambahkan wallet baru untuk pengguna yang sedang login.
     */
    fun addWallet(name: String, initialBalance: Double) {
        if (name.isBlank()) return
        val username = _uiState.value.currentUser?.username ?: return

        val newWallet = Wallet(
            id = 0L,
            name = name.trim(),
            balance = initialBalance,
            colorKey = _uiState.value.wallets.size % 4
        )

        viewModelScope.launch {
            repository.insertWallet(newWallet, username)
        }
    }

    /*
     * Menghapus wallet beserta seluruh transaksi yang terkait.
     * Tidak diizinkan jika pengguna hanya punya satu wallet tersisa.
     */
    fun deleteWallet(walletId: Long) {
        val username = _uiState.value.currentUser?.username ?: return

        viewModelScope.launch {
            val count = repository.countWalletsByUser(username)
            if (count <= 1) return@launch

            repository.deleteWallet(walletId)

            // Jika wallet yang dihapus adalah wallet aktif, pindah ke wallet pertama yang tersisa.
            if (_uiState.value.selectedWalletId == walletId) {
                val remaining = _uiState.value.wallets.firstOrNull { it.id != walletId }
                if (remaining != null) {
                    _uiState.update { it.copy(selectedWalletId = remaining.id) }
                }
            }
        }
    }

    /* ============================================================
     * Saving Goals
     * ============================================================ */

    /*
     * Menambahkan target tabungan baru ke Room.
     */
    fun addSavingGoal(
        title: String,
        targetAmount: Double,
        initialAmount: Double,
        daysLeft: Int
    ) {
        if (title.isBlank() || targetAmount <= 0.0) return
        val username = _uiState.value.currentUser?.username ?: return

        val safeInitialAmount = initialAmount.coerceIn(0.0, targetAmount)
        val newGoal = SavingGoal(
            id = 0L,
            title = title.trim(),
            currentAmount = safeInitialAmount,
            targetAmount = targetAmount,
            daysLeft = daysLeft.coerceAtLeast(1),
            colorKey = _uiState.value.savings.size % 4
        )

        viewModelScope.launch {
            repository.insertSavingGoal(newGoal, username)
        }
    }

    /*
     * Menyetor sejumlah dana ke target tabungan.
     * Deposit juga dicatat sebagai transaksi pengeluaran.
     */
    fun depositToSaving(goalId: Long, amount: Double, category: String) {
        if (amount <= 0.0) return
        val username = _uiState.value.currentUser?.username ?: return
        val currentState = _uiState.value

        val goal = currentState.savings.firstOrNull { it.id == goalId } ?: return
        val remainingAmount = (goal.targetAmount - goal.currentAmount).coerceAtLeast(0.0)
        val safeAmount = amount.coerceAtMost(remainingAmount)

        if (safeAmount <= 0.0 || safeAmount > currentState.totalBalance) return

        viewModelScope.launch {
            // Perbarui currentAmount pada tabel saving_goals.
            repository.updateSavingGoal(
                goal.copy(currentAmount = goal.currentAmount + safeAmount),
                username
            )

            // Catat deposit sebagai transaksi pengeluaran.
            repository.insertTransaction(
                FinanceTransaction(
                    id = 0L,
                    title = goal.title,
                    subtitle = "Saving deposit",
                    amount = safeAmount,
                    type = TransactionType.EXPENSE,
                    category = category.ifBlank { "Saving" },
                    createdAt = System.currentTimeMillis(),
                    walletId = currentState.selectedWalletId
                ),
                username
            )
        }
    }

    /*
     * Menghapus target tabungan berdasarkan ID.
     */
    fun deleteSavingGoal(id: Long) {
        viewModelScope.launch {
            repository.deleteSavingGoal(id)
        }
    }

    /* ============================================================
     * Categories
     * ============================================================ */

    /*
     * Menambahkan kategori custom baru untuk pengguna aktif.
     */
    fun addCustomCategory(category: String) {
        val cleaned = category.trim()
        if (cleaned.isBlank()) return
        val username = _uiState.value.currentUser?.username ?: return

        viewModelScope.launch {
            repository.insertCustomCategory(cleaned, username)
        }
    }

    /* ============================================================
     * Reset Demo Data
     * ============================================================ */

    /*
     * Menghapus semua transaksi milik pengguna yang sedang login
     * dan mengosongkan data keuangan (tidak reset wallet/savings).
     */
    fun resetDemoData() {
        val username = _uiState.value.currentUser?.username ?: return
        viewModelScope.launch {
            repository.deleteAllTransactionsByUser(username)
        }
    }
}