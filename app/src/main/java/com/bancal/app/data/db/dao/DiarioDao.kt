package com.bancal.app.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.bancal.app.data.db.entity.DiarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DiarioDao {

    @Query("SELECT * FROM diario ORDER BY fecha DESC")
    abstract fun getAll(): Flow<List<DiarioEntity>>

    @Query("SELECT * FROM diario ORDER BY fecha DESC LIMIT :limit")
    abstract fun getRecientes(limit: Int): Flow<List<DiarioEntity>>

    @Query("SELECT * FROM diario WHERE fecha = :fechaEpoch LIMIT 1")
    abstract suspend fun getByFecha(fechaEpoch: Long): DiarioEntity?

    @Query("SELECT * FROM diario WHERE id = :id")
    abstract suspend fun getById(id: Long): DiarioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(diario: DiarioEntity): Long

    @Update
    abstract suspend fun update(diario: DiarioEntity)

    @Delete
    abstract suspend fun delete(diario: DiarioEntity)

    @Query("SELECT COUNT(*) FROM diario WHERE helada = 1")
    abstract fun countDiasHelada(): Flow<Int>

    @Query("SELECT SUM(cosechaKg) FROM diario WHERE cosechaKg IS NOT NULL")
    abstract fun totalCosechaKg(): Flow<Float?>

    @Query("SELECT SUM(lluviaMm) FROM diario WHERE lluviaMm IS NOT NULL")
    abstract fun totalLluviaMm(): Flow<Float?>

    @Query("SELECT COUNT(*) FROM diario")
    abstract fun countEntradas(): Flow<Int>

    /** Acumula cosecha en la entrada del día (la crea si no existe) en una sola transacción. */
    @Transaction
    open suspend fun acumularCosecha(fechaEpoch: Long, kg: Float, nota: String) {
        val existente = getByFecha(fechaEpoch)
        if (existente != null) {
            val notas = when {
                existente.cosechaNotas.isBlank() -> nota
                existente.cosechaNotas.contains(nota) -> existente.cosechaNotas
                else -> "${existente.cosechaNotas}, $nota"
            }
            update(
                existente.copy(
                    cosechaKg = (existente.cosechaKg ?: 0f) + kg,
                    cosechaNotas = notas
                )
            )
        } else {
            insert(DiarioEntity(fecha = fechaEpoch, cosechaKg = kg, cosechaNotas = nota))
        }
    }
}
