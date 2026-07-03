package com.example.cashnova.data.local.mapper

import com.example.cashnova.data.FinanceTransaction
import com.example.cashnova.data.TransactionType
import com.example.cashnova.data.local.entity.TransactionEntity

/*
 * Mapping dari entity database ke model domain.
 *
 * Aman terhadap data lama/invalid:
 * - Jika field `type` tidak valid, default ke EXPENSE agar aplikasi tidak crash.
 */
fun TransactionEntity.toFinanceTransaction():
        FinanceTransaction {

    val transactionType =
        runCatching {
            TransactionType.valueOf(type)
        }.getOrDefault(
            TransactionType.EXPENSE
        )

    return FinanceTransaction(
        id = id,
        title = title,
        subtitle = subtitle,
        amount = amount,
        type = transactionType,
        category = category,
        createdAt = createdAt,
        walletId = walletId
    )
}

/*
 * Mapping dari model domain ke entity database.
 * enum TransactionType disimpan sebagai String (`type.name`) di tabel transaksi.
 */
fun FinanceTransaction.toTransactionEntity():
        TransactionEntity {

    return TransactionEntity(
        id = id,
        title = title,
        subtitle = subtitle,
        amount = amount,
        type = type.name,
        category = category,
        createdAt = createdAt,
        walletId = walletId
    )
}
