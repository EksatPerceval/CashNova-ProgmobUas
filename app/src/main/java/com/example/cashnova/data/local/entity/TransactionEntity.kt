package com.example.cashnova.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
 * Entity Room untuk tabel "transactions".
 *
 * Catatan desain:
 * - type disimpan sebagai String agar sederhana saat serialisasi DB.
 * - Mapping ke enum domain (TransactionType) ditangani di layer mapper.
 */
@Entity(tableName = "transactions")
data class TransactionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

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

    // Relasi sederhana ke wallet aktif/asal transaksi.
    val walletId: Long = 0L
)
