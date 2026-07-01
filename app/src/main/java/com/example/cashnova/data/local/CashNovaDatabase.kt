package com.example.cashnova.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cashnova.data.local.dao.TransactionDao
import com.example.cashnova.data.local.entity.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class CashNovaDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao

    companion object {

        /*
         * Volatile memastikan semua thread melihat instance terbaru.
         */
        @Volatile
        private var INSTANCE: CashNovaDatabase? = null

        fun getDatabase(
            context: Context
        ): CashNovaDatabase {

            /*
             * Jika database sudah pernah dibuat,
             * gunakan instance yang sama.
             */
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CashNovaDatabase::class.java,
                    DATABASE_NAME
                ).build()

                INSTANCE = instance

                instance
            }
        }

        private const val DATABASE_NAME =
            "cashnova_database"
    }
}