package com.example.cashnova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cashnova.data.local.entity.WalletEntity
import kotlinx.coroutines.flow.Flow

/*
 * DAO untuk operasi tabel "wallets".
 * Setiap operasi difilter berdasarkan username agar data terisolasi per pengguna.
 */
@Dao
interface WalletDao {

    /*
     * Mengamati semua wallet milik pengguna secara reaktif.
     * Flow akan memancarkan daftar terbaru setiap kali data berubah.
     */
    @Query("SELECT * FROM wallets WHERE username = :username ORDER BY id ASC")
    fun observeWalletsByUser(username: String): Flow<List<WalletEntity>>

    /*
     * Mengambil satu wallet berdasarkan ID.
     */
    @Query("SELECT * FROM wallets WHERE id = :walletId LIMIT 1")
    suspend fun getWalletById(walletId: Long): WalletEntity?

    /*
     * Menambahkan wallet baru.
     * Mengembalikan ID yang dibuat secara otomatis oleh Room.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: WalletEntity): Long

    /*
     * Memperbarui data wallet (misalnya nama atau saldo dasar).
     */
    @Update
    suspend fun updateWallet(wallet: WalletEntity)

    /*
     * Menghapus wallet berdasarkan ID.
     * Transaksi yang terkait akan ikut terhapus karena ada ForeignKey CASCADE.
     */
    @Query("DELETE FROM wallets WHERE id = :walletId")
    suspend fun deleteWalletById(walletId: Long)

    /*
     * Menghitung jumlah wallet milik pengguna.
     * Digunakan untuk mencegah pengguna menghapus wallet terakhirnya.
     */
    @Query("SELECT COUNT(*) FROM wallets WHERE username = :username")
    suspend fun countWalletsByUser(username: String): Int
}
