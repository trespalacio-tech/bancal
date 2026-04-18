package com.bancal.app.ui.screens.cultivopersonalizado

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bancal.app.data.db.AppDatabase
import com.bancal.app.data.db.entity.CultivoEntity
import com.bancal.app.data.repository.BancalRepository
import com.bancal.app.domain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CultivoPersonalizadoViewModel(app: Application) : AndroidViewModel(app) {

    private val db = AppDatabase.getInstance(app)
    private val repository = BancalRepository(
        cultivoDao = db.cultivoDao(),
        plantacionDao = db.plantacionDao(),
        tratamientoDao = db.tratamientoDao(),
        asociacionDao = db.asociacionDao(),
        alertaDao = db.alertaDao()
    )

    // Campos del formulario
    val nombre = MutableStateFlow("")
    val icono = MutableStateFlow("")
    val familia = MutableStateFlow(FamiliaCultivo.SOLANACEAS)
    val marcoCm = MutableStateFlow("30")
    val diasCosecha = MutableStateFlow("90")
    val diasGerminacion = MutableStateFlow("7")
    val temperaturaMinima = MutableStateFlow("5")
    val temperaturaOptima = MutableStateFlow("20")
    val profundidadSiembraCm = MutableStateFlow("1.0")
    val riego = MutableStateFlow(NivelRiego.MEDIO)
    val categoria = MutableStateFlow(CategoriaBiointensiva.VEGETAL)
    val exigencia = MutableStateFlow(ExigenciaNutricional.POCO_EXIGENTE)
    val lineasPorBancal = MutableStateFlow("2")
    val semanasCosechando = MutableStateFlow("4")
    val admiteSiembraDirecta = MutableStateFlow(false)
    val admitePlantel = MutableStateFlow(true)
    val notas = MutableStateFlow("")

    // Meses de siembra (bitfields)
    val mesesDirecta = MutableStateFlow(0)
    val mesesSemillero = MutableStateFlow(0)
    val mesesTrasplante = MutableStateFlow(0)

    private val _guardado = MutableStateFlow(false)
    val guardado: StateFlow<Boolean> = _guardado

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun toggleMesDirecta(mes: Int) {
        mesesDirecta.value = mesesDirecta.value xor (1 shl mes)
    }

    fun toggleMesSemillero(mes: Int) {
        mesesSemillero.value = mesesSemillero.value xor (1 shl mes)
    }

    fun toggleMesTrasplante(mes: Int) {
        mesesTrasplante.value = mesesTrasplante.value xor (1 shl mes)
    }

    fun guardar() {
        val nombreVal = nombre.value.trim()
        if (nombreVal.isBlank()) {
            _error.value = "El nombre es obligatorio"
            return
        }
        val iconoVal = icono.value.trim().ifBlank { "\uD83C\uDF3F" } // default: herb emoji
        val marcoVal = marcoCm.value.toIntOrNull()
        if (marcoVal == null || marcoVal < 5) {
            _error.value = "Marco de plantacion invalido (minimo 5 cm)"
            return
        }
        val diasCosechaVal = diasCosecha.value.toIntOrNull()
        if (diasCosechaVal == null || diasCosechaVal < 1) {
            _error.value = "Dias a cosecha invalido"
            return
        }

        viewModelScope.launch {
            val cultivo = CultivoEntity(
                nombre = nombreVal,
                familia = familia.value,
                icono = iconoVal,
                marcoCm = marcoVal,
                diasGerminacion = diasGerminacion.value.toIntOrNull() ?: 7,
                diasCosecha = diasCosechaVal,
                temperaturaMinima = temperaturaMinima.value.toIntOrNull() ?: 5,
                temperaturaOptima = temperaturaOptima.value.toIntOrNull() ?: 20,
                mesesSiembraDirecta = mesesDirecta.value,
                mesesSemillero = mesesSemillero.value,
                mesesTrasplante = mesesTrasplante.value,
                profundidadSiembraCm = profundidadSiembraCm.value.toFloatOrNull() ?: 1f,
                riego = riego.value,
                categoria = categoria.value,
                lineasPorBancal = lineasPorBancal.value.toIntOrNull() ?: 2,
                semanasCosechando = semanasCosechando.value.toIntOrNull() ?: 4,
                exigencia = exigencia.value,
                admiteSiembraDirecta = admiteSiembraDirecta.value,
                admitePlantel = admitePlantel.value,
                esPersonalizado = true,
                notas = notas.value.trim()
            )
            repository.insertCultivo(cultivo)
            _guardado.value = true
        }
    }

    fun resetGuardado() {
        _guardado.value = false
    }

    fun clearError() {
        _error.value = null
    }
}
