package com.bancal.app.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.bancal.app.domain.model.TipoAsociacion

@Entity(
    tableName = "asociaciones",
    primaryKeys = ["cultivoId1", "cultivoId2"],
    foreignKeys = [
        ForeignKey(
            entity = CultivoEntity::class,
            parentColumns = ["id"],
            childColumns = ["cultivoId1"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CultivoEntity::class,
            parentColumns = ["id"],
            childColumns = ["cultivoId2"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cultivoId1"), Index("cultivoId2")]
)
data class AsociacionEntity(
    val cultivoId1: Long,
    val cultivoId2: Long,
    val tipo: TipoAsociacion,
    val motivo: String,
    val intercalable: Boolean = false
)
