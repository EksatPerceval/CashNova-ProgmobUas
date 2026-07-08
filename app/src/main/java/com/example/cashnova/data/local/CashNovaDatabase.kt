package com.example.cashnova.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cashnova.data.local.dao.CategoryDao
import com.example.cashnova.data.local.dao.SavingGoalDao
import com.example.cashnova.data.local.dao.TransactionDao
import com.example.cashnova.data.local.dao.UserDao
import com.example.cashnova.data.local.dao.WalletDao
import com.example.cashnova.data.local.entity.CategoryEntity
import com.example.cashnova.data.local.entity.SavingGoalEntity
import com.example.cashnova.data.local.entity.TransactionEntity
import com.example.cashnova.data.local.entity.UserEntity
import com.example.cashnova.data.local.entity.WalletEntity

/*
 * Definisi Room Database utama untuk aplikasi CashNova.
 *
 * Perubahan skema (versi naik) menggunakan fallbackToDestructiveMigration
 * yang akan menghapus dan membuat ulang semua tabel secara otomatis.
 *
 * Tabel yang terdaftar:
 * - users          : Data akun pengguna
 * - wallets        : Dompet/rekening per pengguna
 * - transactions   : Riwayat transaksi keuangan
 * - saving_goals   : Target tabungan per pengguna
 * - categories     : Kategori transaksi (default + custom)
 */
@Database(
    entities = [
        UserEntity::class,
        WalletEntity::class,
        TransactionEntity::class,
        SavingGoalEntity::class,
        CategoryEntity::class
    ],
    version = 4,
    exportSchema = true
)
abstract class CashNovaDatabase : RoomDatabase() {

    /* DAO untuk tabel users. */
    abstract fun userDao(): UserDao

    /* DAO untuk tabel wallets. */
    abstract fun walletDao(): WalletDao

    /* DAO untuk tabel transactions. */
    abstract fun transactionDao(): TransactionDao

    /* DAO untuk tabel saving_goals. */
    abstract fun savingGoalDao(): SavingGoalDao

    /* DAO untuk tabel categories. */
    abstract fun categoryDao(): CategoryDao

    companion object {

        /*
         * Singleton instance database agar hanya satu koneksi Room aktif
         * sepanjang lifecycle aplikasi.
         */
        @Volatile
        private var INSTANCE: CashNovaDatabase? = null

        /*
         * Mengembalikan instance database yang sudah ada, atau membuat baru jika belum ada.
         * Menggunakan synchronized untuk thread-safety.
         */
        fun getDatabase(
            context: Context
        ): CashNovaDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CashNovaDatabase::class.java,
                    DATABASE_NAME
                )
                    /*
                     * Fallback ini akan mereset tabel saat versi schema berubah
                     * dan migration spesifik belum disediakan.
                     */
                    .fallbackToDestructiveMigration(true)
                    .build()

                INSTANCE = instance

                instance
            }
        }

        private const val DATABASE_NAME = "cashnova_database"
    }
}
