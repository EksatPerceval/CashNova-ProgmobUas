package com.example.cashnova.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/*
 * Entity Room untuk tabel "transactions".
 * Setiap transaksi terhubung ke wallet dan pengguna tertentu.
 * Jika wallet dihapus, transaksi terkait juga ikut dihapus (CASCADE).
 */
@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = WalletEntity::class,
            parentColumns = ["id"],
            childColumns = ["walletId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("walletId"), Index("username")]
)
data class TransactionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    // Username pengguna pemilik transaksi ini (untuk isolasi data multi-user).
    val username: String = "",

    // Judul transaksi (contoh: "Gaji Bulanan", "Belanja Harian").
    val title: String,

    // Keterangan tambahan transaksi.
    val subtitle: String,

    // Nominal transaksi (selalu positif, makna +/− ditentukan oleh field `type`).
    val amount: Double,

    /*
     * Jenis transaksi yang tersimpan sebagai teks:
     * - "INCOME"
     * - "EXPENSE"
     */
    val type: String,

    // Kategori transaksi untuk kebutuhan grouping/filter analytics.
    val category: String,

    /*
     * Waktu transaksi dalam Unix timestamp (epoch millis).
     * Dipakai untuk sorting dan analisis tren per waktu.
     */
    val createdAt: Long,

    // Relasi ke wallet asal transaksi.
    val walletId: Long = 0L
)

