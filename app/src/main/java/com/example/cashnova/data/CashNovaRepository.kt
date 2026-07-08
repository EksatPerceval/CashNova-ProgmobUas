package com.example.cashnova.data

import android.content.Context
import com.example.cashnova.data.local.CashNovaDatabase
import com.example.cashnova.data.local.entity.CategoryEntity
import com.example.cashnova.data.local.entity.SavingGoalEntity
import com.example.cashnova.data.local.entity.UserEntity
import com.example.cashnova.data.local.entity.WalletEntity
import com.example.cashnova.data.local.mapper.toFinanceTransaction
import com.example.cashnova.data.local.mapper.toSavingGoal
import com.example.cashnova.data.local.mapper.toSavingGoalEntity
import com.example.cashnova.data.local.mapper.toTransactionEntity
import com.example.cashnova.data.local.mapper.toWallet
import com.example.cashnova.data.local.mapper.toWalletEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/*
 * Repository utama aplikasi.
 *
 * Tanggung jawab:
 * 1) Menjembatani seluruh sumber data Room (Transactions, Wallets, Savings, Categories, Users).
 * 2) Menyediakan data demo awal saat first install/reset.
 * 3) Mengelola sesi login sederhana via SharedPreferences.
 */
class CashNovaRepository(
    context: Context
) {

    /*
     * SharedPreferences hanya digunakan untuk:
     * - status onboarding;
     * - sesi login (remember_me, current_user);
     * - last_username untuk mendeteksi pergantian akun.
     * Semua data keuangan (Wallet, Savings, Categories) kini di Room.
     */
    private val preferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val db = CashNovaDatabase.getDatabase(context)
    private val transactionDao = db.transactionDao()
    private val walletDao = db.walletDao()
    private val savingGoalDao = db.savingGoalDao()
    private val categoryDao = db.categoryDao()
    private val userDao = db.userDao()

    /* ============================================================
     * Auth
     * ============================================================ */

    /*
     * Mendaftarkan akun baru ke dalam Room.
     * Mengembalikan true jika berhasil, false jika username sudah ada.
     */
    suspend fun registerUser(username: String, pin: String, profileName: String): Boolean {
        val existing = userDao.getUserByUsername(username)
        if (existing != null) return false

        val inserted = userDao.insertUser(
            UserEntity(username = username, pin = pin, profileName = profileName)
        )

        if (inserted > 0) {
            // Inisialisasi wallet default untuk pengguna baru.
            val defaultWalletId = walletDao.insertWallet(
                WalletEntity(username = username, name = "Main Wallet", balance = 0.0, colorKey = 0)
            )
            // Inisialisasi kategori default untuk pengguna baru.
            seedDefaultCategories(username)
        }
        return inserted > 0
    }

    /*
     * Memvalidasi kredensial login.
     * Mengembalikan UserEntity jika valid, null jika tidak.
     */
    suspend fun loginUser(username: String, pin: String): UserEntity? {
        return userDao.getUserByCredentials(username, pin)
    }

    /*
     * Menyimpan sesi login ke SharedPreferences.
     */
    fun saveSession(username: String, profileName: String, rememberMe: Boolean, themeMode: ThemeMode) {
        preferences.edit()
            .putString(KEY_CURRENT_USERNAME, username)
            .putString(KEY_PROFILE_NAME, username)
            .putBoolean(KEY_REMEMBER_ME, rememberMe)
            .putString(KEY_THEME_MODE, themeMode.name)
            .putString(KEY_LAST_USERNAME, username)
            .apply()
    }

    /*
     * Menghapus sesi login dari SharedPreferences.
     */
    fun clearSession() {
        preferences.edit()
            .remove(KEY_CURRENT_USERNAME)
            .remove(KEY_REMEMBER_ME)
            .apply()
    }

    /*
     * Memuat state awal dari SharedPreferences dan Room saat aplikasi dimulai.
     */
    fun loadInitialState(): CashNovaUiState {
        val onboardingDone = preferences.getBoolean(KEY_ONBOARDING, false)
        val rememberMe = preferences.getBoolean(KEY_REMEMBER_ME, false)
        val currentUsername = if (rememberMe) preferences.getString(KEY_CURRENT_USERNAME, null) else null
        val themeMode = ThemeMode.valueOf(
            preferences.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        )

        return if (currentUsername != null) {
            CashNovaUiState(
                onboardingCompleted = onboardingDone,
                currentUser = User(username = currentUsername, pin = ""),
                rememberMe = rememberMe,
                themeMode = themeMode,
                profileName = preferences.getString(KEY_PROFILE_NAME, currentUsername) ?: currentUsername
            )
        } else {
            CashNovaUiState(
                onboardingCompleted = onboardingDone,
                themeMode = themeMode
            )
        }
    }

    /*
     * Mendapatkan username terakhir yang login (untuk deteksi pergantian akun).
     */
    fun getLastUsername(): String? = preferences.getString(KEY_LAST_USERNAME, null)

    /*
     * Menyimpan username terakhir yang login.
     */
    fun saveLastUsername(username: String) {
        preferences.edit().putString(KEY_LAST_USERNAME, username).apply()
    }

    /* ============================================================
     * Onboarding & Theme
     * ============================================================ */

    fun completeOnboarding() {
        preferences.edit().putBoolean(KEY_ONBOARDING, true).apply()
    }

    fun saveThemeMode(themeMode: ThemeMode) {
        preferences.edit().putString(KEY_THEME_MODE, themeMode.name).apply()
    }

    fun saveProfileName(username: String, profileName: String) {
        preferences.edit().putString(KEY_PROFILE_NAME, profileName).apply()
    }

    /* ============================================================
     * Transactions
     * ============================================================ */

    /*
     * Mengamati transaksi milik pengguna aktif secara reaktif.
     */
    fun observeTransactionsByUser(username: String): Flow<List<FinanceTransaction>> {
        return transactionDao.observeTransactionsByUser(username).map { list ->
            list.map { it.toFinanceTransaction() }
        }
    }

    /*
     * Menambahkan transaksi baru ke Room.
     */
    suspend fun insertTransaction(transaction: FinanceTransaction, username: String): Long {
        return transactionDao.insertTransaction(transaction.toTransactionEntity(username))
    }

    /*
     * Memperbarui transaksi.
     */
    suspend fun updateTransaction(transaction: FinanceTransaction, username: String) {
        transactionDao.updateTransaction(transaction.toTransactionEntity(username))
    }

    /*
     * Menghapus transaksi berdasarkan ID.
     */
    suspend fun deleteTransaction(transactionId: Long) {
        transactionDao.deleteTransactionById(transactionId)
    }

    /*
     * Menghapus semua transaksi milik satu pengguna.
     */
    suspend fun deleteAllTransactionsByUser(username: String) {
        transactionDao.deleteAllTransactionsByUser(username)
    }

    /* ============================================================
     * Wallets
     * ============================================================ */

    /*
     * Mengamati daftar wallet milik pengguna aktif secara reaktif.
     */
    fun observeWalletsByUser(username: String): Flow<List<Wallet>> {
        return walletDao.observeWalletsByUser(username).map { list ->
            list.map { it.toWallet() }
        }
    }

    /*
     * Menambahkan wallet baru.
     * Mengembalikan ID wallet baru.
     */
    suspend fun insertWallet(wallet: Wallet, username: String): Long {
        return walletDao.insertWallet(wallet.toWalletEntity(username))
    }

    /*
     * Menghapus wallet beserta semua transaksi terkait (via CASCADE di DB).
     */
    suspend fun deleteWallet(walletId: Long) {
        walletDao.deleteWalletById(walletId)
    }

    /*
     * Mengecek apakah pengguna masih punya lebih dari satu wallet.
     */
    suspend fun countWalletsByUser(username: String): Int {
        return walletDao.countWalletsByUser(username)
    }

    /* ============================================================
     * Saving Goals
     * ============================================================ */

    /*
     * Mengamati daftar target tabungan milik pengguna aktif secara reaktif.
     */
    fun observeSavingsByUser(username: String): Flow<List<SavingGoal>> {
        return savingGoalDao.observeSavingsByUser(username).map { list ->
            list.map { it.toSavingGoal() }
        }
    }

    /*
     * Menambahkan target tabungan baru.
     */
    suspend fun insertSavingGoal(goal: SavingGoal, username: String): Long {
        return savingGoalDao.insertSavingGoal(goal.toSavingGoalEntity(username))
    }

    /*
     * Memperbarui target tabungan (misalnya currentAmount setelah deposit).
     */
    suspend fun updateSavingGoal(goal: SavingGoal, username: String) {
        savingGoalDao.updateSavingGoal(goal.toSavingGoalEntity(username))
    }

    /*
     * Menghapus target tabungan berdasarkan ID.
     */
    suspend fun deleteSavingGoal(goalId: Long) {
        savingGoalDao.deleteSavingGoalById(goalId)
    }

    /* ============================================================
     * Categories
     * ============================================================ */

    /*
     * Mengamati seluruh kategori (default + custom) untuk pengguna aktif.
     */
    fun observeCategoriesByUser(username: String): Flow<List<String>> {
        return categoryDao.observeCategoriesByUser(username).map { list ->
            list.map { it.name }
        }
    }

    /*
     * Menambahkan kategori custom baru untuk pengguna.
     * Jika sudah ada, tidak akan menyisipkan duplikat.
     */
    suspend fun insertCustomCategory(name: String, username: String): Boolean {
        val count = categoryDao.countByNameForUser(name, username)
        if (count > 0) return false
        return categoryDao.insertCategory(
            CategoryEntity(name = name, username = username)
        ) > 0
    }

    /*
     * Menyemai kategori default ke tabel categories untuk pengguna baru.
     */
    suspend fun seedDefaultCategories(username: String) {
        DEFAULT_CATEGORIES.forEach { categoryName ->
            categoryDao.insertCategory(
                CategoryEntity(name = categoryName, username = "", type = "ALL")
            )
        }
    }

    /* ============================================================
     * Legacy / Initialization
     * ============================================================ */

    /*
     * Dijalankan sekali saat startup setelah login untuk memastikan
     * wallet default ada (dalam kasus data terhapus).
     */
    suspend fun ensureDefaultWalletExists(username: String): Long {
        val existing = walletDao.observeWalletsByUser(username)
        // Cek apakah wallet sudah ada di DB menggunakan count
        val count = walletDao.countWalletsByUser(username)
        if (count == 0) {
            return walletDao.insertWallet(
                WalletEntity(username = username, name = "Main Wallet", balance = 0.0, colorKey = 0)
            )
        }
        return -1L
    }

    companion object {
        private const val PREFS_NAME = "cashnova_preferences"
        private const val KEY_ONBOARDING = "onboarding_completed"
        private const val KEY_CURRENT_USERNAME = "current_username"
        private const val KEY_REMEMBER_ME = "remember_me"
        private const val KEY_LAST_USERNAME = "last_username"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_PROFILE_NAME = "profile_name"

        val DEFAULT_CATEGORIES = listOf(
            "Salary", "Freelance", "Food", "Transport", "Shopping",
            "Utility", "Subscription", "Payment", "Saving", "Other"
        )
    }
}