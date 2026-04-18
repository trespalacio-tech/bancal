package com.bancal.app.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.bancal.app.data.db.entity.TratamientoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TratamientoDao {

    @Query("SELECT * FROM tratamientos ORDER BY fecha DESC")
    fun getAll(): Flow<List<TratamientoEntity>>

    @Query("SELECT * FROM tratamientos WHERE plantacionId = :plantacionId ORDER BY fecha DESC")
    fun getByPlantacion(plantacionId: Long): Flow<List<TratamientoEntity>>

    @Query("SELECT * FROM tratamientos WHERE plantacionId IS NULL ORDER BY fecha DESC")
    fun getGenerales(): Flow<List<TratamientoEntity>>

    @Insert
    suspend fun insert(tratamiento: TratamientoEntity): Long

    @Delete
    suspend fun delete(tratamiento: TratamientoEntity)

    @Query("SELECT MAX(fecha) FROM tratamientos WHERE tipo = :tipo")
    suspend fun getUltimaFecha(tipo: String): Long?
}
