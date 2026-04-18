package com.bancal.app.ui.screens.gestion

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bancal.app.data.db.AppDatabase
import com.bancal.app.data.db.entity.BancalEntity
import com.bancal.app.data.preferences.BancalPreferences
import com.bancal.app.data.repository.BancalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BancalConInfo(
    val bancal: BancalEntity,
    val plantacionesActivas: Int
)

class GestionBancalesViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = BancalRepository(
        db.cultivoDao(), db.plantacionDao(), db.tratamientoDao(),
        db.asociacionDao(), db.alertaDao(), db.diarioDao(), db.bancalDao()
    )

    val bancalesConInfo: StateFlow<List<BancalConInfo>> = combine(
        repository.getAllBancales(),
        repository.getTodasPlantacionesActivas()
    ) { bancales, plantaciones ->
        val countMap = plantaciones.groupBy { it.bancalId }.mapValues { it.value.size }
        bancales.map { b -> BancalConInfo(b, countMap[b.id] ?: 0) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedId = MutableStateFlow(
        BancalPreferences.getSelectedId(application)
    )
    val selectedId: StateFlow<Long> = _selectedId

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun clearError() { _error.value = null }

    fun crearBancal(nombre: String, largoCm: Int, anchoCm: Int) {
        if (nombre.isBlank()) {
            _error.value = "El nombre no puede estar vacío"
            return
        }
        if (largoCm < 50) {
            _error.value = "El largo mínimo es 50cm"
            return
        }
        if (anchoCm < 20) {
            _error.value = "El ancho mínimo es 20cm"
            return
        }
        viewModelScope.launch {
            val id = repository.insertBancal(
                BancalEntity(nombre = nombre, largoCm = largoCm, anchoCm = anchoCm)
            )
            // Auto-seleccionar el nuevo bancal
            seleccionar(id)
        }
    }

    fun editarBancal(bancal: BancalEntity, nombre: String, largoCm: Int, anchoCm: Int) {
        if (nombre.isBlank()) {
            _error.value = "El nombre no puede estar vacío"
            return
        }
        if (largoCm < 50) {
            _error.value = "El largo mínimo es 50cm"
            return
        }
        if (anchoCm < 20) {
            _error.value = "El ancho mínimo es 20cm"
            return
        }
        viewModelScope.launch {
            // Validar que no haya plantaciones fuera del nuevo largo
            val maxOcupado = repository.getMaxOcupado(bancal.id)
            if (largoCm < maxOcupado) {
                _error.value = "No puedes reducir a ${largoCm}cm: hay plantaciones hasta ${maxOcupado}cm"
                return@launch
            }
            repository.updateBancal(
                bancal.copy(nombre = nombre, largoCm = largoCm, anchoCm = anchoCm)
            )
        }
    }

    fun eliminarBancal(bancal: BancalEntity) {
        viewModelScope.launch {
            val lista = bancalesConInfo.value
            if (lista.size <= 1) {
                _error.value = "No puedes eliminar el único bancal"
                return@launch
            }
            repository.deleteBancal(bancal)
            // Si se eliminó el seleccionado, seleccionar otro
            if (_selectedId.value == bancal.id) {
                val otro = lista.first { it.bancal.id != bancal.id }
                seleccionar(otro.bancal.id)
            }
        }
    }

    fun seleccionar(id: Long) {
        _selectedId.value = id
        BancalPreferences.setSelectedId(getApplication(), id)
    }
}
