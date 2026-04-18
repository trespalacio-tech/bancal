package com.bancal.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bancal.app.data.db.entity.CultivoEntity
import com.bancal.app.domain.model.FamiliaCultivo
import kotlinx.coroutines.flow.Flow

@Dao
interface CultivoDao {

    @Query("SELECT * FROM cultivos ORDER BY nombre")
    fun getAll(): Flow<List<CultivoEntity>>

    @Query("SELECT * FROM cultivos WHERE id = :id")
    suspend fun getById(id: Long): CultivoEntity?

    @Query("SELECT * FROM cultivos WHERE familia = :familia ORDER BY nombre")
    fun getByFamilia(familia: FamiliaCultivo): Flow<List<CultivoEntity>>

    @Query("SELECT * FROM cultivos WHERE nombre LIKE '%' || :query || '%' ORDER BY nombre")
    fun search(query: String): Flow<List<CultivoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cultivos: List<CultivoEntity>)

    @Insert
    suspend fun insert(cultivo: CultivoEntity): Long

    @Query("DELETE FROM cultivos WHERE id = :id AND esPersonalizado = 1")
    suspend fun deletePersonalizado(id: Long)

    @Query("SELECT COUNT(*) FROM cultivos")
    suspend fun count(): Int
}
