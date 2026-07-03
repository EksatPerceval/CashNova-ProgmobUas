package com.example.cashnova.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cashnova.data.local.dao.TransactionDao
import com.example.cashnova.data.local.entity.TransactionEntity

/*
 * Definisi Room Database utama untuk aplikasi CashNova.
 *
 * Catatan:
 * - Saat ini hanya memiliki satu entity utama: TransactionEntity.
 * - Version mengikuti evolusi skema Room (lihat folder app/schemas).
 */
@Database(
    entities = [
        TransactionEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class CashNovaDatabase : RoomDatabase() {

    /*
     * Satu-satunya DAO yang dibutuhkan saat ini untuk operasi transaksi.
     */
    abstract fun transactionDao(): TransactionDao

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
                     * Cocok untuk fase demo/prototipe, namun pada production
                     * biasanya diganti migration terarah agar data tidak hilang.
                     */
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance

                instance
            }
        }

        private const val DATABASE_NAME =
            "cashnova_database"
    }
}
