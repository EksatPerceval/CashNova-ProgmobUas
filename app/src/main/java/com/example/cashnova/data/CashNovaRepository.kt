package com.example.cashnova.data

import android.content.Context
import com.example.cashnova.data.local.CashNovaDatabase
import com.example.cashnova.data.local.mapper.toFinanceTransaction
import com.example.cashnova.data.local.mapper.toTransactionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class CashNovaRepository(
    context: Context
) {

    /*
     * SharedPreferences masih digunakan untuk:
     * - status onboarding;
     * - nama pengguna;
     * - saldo pembuka;
     * - target tabungan.
     *
     * Transaksi dipindahkan ke Room.
     */
    private val preferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    /*
     * Mengambil DAO dari Room Database.
     */
    private val transactionDao =
        CashNovaDatabase
            .getDatabase(context)
            .transactionDao()

    /*
     * Mengamati seluruh transaksi dari Room.
     *
     * Ketika tabel transaksi berubah,
     * Flow akan mengirim daftar terbaru.
     */
    fun observeTransactions(): Flow<List<FinanceTransaction>> {
        return transactionDao
            .observeAllTransactions()
            .map { entities ->
                entities.map { entity ->
                    entity.toFinanceTransaction()
                }
            }
    }

    /*
     * Memuat data yang masih tersimpan
     * di SharedPreferences.
     *
     * Daftar transaksi dibuat kosong karena
     * transaksi akan dimuat dari Room melalui Flow.
     */
    fun loadPreferencesState(): CashNovaUiState {

        if (!preferences.contains(KEY_SAVINGS)) {
            val initialState = demoPreferencesState(
                onboardingCompleted = false
            )

            savePreferences(initialState)

            return initialState
        }

        return runCatching {
            CashNovaUiState(
                onboardingCompleted =
                    preferences.getBoolean(
                        KEY_ONBOARDING,
                        false
                    ),

                profileName =
                    preferences.getString(
                        KEY_PROFILE_NAME,
                        DEFAULT_PROFILE_NAME
                    )
                        .orEmpty()
                        .ifBlank {
                            DEFAULT_PROFILE_NAME
                        },

                openingBalance =
                    preferences.getLong(
                        KEY_OPENING_BALANCE,
                        java.lang.Double.doubleToRawLongBits(
                            DEFAULT_OPENING_BALANCE
                        )
                    ).let { bits ->
                        java.lang.Double.longBitsToDouble(bits)
                    },

                /*
                 * Transaksi tidak lagi dibaca
                 * dari SharedPreferences.
                 */
                transactions = emptyList(),

                savings = decodeSavings(
                    preferences.getString(
                        KEY_SAVINGS,
                        "[]"
                    ).orEmpty()
                )
            )
        }.getOrElse {
            demoPreferencesState(
                onboardingCompleted = false
            )
        }
    }

    /*
     * Menyimpan data selain transaksi.
     */
    fun savePreferences(
        state: CashNovaUiState
    ) {
        preferences
            .edit()
            .putBoolean(
                KEY_ONBOARDING,
                state.onboardingCompleted
            )
            .putString(
                KEY_PROFILE_NAME,
                state.profileName
            )
            .putLong(
                KEY_OPENING_BALANCE,
                java.lang.Double.doubleToRawLongBits(
                    state.openingBalance
                )
            )
            .putString(
                KEY_SAVINGS,
                encodeSavings(state.savings)
            )
            .apply()
    }

    /*
     * Menyiapkan transaksi Room pada penggunaan pertama.
     *
     * Apabila versi aplikasi lama memiliki transaksi
     * di SharedPreferences, transaksi tersebut dipindahkan
     * terlebih dahulu ke Room.
     */
    suspend fun initializeTransactions() {

        val roomAlreadyInitialized =
            preferences.getBoolean(
                KEY_ROOM_INITIALIZED,
                false
            )

        if (roomAlreadyInitialized) {
            return
        }

        val transactionCount =
            transactionDao.countTransactions()

        if (transactionCount == 0) {

            val legacyTransactions =
                loadLegacyTransactions()

            val initialTransactions =
                legacyTransactions.ifEmpty {
                    demoTransactions()
                }

            initialTransactions.forEach { transaction ->
                transactionDao.insertTransaction(
                    transaction.toTransactionEntity()
                )
            }
        }

        /*
         * Setelah migrasi selesai, transaksi JSON lama
         * dihapus agar tidak ada dua sumber data.
         */
        preferences
            .edit()
            .remove(KEY_LEGACY_TRANSACTIONS)
            .putBoolean(
                KEY_ROOM_INITIALIZED,
                true
            )
            .apply()
    }

    /*
     * Menambahkan transaksi baru.
     */
    suspend fun insertTransaction(
        transaction: FinanceTransaction
    ): Long {
        return transactionDao.insertTransaction(
            transaction.toTransactionEntity()
        )
    }

    /*
     * Memperbarui transaksi.
     */
    suspend fun updateTransaction(
        transaction: FinanceTransaction
    ) {
        transactionDao.updateTransaction(
            transaction.toTransactionEntity()
        )
    }

    /*
     * Menghapus transaksi berdasarkan ID.
     */
    suspend fun deleteTransaction(
        transactionId: Long
    ) {
        transactionDao.deleteTransactionById(
            transactionId
        )
    }

    /*
     * Menghapus seluruh transaksi.
     */
    suspend fun deleteAllTransactions() {
        transactionDao.deleteAllTransactions()
    }

    /*
     * Mengembalikan seluruh data aplikasi
     * ke data demo awal.
     */
    suspend fun resetAllData(): CashNovaUiState {

        transactionDao.deleteAllTransactions()

        demoTransactions().forEach { transaction ->
            transactionDao.insertTransaction(
                transaction.toTransactionEntity()
            )
        }

        val resetState =
            demoPreferencesState(
                onboardingCompleted = true
            )

        savePreferences(resetState)

        preferences
            .edit()
            .putBoolean(
                KEY_ROOM_INITIALIZED,
                true
            )
            .apply()

        return resetState
    }

    /*
     * Membaca transaksi dari versi penyimpanan lama.
     *
     * Fungsi ini hanya digunakan satu kali
     * ketika migrasi ke Room.
     */
    private fun loadLegacyTransactions():
            List<FinanceTransaction> {

        val legacyJson =
            preferences.getString(
                KEY_LEGACY_TRANSACTIONS,
                null
            ) ?: return emptyList()

        return runCatching {
            decodeLegacyTransactions(
                legacyJson
            )
        }.getOrElse {
            emptyList()
        }
    }

    private fun decodeLegacyTransactions(
        json: String
    ): List<FinanceTransaction> {

        val array = JSONArray(json)

        return buildList {

            for (index in 0 until array.length()) {

                val item =
                    array.getJSONObject(index)

                val transactionType =
                    runCatching {
                        TransactionType.valueOf(
                            item.getString("type")
                        )
                    }.getOrDefault(
                        TransactionType.EXPENSE
                    )

                add(
                    FinanceTransaction(
                        id = item.optLong(
                            "id",
                            0L
                        ),
                        title = item.optString(
                            "title",
                            "Transaction"
                        ),
                        subtitle = item.optString(
                            "subtitle",
                            ""
                        ),
                        amount = item.optDouble(
                            "amount",
                            0.0
                        ),
                        type = transactionType,
                        category = item.optString(
                            "category",
                            "Other"
                        ),
                        createdAt = item.optLong(
                            "createdAt",
                            System.currentTimeMillis()
                        )
                    )
                )
            }
        }
    }

    /*
     * Mengubah daftar target tabungan menjadi JSON.
     */
    private fun encodeSavings(
        items: List<SavingGoal>
    ): String {

        val array = JSONArray()

        items.forEach { item ->

            val jsonObject =
                JSONObject()
                    .put(
                        "id",
                        item.id
                    )
                    .put(
                        "title",
                        item.title
                    )
                    .put(
                        "currentAmount",
                        item.currentAmount
                    )
                    .put(
                        "targetAmount",
                        item.targetAmount
                    )
                    .put(
                        "daysLeft",
                        item.daysLeft
                    )
                    .put(
                        "colorKey",
                        item.colorKey
                    )

            array.put(jsonObject)
        }

        return array.toString()
    }

    /*
     * Mengubah JSON menjadi daftar target tabungan.
     */
    private fun decodeSavings(
        json: String
    ): List<SavingGoal> {

        val array = JSONArray(json)

        return buildList {

            for (index in 0 until array.length()) {

                val item =
                    array.getJSONObject(index)

                add(
                    SavingGoal(
                        id = item.getLong("id"),
                        title = item.getString(
                            "title"
                        ),
                        currentAmount =
                            item.getDouble(
                                "currentAmount"
                            ),
                        targetAmount =
                            item.getDouble(
                                "targetAmount"
                            ),
                        daysLeft =
                            item.getInt(
                                "daysLeft"
                            ),
                        colorKey =
                            item.optInt(
                                "colorKey",
                                index % 4
                            )
                    )
                )
            }
        }
    }

    /*
     * Data SharedPreferences awal.
     */
    private fun demoPreferencesState(
        onboardingCompleted: Boolean
    ): CashNovaUiState {

        return CashNovaUiState(
            onboardingCompleted =
                onboardingCompleted,

            profileName =
                DEFAULT_PROFILE_NAME,

            openingBalance =
                DEFAULT_OPENING_BALANCE,

            /*
             * Transaksi dimuat dari Room.
             */
            transactions = emptyList(),

            savings = listOf(
                SavingGoal(
                    id = 101L,
                    title = "iPhone 13 Mini",
                    currentAmount = 300.0,
                    targetAmount = 699.0,
                    daysLeft = 14,
                    colorKey = 0
                ),
                SavingGoal(
                    id = 102L,
                    title = "Macbook Pro M1",
                    currentAmount = 300.0,
                    targetAmount = 1_499.0,
                    daysLeft = 30,
                    colorKey = 1
                ),
                SavingGoal(
                    id = 103L,
                    title = "Car",
                    currentAmount = 10_000.0,
                    targetAmount = 20_000.0,
                    daysLeft = 30,
                    colorKey = 2
                ),
                SavingGoal(
                    id = 104L,
                    title = "House",
                    currentAmount = 32_500.0,
                    targetAmount = 65_000.0,
                    daysLeft = 1_095,
                    colorKey = 3
                )
            )
        )
    }

    /*
     * Daftar transaksi demo yang dimasukkan
     * ke Room pada penggunaan pertama.
     */
    private fun demoTransactions():
            List<FinanceTransaction> {

        val now =
            System.currentTimeMillis()

        val oneDay =
            86_400_000L

        return listOf(
            FinanceTransaction(
                id = 1L,
                title = "Adobe Illustrator",
                subtitle = "Subscription fee",
                amount = 32.0,
                type = TransactionType.EXPENSE,
                category = "Subscription",
                createdAt = now
            ),
            FinanceTransaction(
                id = 2L,
                title = "Dribbble",
                subtitle = "Subscription fee",
                amount = 15.0,
                type = TransactionType.EXPENSE,
                category = "Subscription",
                createdAt = now - 1_000L
            ),
            FinanceTransaction(
                id = 3L,
                title = "PayPal",
                subtitle = "Online payment",
                amount = 953.0,
                type = TransactionType.EXPENSE,
                category = "Payment",
                createdAt = now - oneDay
            ),
            FinanceTransaction(
                id = 4L,
                title = "Mobile Data",
                subtitle = "Internet package",
                amount = 1_000.0,
                type = TransactionType.EXPENSE,
                category = "Utility",
                createdAt = now - (2 * oneDay)
            ),
            FinanceTransaction(
                id = 5L,
                title = "Sony Camera",
                subtitle = "Shopping fee",
                amount = 2_000.0,
                type = TransactionType.EXPENSE,
                category = "Shopping",
                createdAt = now - (3 * oneDay)
            ),
            FinanceTransaction(
                id = 6L,
                title = "Car",
                subtitle = "Saving",
                amount = 5_000.0,
                type = TransactionType.EXPENSE,
                category = "Saving",
                createdAt = now - (4 * oneDay)
            ),
            FinanceTransaction(
                id = 7L,
                title = "House",
                subtitle = "Saving",
                amount = 8_000.0,
                type = TransactionType.EXPENSE,
                category = "Saving",
                createdAt = now - (5 * oneDay)
            ),
            FinanceTransaction(
                id = 8L,
                title = "Upwork",
                subtitle = "Freelance income",
                amount = 3_000.0,
                type = TransactionType.INCOME,
                category = "Freelance",
                createdAt = now - (6 * oneDay)
            ),
            FinanceTransaction(
                id = 9L,
                title = "Freepik",
                subtitle = "Design income",
                amount = 3_000.0,
                type = TransactionType.INCOME,
                category = "Freelance",
                createdAt = now - (7 * oneDay)
            ),
            FinanceTransaction(
                id = 10L,
                title = "Envato",
                subtitle = "Marketplace income",
                amount = 2_000.0,
                type = TransactionType.INCOME,
                category = "Freelance",
                createdAt = now - (8 * oneDay)
            ),
            FinanceTransaction(
                id = 11L,
                title = "Salary",
                subtitle = "Monthly salary",
                amount = 12_000.0,
                type = TransactionType.INCOME,
                category = "Salary",
                createdAt = now - (9 * oneDay)
            )
        )
    }

    companion object {

        private const val PREFS_NAME =
            "cashnova_preferences"

        private const val KEY_ONBOARDING =
            "onboarding_completed"

        private const val KEY_PROFILE_NAME =
            "profile_name"

        private const val KEY_OPENING_BALANCE =
            "opening_balance"

        private const val KEY_SAVINGS =
            "savings"

        /*
         * Nama key transaksi dari repository lama.
         * Hanya digunakan untuk migrasi satu kali.
         */
        private const val KEY_LEGACY_TRANSACTIONS =
            "transactions"

        private const val KEY_ROOM_INITIALIZED =
            "room_transactions_initialized"

        private const val DEFAULT_PROFILE_NAME =
            "Asep Resing"

        private const val DEFAULT_OPENING_BALANCE =
            22_000.40
    }
}