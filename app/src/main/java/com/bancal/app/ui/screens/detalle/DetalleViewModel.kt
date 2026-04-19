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

    // Compañeros intercalados (madre + hermanas si es hija; todas las hijas si es madre)
    data class Companero(
        val plantacion: PlantacionEntity,
        val cultivo: CultivoEntity,
        val rol: Rol
    ) {
        enum class Rol { MADRE, HIJA, HERMANA }
    }
    private val _companeros = MutableStateFlow<List<Companero>>(emptyList())
    val companeros: StateFlow<List<Companero>> = _companeros

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

            val lista = mutableListOf<Companero>()
            if (p.intercaladaCon != null) {
                // Hija: madre + hermanas (otras hijas de la misma madre)
                val madre = repository.getPlantacion(p.intercaladaCon)
                if (madre != null) {
                    repository.getCultivo(madre.cultivoId)?.let {
                        lista += Companero(madre, it, Companero.Rol.MADRE)
                    }
                    val hermanas = repository.getIntercalados(madre.id)
                        .filter { it.id != p.id }
                        .sortedBy { it.posicionXCm }
                    for (h in hermanas) {
                        repository.getCultivo(h.cultivoId)?.let {
                            lista += Companero(h, it, Companero.Rol.HERMANA)
                        }
                    }
                }
            } else {
                // Madre: todas las hijas
                val hijas = repository.getIntercalados(p.id).sortedBy { it.posicionXCm }
                for (h in hijas) {
                    repository.getCultivo(h.cultivoId)?.let {
                        lista += Companero(h, it, Companero.Rol.HIJA)
                    }
                }
            }
            _companeros.value = lista
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
