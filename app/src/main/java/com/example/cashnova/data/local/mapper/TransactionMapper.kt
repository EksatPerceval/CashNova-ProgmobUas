package com.example.cashnova.data.local.mapper

import com.example.cashnova.data.FinanceTransaction
import com.example.cashnova.data.SavingGoal
import com.example.cashnova.data.TransactionType
import com.example.cashnova.data.Wallet
import com.example.cashnova.data.local.entity.SavingGoalEntity
import com.example.cashnova.data.local.entity.TransactionEntity
import com.example.cashnova.data.local.entity.WalletEntity

/* ============================================================
 * Transaction Mappers
 * ============================================================ */

/*
 * Mapping dari entity database ke model domain.
 * Aman terhadap data lama/invalid:
 * - Jika field `type` tidak valid, default ke EXPENSE agar aplikasi tidak crash.
 */
fun TransactionEntity.toFinanceTransaction(): FinanceTransaction {
    val transactionType = runCatching {
        TransactionType.valueOf(type)
    }.getOrDefault(TransactionType.EXPENSE)

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
fun FinanceTransaction.toTransactionEntity(username: String = ""): TransactionEntity {
    return TransactionEntity(
        id = id,
        username = username,
        title = title,
        subtitle = subtitle,
        amount = amount,
        type = type.name,
        category = category,
        createdAt = createdAt,
        walletId = walletId
    )
}

/* ============================================================
 * Wallet Mappers
 * ============================================================ */

/*
 * Mapping dari WalletEntity (Room) ke model domain Wallet.
 */
fun WalletEntity.toWallet(): Wallet {
    return Wallet(
        id = id,
        name = name,
        balance = balance,
        colorKey = colorKey
    )
}

/*
 * Mapping dari model domain Wallet ke WalletEntity (Room).
 */
fun Wallet.toWalletEntity(username: String): WalletEntity {
    return WalletEntity(
        id = id,
        username = username,
        name = name,
        balance = balance,
        colorKey = colorKey
    )
}

/* ============================================================
 * SavingGoal Mappers
 * ============================================================ */

/*
 * Mapping dari SavingGoalEntity (Room) ke model domain SavingGoal.
 */
fun SavingGoalEntity.toSavingGoal(): SavingGoal {
    return SavingGoal(
        id = id,
        title = title,
        currentAmount = currentAmount,
        targetAmount = targetAmount,
        daysLeft = daysLeft,
        colorKey = colorKey
    )
}

/*
 * Mapping dari model domain SavingGoal ke SavingGoalEntity (Room).
 */
fun SavingGoal.toSavingGoalEntity(username: String): SavingGoalEntity {
    return SavingGoalEntity(
        id = id,
        username = username,
        title = title,
        currentAmount = currentAmount,
        targetAmount = targetAmount,
        daysLeft = daysLeft,
        colorKey = colorKey
    )
}
