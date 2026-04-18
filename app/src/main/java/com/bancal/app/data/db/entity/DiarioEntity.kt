package com.bancal.app.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "diario",
    indices = [Index(value = ["fecha"], unique = true)]
)
data class DiarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fecha: Long, // epoch millis, inicio del día
    val tempMin: Int? = null, // °C
    val tempMax: Int? = null, // °C
    val lluviaMm: Float? = null, // milímetros
    val helada: Boolean = false,
    val tareas: String = "",
    val observaciones: String = "",
    val cosechaKg: Float? = null,
    val cosechaNotas: String = "",
    val fotoPath: String? = null
)
