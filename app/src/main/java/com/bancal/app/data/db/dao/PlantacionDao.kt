package com.bancal.app.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.bancal.app.data.db.entity.PlantacionEntity
import com.bancal.app.domain.model.EstadoPlantacion
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantacionDao {

    // --- Queries filtradas por bancalId (preparado para multi-bancal) ---

    @Query("SELECT * FROM plantaciones WHERE bancalId = :bancalId AND estado != 'RETIRADO' ORDER BY posicionXCm")
    fun getActivasByBancal(bancalId: Long): Flow<List<PlantacionEntity>>

    @Query("SELECT * FROM plantaciones WHERE bancalId = :bancalId ORDER BY posicionXCm")
    fun getAllByBancal(bancalId: Long): Flow<List<PlantacionEntity>>

    @Query("SELECT * FROM plantaciones WHERE bancalId = :bancalId AND estado != 'RETIRADO' AND posicionXCm < :endX AND (posicionXCm + anchoCm) > :startX")
    suspend fun getInRangeByBancal(bancalId: Long, startX: Int, endX: Int): List<PlantacionEntity>

    @Query("SELECT * FROM plantaciones WHERE bancalId = :bancalId AND posicionXCm < :endX AND (posicionXCm + anchoCm) > :startX")
    suspend fun getAllInRangeByBancal(bancalId: Long, startX: Int, endX: Int): List<PlantacionEntity>

    // --- Queries globales (todos los bancales) ---

    @Query("SELECT * FROM plantaciones WHERE estado != 'RETIRADO' ORDER BY posicionXCm")
    fun getActivas(): Flow<List<PlantacionEntity>>

    @Query("SELECT * FROM plantaciones ORDER BY posicionXCm")
    fun getAll(): Flow<List<PlantacionEntity>>

    @Query("SELECT * FROM plantaciones WHERE id = :id")
    suspend fun getById(id: Long): PlantacionEntity?

    @Query("SELECT * FROM plantaciones WHERE estado != 'RETIRADO' AND posicionXCm < :endX AND (posicionXCm + anchoCm) > :startX")
    suspend fun getInRange(startX: Int, endX: Int): List<PlantacionEntity>

    // --- Agregados por bancal ---

    @Query("SELECT COUNT(*) FROM plantaciones WHERE bancalId = :bancalId AND estado != 'RETIRADO'")
    suspend fun countActivasByBancal(bancalId: Long): Int

    @Query("SELECT COALESCE(MAX(posicionXCm + anchoCm), 0) FROM plantaciones WHERE bancalId = :bancalId AND estado != 'RETIRADO'")
    suspend fun getMaxOcupadoByBancal(bancalId: Long): Int

    // --- CRUD ---

    @Insert
    suspend fun insert(plantacion: PlantacionEntity): Long

    @Update
    suspend fun update(plantacion: PlantacionEntity)

    @Delete
    suspend fun delete(plantacion: PlantacionEntity)

    @Query("UPDATE plantaciones SET estado = :estado WHERE id = :id")
    suspend fun updateEstado(id: Long, estado: EstadoPlantacion)

    @Query("SELECT COUNT(*) FROM plantaciones WHERE intercaladaCon = :plantacionId AND estado != 'RETIRADO'")
    suspend fun countIntercalados(plantacionId: Long): Int

    @Query("SELECT * FROM plantaciones WHERE intercaladaCon = :plantacionId AND estado != 'RETIRADO'")
    suspend fun getIntercalados(plantacionId: Long): List<PlantacionEntity>
}
