package com.example.cashnova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cashnova.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

/*
 * DAO untuk operasi tabel "categories".
 * Kategori bawaan (username = "") dan kategori custom (username = user) digabungkan saat ditampilkan.
 */
@Dao
interface CategoryDao {

    /*
     * Mengamati semua kategori yang bisa digunakan oleh pengguna:
     * kategori default (username = "") UNION kategori custom milik pengguna.
     */
    @Query("SELECT * FROM categories WHERE username = '' OR username = :username ORDER BY name ASC")
    fun observeCategoriesByUser(username: String): Flow<List<CategoryEntity>>

    /*
     * Menambahkan kategori baru (bisa default atau custom).
     * Jika kombinasi (name+username) sudah ada, abaikan.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(category: CategoryEntity): Long

    /*
     * Menghapus satu kategori custom berdasarkan nama dan username.
     */
    @Query("DELETE FROM categories WHERE name = :name AND username = :username")
    suspend fun deleteCategoryByNameAndUser(name: String, username: String)

    /*
     * Memeriksa apakah kategori dengan nama tertentu sudah ada untuk pengguna ini.
     */
    @Query("SELECT COUNT(*) FROM categories WHERE name = :name AND (username = '' OR username = :username)")
    suspend fun countByNameForUser(name: String, username: String): Int

    /*
     * Menghapus semua kategori custom milik satu pengguna.
     * Digunakan saat data pengguna di-reset.
     */
    @Query("DELETE FROM categories WHERE username = :username")
    suspend fun deleteAllCustomCategoriesForUser(username: String)
}
