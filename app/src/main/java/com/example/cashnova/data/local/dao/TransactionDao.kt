package com.example.cashnova.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cashnova.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    /*
     * Mengambil seluruh transaksi.
     * Transaksi terbaru ditampilkan paling atas.
     */
    @Query(
        """
        SELECT * FROM transactions
        ORDER BY createdAt DESC
        """
    )
    fun observeAllTransactions(): Flow<List<TransactionEntity>>

    /*
     * Mengambil satu transaksi berdasarkan ID.
     */
    @Query(
        """
        SELECT * FROM transactions
        WHERE id = :transactionId
        LIMIT 1
        """
    )
    suspend fun getTransactionById(
        transactionId: Long
    ): TransactionEntity?

    /*
     * Menambahkan transaksi baru.
     *
     * Jika terjadi konflik ID, data lama akan diganti.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(
        transaction: TransactionEntity
    ): Long

    /*
     * Memperbarui transaksi yang sudah ada.
     */
    @Update
    suspend fun updateTransaction(
        transaction: TransactionEntity
    )

    /*
     * Menghapus transaksi berdasarkan object.
     */
    @Delete
    suspend fun deleteTransaction(
        transaction: TransactionEntity
    )

    /*
     * Menghapus transaksi berdasarkan ID.
     */
    @Query(
        """
        DELETE FROM transactions
        WHERE id = :transactionId
        """
    )
    suspend fun deleteTransactionById(
        transactionId: Long
    )

    /*
     * Menghapus transaksi berdasarkan walletId.
     */
    @Query(
        """
        DELETE FROM transactions
        WHERE walletId = :walletId
        """
    )
    suspend fun deleteTransactionsByWalletId(
        walletId: Long
    )

    /*
     * Menghapus seluruh transaksi.
     */
    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()

    /*
     * Menghitung jumlah transaksi.
     */
    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun countTransactions(): Int
}