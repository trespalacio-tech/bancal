package com.bancal.app.ui.screens.tratamientos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bancal.app.data.db.AppDatabase
import com.bancal.app.data.db.entity.CultivoEntity
import com.bancal.app.data.db.entity.PlantacionEntity
import com.bancal.app.data.db.entity.TratamientoEntity
import com.bancal.app.data.preferences.BancalPreferences
import com.bancal.app.data.repository.BancalRepository
import com.bancal.app.domain.model.TipoTratamiento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TratamientoConCultivo(
    val tratamiento: TratamientoEntity,
    val plantacion: PlantacionEntity?,
    val cultivo: CultivoEntity?
)

class TratamientosViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = BancalRepository(
        db.cultivoDao(), db.plantacionDao(), db.tratamientoDao(),
        db.asociacionDao(), db.alertaDao(), db.diarioDao(), db.bancalDao()
    )

    private val bancalId = BancalPreferences.getSelectedId(application)

    val tratamientos: StateFlow<List<TratamientoConCultivo>> =
        combine(
            repository.getTratamientos(),
            repository.getTodasPlantaciones(),
            repository.getCultivos()
        ) { lista, plantaciones, cultivos ->
            val pMap = plantaciones.associateBy { it.id }
            val cMap = cultivos.associateBy { it.id }
            lista.map { t ->
                val plantacion = t.plantacionId?.let { pMap[it] }
                val cultivo = plantacion?.let { cMap[it.cultivoId] }
                TratamientoConCultivo(t, plantacion, cultivo)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val plantacionesActivas: StateFlow<List<PlantacionEntity>> =
        repository.getPlantacionesActivas(bancalId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog

    fun showAddDialog() { _showDialog.value = true }
    fun hideDialog() { _showDialog.value = false }

    fun addTratamiento(
        plantacionId: Long?,
        tipo: TipoTratamiento,
        producto: String,
        dosis: String,
        notas: String
    ) {
        viewModelScope.launch {
            repository.insertTratamiento(
                TratamientoEntity(
                    plantacionId = plantacionId,
                    fecha = System.currentTimeMillis(),
                    tipo = tipo,
                    producto = producto,
                    dosis = dosis,
                    notas = notas
                )
            )
            _showDialog.value = false
        }
    }

    fun deleteTratamiento(tratamiento: TratamientoEntity) {
        viewModelScope.launch {
            repository.deleteTratamiento(tratamiento)
        }
    }
}
