package com.bancal.app.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.bancal.app.data.db.entity.BancalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BancalDao {

    @Query("SELECT * FROM bancales ORDER BY id")
    fun getAll(): Flow<List<BancalEntity>>

    @Query("SELECT * FROM bancales WHERE id = :id")
    suspend fun getById(id: Long): BancalEntity?

    @Query("SELECT * FROM bancales WHERE id = :id")
    fun getByIdFlow(id: Long): Flow<BancalEntity?>

    @Query("SELECT COUNT(*) FROM bancales")
    suspend fun count(): Int

    @Insert
    suspend fun insert(bancal: BancalEntity): Long

    @Update
    suspend fun update(bancal: BancalEntity)

    @Delete
    suspend fun delete(bancal: BancalEntity)

    @Query("SELECT * FROM bancales WHERE tarpingDesde IS NOT NULL")
    suspend fun getConTarping(): List<BancalEntity>

    @Query("SELECT * FROM bancales WHERE abonoVerdeDesde IS NOT NULL")
    suspend fun getConAbonoVerde(): List<BancalEntity>
}
