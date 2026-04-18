package com.bancal.app.ui.screens.detalle

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bancal.app.data.db.AppDatabase
import com.bancal.app.data.db.entity.CultivoEntity
import com.bancal.app.data.db.entity.PlantacionEntity
import com.bancal.app.data.db.entity.TratamientoEntity
import com.bancal.app.data.repository.BancalRepository
import com.bancal.app.domain.model.EstadoPlantacion
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class DetalleViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = BancalRepository(
        db.cultivoDao(), db.plantacionDao(), db.tratamientoDao(),
        db.asociacionDao(), db.alertaDao(), db.diarioDao(), db.bancalDao()
    )

    private val _plantacion = MutableStateFlow<PlantacionEntity?>(null)
    val plantacion: StateFlow<PlantacionEntity?> = _plantacion

    private val _cultivo = MutableStateFlow<CultivoEntity?>(null)
    val cultivo: StateFlow<CultivoEntity?> = _cultivo

    private val _tratamientos = MutableStateFlow<List<TratamientoEntity>>(emptyList())
    val tratamientos: StateFlow<List<TratamientoEntity>> = _tratamientos

    // Compañero intercalado (si existe)
    private val _companero = MutableStateFlow<CultivoEntity?>(null)
    val companero: StateFlow<CultivoEntity?> = _companero

    private val _companeroPlantacion = MutableStateFlow<PlantacionEntity?>(null)
    val companeroPlantacion: StateFlow<PlantacionEntity?> = _companeroPlantacion

    private val _deleted = MutableStateFlow(false)
    val deleted: StateFlow<Boolean> = _deleted

    private val _cosechaRegistrada = MutableStateFlow(false)
    val cosechaRegistrada: StateFlow<Boolean> = _cosechaRegistrada

    private var tratamientosJob: Job? = null

    fun loadPlantacion(id: Long) {
        viewModelScope.launch {
            val p = repository.getPlantacion(id) ?: return@launch
            _plantacion.value = p
            _cultivo.value = repository.getCultivo(p.cultivoId)

            // Cargar compañero intercalado
            if (p.intercaladaCon != null) {
                // Esta es la hija: mostrar la madre como compañera
                val madre = repository.getPlantacion(p.intercaladaCon)
                _companeroPlantacion.value = madre
                _companero.value = madre?.let { repository.getCultivo(it.cultivoId) }
            } else {
                // Esta podría ser la madre: buscar hija
                val hijas = repository.getIntercalados(p.id)
                val hija = hijas.firstOrNull()
                _companeroPlantacion.value = hija
                _companero.value = hija?.let { repository.getCultivo(it.cultivoId) }
            }
        }

        tratamientosJob?.cancel()
        tratamientosJob = viewModelScope.launch {
            repository.getTratamientosPlantacion(id).collect {
                _tratamientos.value = it
            }
        }
    }

    fun updateEstado(estado: EstadoPlantacion) {
        val p = _plantacion.value ?: return
        viewModelScope.launch {
            repository.updateEstadoPlantacion(p.id, estado)
            _plantacion.value = p.copy(estado = estado)
        }
    }

    /**
     * Registra una cosecha en el cuaderno de campo (acumula si ya hay entrada de hoy).
     * Marca la plantación como COSECHANDO si aún no lo está.
     */
    fun registrarCosecha(kg: Float) {
        val p = _plantacion.value ?: return
        val c = _cultivo.value ?: return
        if (kg <= 0f) return
        viewModelScope.launch {
            val zone = ZoneId.of("Europe/Madrid")
            val hoyEpoch = LocalDate.now().atStartOfDay(zone).toInstant().toEpochMilli()
            repository.acumularCosecha(hoyEpoch, kg, "${c.icono} ${c.nombre}")

            if (p.estado != EstadoPlantacion.COSECHANDO) {
                repository.updateEstadoPlantacion(p.id, EstadoPlantacion.COSECHANDO)
                _plantacion.value = p.copy(estado = EstadoPlantacion.COSECHANDO)
            }
            _cosechaRegistrada.value = true
        }
    }

    fun resetCosechaRegistrada() { _cosechaRegistrada.value = false }

    fun deletePlantacion() {
        val p = _plantacion.value ?: return
        viewModelScope.launch {
            repository.deletePlantacion(p)
            _deleted.value = true
        }
    }
}
