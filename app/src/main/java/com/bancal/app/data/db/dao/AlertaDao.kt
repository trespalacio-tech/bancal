package com.bancal.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.bancal.app.data.db.entity.AlertaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertaDao {

    @Query("SELECT * FROM alertas WHERE resuelta = 0 ORDER BY fecha DESC")
    fun getPendientes(): Flow<List<AlertaEntity>>

    @Query("SELECT * FROM alertas ORDER BY fecha DESC")
    fun getAll(): Flow<List<AlertaEntity>>

    @Query("SELECT COUNT(*) FROM alertas WHERE resuelta = 0")
    fun countPendientes(): Flow<Int>

    @Insert
    suspend fun insert(alerta: AlertaEntity): Long

    @Query("UPDATE alertas SET resuelta = 1 WHERE id = :id")
    suspend fun resolver(id: Long)

    @Query("DELETE FROM alertas WHERE resuelta = 1")
    suspend fun limpiarResueltas()

    @Query("""
        SELECT COUNT(*) FROM alertas
        WHERE tipo = :tipo AND plantacionId = :plantacionId AND resuelta = 0
    """)
    suspend fun existeAlerta(tipo: String, plantacionId: Long): Int

    @Query("SELECT tipo, plantacionId FROM alertas WHERE resuelta = 0")
    suspend fun getPendientesKeys(): List<AlertaKey>
}

data class AlertaKey(val tipo: String, val plantacionId: Long?)
