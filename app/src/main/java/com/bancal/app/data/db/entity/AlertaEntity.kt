package com.bancal.app.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.bancal.app.domain.model.TipoAlerta

@Entity(
    tableName = "alertas",
    foreignKeys = [
        ForeignKey(
            entity = PlantacionEntity::class,
            parentColumns = ["id"],
            childColumns = ["plantacionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("plantacionId")]
)
data class AlertaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tipo: TipoAlerta,
    val titulo: String,
    val mensaje: String,
    val fecha: Long, // epoch millis
    val resuelta: Boolean = false,
    val plantacionId: Long? = null
)
