package com.example.cashnova.data.local.mapper

import com.example.cashnova.data.FinanceTransaction
import com.example.cashnova.data.TransactionType
import com.example.cashnova.data.local.entity.TransactionEntity

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
        createdAt = createdAt
    )
}

fun FinanceTransaction.toTransactionEntity():
        TransactionEntity {

    return TransactionEntity(
        id = id,
        title = title,
        subtitle = subtitle,
        amount = amount,
        type = type.name,
        category = category,
        createdAt = createdAt
    )
}