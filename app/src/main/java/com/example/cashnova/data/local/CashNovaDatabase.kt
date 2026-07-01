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
    version = 2,
    exportSchema = true
)
abstract class CashNovaDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao

    companion object {

        @Volatile
        private var INSTANCE: CashNovaDatabase? = null

        fun getDatabase(
            context: Context
        ): CashNovaDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CashNovaDatabase::class.java,
                    DATABASE_NAME
                )
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