package com.bancal.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bancal.app.domain.model.CategoriaBiointensiva
import com.bancal.app.domain.model.ExigenciaNutricional
import com.bancal.app.domain.model.FamiliaCultivo
import com.bancal.app.domain.model.NivelRiego

@Entity(tableName = "cultivos")
data class CultivoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val familia: FamiliaCultivo,
    val icono: String, // emoji
    val marcoCm: Int, // distancia entre plantas en cm
    val diasGerminacion: Int,
    val diasCosecha: Int, // desde siembra/trasplante hasta cosecha
    val temperaturaMinima: Int, // °C
    val temperaturaOptima: Int, // °C
    val mesesSiembraDirecta: Int, // bitfield: bit 0 = enero, bit 11 = diciembre
    val mesesSemillero: Int, // bitfield
    val mesesTrasplante: Int, // bitfield
    val profundidadSiembraCm: Float,
    val riego: NivelRiego,
    val categoria: CategoriaBiointensiva = CategoriaBiointensiva.VEGETAL,
    val intervaloSucesionDias: Int = 0, // 0 = no soporta siembra escalonada
    val lineasPorBancal: Int = 2, // líneas de cultivo en bancal de 75cm
    val semanasCosechando: Int = 4, // duración del periodo de cosecha en semanas
    val exigencia: ExigenciaNutricional = ExigenciaNutricional.POCO_EXIGENTE,
    val admiteSiembraDirecta: Boolean = false,
    val admitePlantel: Boolean = true,
    val esPersonalizado: Boolean = false,
    val notas: String = ""
)
