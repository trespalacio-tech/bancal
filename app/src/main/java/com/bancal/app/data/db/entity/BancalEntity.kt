package com.bancal.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bancales")
data class BancalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String = "Mi Bancal",
    val largoCm: Int = 1000,
    val anchoCm: Int = 75,
    val tarpingDesde: Long? = null, // epoch millis, null = sin tarping activo
    val abonoVerdeDesde: Long? = null, // epoch millis cuando se sembró el abono verde
    val abonoVerdeTipo: String? = null  // tipo: veza, trébol, centeno, mostaza, etc.
)
