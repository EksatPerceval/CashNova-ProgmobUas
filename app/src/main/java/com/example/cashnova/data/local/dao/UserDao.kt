package com.example.cashnova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cashnova.data.local.entity.UserEntity

/*
 * DAO untuk operasi tabel "users".
 * Menangani registrasi, login, dan pembaruan profil pengguna.
 */
@Dao
interface UserDao {

    /*
     * Menyimpan user baru ke dalam database.
     * Jika username sudah ada, akan diabaikan (IGNORE).
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: UserEntity): Long

    /*
     * Memperbarui data profil user yang sudah ada.
     */
    @Update
    suspend fun updateUser(user: UserEntity)

    /*
     * Mencari user berdasarkan username untuk keperluan login.
     */
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    /*
     * Mencari user berdasarkan username dan PIN (digunakan untuk verifikasi login).
     */
    @Query("SELECT * FROM users WHERE username = :username AND pin = :pin LIMIT 1")
    suspend fun getUserByCredentials(username: String, pin: String): UserEntity?
}
