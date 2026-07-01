package com.example.cashnova.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val title: String,

    val subtitle: String,

    val amount: Double,

    /*
     * Nilainya nanti:
     * "INCOME" atau "EXPENSE"
     */
    val type: String,

    val category: String,

    /*
     * Waktu transaksi dalam format Unix timestamp.
     */
    val createdAt: Long
)