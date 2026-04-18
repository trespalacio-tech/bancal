package com.bancal.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bancal.app.data.db.entity.AsociacionEntity

@Dao
interface AsociacionDao {

    @Query("""
        SELECT * FROM asociaciones
        WHERE cultivoId1 = :cultivoId OR cultivoId2 = :cultivoId
    """)
    suspend fun getByCultivo(cultivoId: Long): List<AsociacionEntity>

    @Query("""
        SELECT * FROM asociaciones
        WHERE (cultivoId1 = :id1 AND cultivoId2 = :id2)
           OR (cultivoId1 = :id2 AND cultivoId2 = :id1)
        LIMIT 1
    """)
    suspend fun getAsociacion(id1: Long, id2: Long): AsociacionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(asociaciones: List<AsociacionEntity>)

    @Query("""
        SELECT * FROM asociaciones
        WHERE intercalable = 1
          AND (cultivoId1 = :cultivoId OR cultivoId2 = :cultivoId)
    """)
    suspend fun getIntercalables(cultivoId: Long): List<AsociacionEntity>

    @Query("SELECT COUNT(*) FROM asociaciones")
    suspend fun count(): Int
}
