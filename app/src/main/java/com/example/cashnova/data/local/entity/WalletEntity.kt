package com.example.cashnova.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/*
 * Entity Room untuk tabel "wallets".
 * Setiap wallet dimiliki oleh satu pengguna (UserEntity).
 * Menggunakan Foreign Key agar integritas data terjaga:
 * jika user dihapus, semua wallet miliknya juga akan ikut terhapus.
 */
@Entity(
    tableName = "wallets",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["username"],
            childColumns = ["username"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("username")]
)
data class WalletEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    // Relasi ke pengguna pemilik wallet ini.
    val username: String,

    // Nama tampilan dompet/rekening.
    val name: String,

    // Saldo dasar pembuka dompet.
    val balance: Double = 0.0,

    // Key warna untuk pewarnaan UI (index 0..3).
    val colorKey: Int = 0
)
