package com.example.cashnova.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/*
 * Entity Room untuk tabel "saving_goals".
 * Setiap target tabungan dimiliki oleh satu pengguna (UserEntity).
 * Jika user dihapus, semua saving goals miliknya juga ikut terhapus.
 */
@Entity(
    tableName = "saving_goals",
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
data class SavingGoalEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    // Relasi ke pengguna pemilik saving goal ini.
    val username: String,

    // Nama/judul target tabungan.
    val title: String,

    // Jumlah yang sudah terkumpul saat ini.
    val currentAmount: Double = 0.0,

    // Jumlah target yang ingin dicapai.
    val targetAmount: Double,

    // Sisa hari menuju tenggat waktu target.
    val daysLeft: Int,

    // Key warna untuk pewarnaan UI (index 0..3).
    val colorKey: Int = 0
)
