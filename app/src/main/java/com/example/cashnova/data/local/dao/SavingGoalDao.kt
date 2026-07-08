package com.example.cashnova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cashnova.data.local.entity.SavingGoalEntity
import kotlinx.coroutines.flow.Flow

/*
 * DAO untuk operasi tabel "saving_goals".
 * Semua operasi difilter berdasarkan username agar data per pengguna terisolasi.
 */
@Dao
interface SavingGoalDao {

    /*
     * Mengamati semua target tabungan milik pengguna secara reaktif.
     * Ditampilkan berurutan dari yang terbaru (ID tertinggi di atas).
     */
    @Query("SELECT * FROM saving_goals WHERE username = :username ORDER BY id DESC")
    fun observeSavingsByUser(username: String): Flow<List<SavingGoalEntity>>

    /*
     * Menambahkan target tabungan baru.
     * Mengembalikan ID yang dibuat secara otomatis oleh Room.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingGoal(savingGoal: SavingGoalEntity): Long

    /*
     * Memperbarui data target tabungan (misalnya currentAmount setelah deposit).
     */
    @Update
    suspend fun updateSavingGoal(savingGoal: SavingGoalEntity)

    /*
     * Menghapus target tabungan berdasarkan ID.
     */
    @Query("DELETE FROM saving_goals WHERE id = :goalId")
    suspend fun deleteSavingGoalById(goalId: Long)
}
