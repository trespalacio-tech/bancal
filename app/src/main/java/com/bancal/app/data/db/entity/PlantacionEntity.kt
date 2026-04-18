package com.bancal.app.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.bancal.app.domain.model.EstadoPlantacion
import com.bancal.app.domain.model.TipoSiembra

@Entity(
    tableName = "plantaciones",
    foreignKeys = [
        ForeignKey(
            entity = CultivoEntity::class,
            parentColumns = ["id"],
            childColumns = ["cultivoId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = BancalEntity::class,
            parentColumns = ["id"],
            childColumns = ["bancalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cultivoId"), Index("bancalId")]
)
data class PlantacionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cultivoId: Long,
    val bancalId: Long = 1, // FK a bancales — default al bancal inicial
    val fechaSiembra: Long, // epoch millis
    val fechaTrasplanteEstimada: Long? = null,
    val fechaCosechaEstimada: Long,
    val posicionXCm: Int, // posición desde el inicio del bancal
    val anchoCm: Int, // espacio que ocupa
    val tipoSiembra: TipoSiembra,
    val estado: EstadoPlantacion,
    val notas: String = "",
    val intercaladaCon: Long? = null // id de la plantación "madre" si es intercalada
)
