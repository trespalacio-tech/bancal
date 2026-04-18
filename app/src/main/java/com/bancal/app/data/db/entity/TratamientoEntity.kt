package com.bancal.app.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.bancal.app.domain.model.TipoTratamiento

@Entity(
    tableName = "tratamientos",
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
data class TratamientoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantacionId: Long?, // null = tratamiento general al bancal
    val fecha: Long, // epoch millis
    val tipo: TipoTratamiento,
    val producto: String,
    val dosis: String = "",
    val notas: String = ""
)
