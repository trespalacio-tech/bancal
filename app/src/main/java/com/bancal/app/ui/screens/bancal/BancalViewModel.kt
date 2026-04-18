package com.bancal.app.ui.screens.bancal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bancal.app.data.db.AppDatabase
import com.bancal.app.data.db.entity.BancalEntity
import com.bancal.app.data.db.entity.PlantacionEntity
import com.bancal.app.data.preferences.BancalPreferences
import com.bancal.app.data.repository.BancalRepository
import com.bancal.app.ui.components.PlantacionVisual
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class BancalViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = BancalRepository(
        db.cultivoDao(), db.plantacionDao(), db.tratamientoDao(),
        db.asociacionDao(), db.alertaDao(), db.diarioDao(), db.bancalDao()
    )

    // --- Selección de bancal ---
    private val _selectedBancalId = MutableStateFlow(
        BancalPreferences.getSelectedId(application)
    )

    val bancales: StateFlow<List<BancalEntity>> = repository.getAllBancales()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * El bancal actualmente seleccionado. Reactivo: si el bancal se elimina,
     * cae al primero disponible. Si la lista está vacía, devuelve un BancalEntity por defecto.
     */
    val bancal: StateFlow<BancalEntity> = combine(
        _selectedBancalId, bancales
    ) { selectedId, lista ->
        lista.find { it.id == selectedId }
            ?: lista.firstOrNull()
            ?: BancalEntity()
    }.onEach { b ->
        // Si el bancal seleccionado desaparece, corregir la selección.
        // Side-effect sobre el flow: solo corre cuando hay suscriptores.
        val currentSelected = _selectedBancalId.value
        if (b.id != 0L && b.id != currentSelected) {
            _selectedBancalId.value = b.id
            BancalPreferences.setSelectedId(getApplication(), b.id)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BancalEntity())

    /**
     * Re-lee el ID seleccionado de SharedPreferences.
     * Llamar al volver de GestionBancalesScreen, donde otro ViewModel pudo cambiar la selección.
     */
    fun syncSelection() {
        val prefsId = BancalPreferences.getSelectedId(getApplication())
        if (prefsId != _selectedBancalId.value) {
            _selectedBancalId.value = prefsId
        }
    }

    // --- Plantaciones del bancal seleccionado ---
    val plantacionesVisuales: StateFlow<List<PlantacionVisual>> =
        _selectedBancalId.flatMapLatest { bancalId ->
            combine(
                repository.getPlantacionesActivas(bancalId),
                repository.getCultivos()
            ) { plantaciones, cultivos ->
                val cultivoMap = cultivos.associateBy { it.id }
                plantaciones.mapNotNull { p ->
                    cultivoMap[p.cultivoId]?.let { c -> PlantacionVisual(p, c) }
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Alertas (globales, no por bancal) ---
    val alertasPendientes: StateFlow<Int> = repository.countAlertasPendientes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun seleccionarBancal(id: Long) {
        _selectedBancalId.value = id
        BancalPreferences.setSelectedId(getApplication(), id)
    }

    fun deletePlantacion(plantacion: PlantacionEntity) {
        viewModelScope.launch {
            repository.deletePlantacion(plantacion)
        }
    }

    fun toggleTarping() {
        viewModelScope.launch {
            val current = bancal.value
            if (current.id == 0L) return@launch
            val updated = if (current.tarpingDesde != null) {
                current.copy(tarpingDesde = null)
            } else {
                current.copy(tarpingDesde = System.currentTimeMillis())
            }
            repository.updateBancal(updated)
        }
    }

    fun toggleAbonoVerde(tipo: String? = null) {
        viewModelScope.launch {
            val current = bancal.value
            if (current.id == 0L) return@launch
            val updated = if (current.abonoVerdeDesde != null) {
                current.copy(abonoVerdeDesde = null, abonoVerdeTipo = null)
            } else {
                current.copy(
                    abonoVerdeDesde = System.currentTimeMillis(),
                    abonoVerdeTipo = tipo ?: "Veza"
                )
            }
            repository.updateBancal(updated)
        }
    }
}
