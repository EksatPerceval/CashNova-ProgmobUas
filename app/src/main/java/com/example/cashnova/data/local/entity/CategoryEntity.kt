package com.example.cashnova.data.local.entity

import androidx.room.Entity
import androidx.room.Index

/*
 * Entity Room untuk tabel "categories".
 * Kategori bisa bersifat global (username = "", kategori bawaan aplikasi)
 * atau spesifik milik pengguna (kategori custom).
 * Primary Key adalah gabungan (name + username) agar unik per pengguna.
 */
@Entity(
    tableName = "categories",
    primaryKeys = ["name", "username"],
    indices = [Index("username")]
)
data class CategoryEntity(

    // Nama kategori (contoh: "Salary", "Food", "Transport").
    val name: String,

    // Username pemilik kategori custom. Gunakan string kosong "" untuk kategori default.
    val username: String = "",

    // Tipe kategori yang sesuai: "INCOME", "EXPENSE", atau "ALL".
    val type: String = "EXPENSE"
)
