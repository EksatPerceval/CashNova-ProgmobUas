package com.example.cashnova.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
 * Entity Room untuk tabel "users".
 * Menyimpan informasi akun pengguna secara lokal.
 * username digunakan sebagai Primary Key karena bersifat unik.
 */
@Entity(tableName = "users")
data class UserEntity(

    // Username unik sebagai identifier pengguna.
    @PrimaryKey
    val username: String,

    // PIN pengguna (disimpan sebagai plain text untuk saat ini).
    val pin: String,

    // Nama tampilan profil pengguna.
    val profileName: String = "User"
)
