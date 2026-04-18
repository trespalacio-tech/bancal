package com.bancal.app.ui.screens.sucesion

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bancal.app.data.db.AppDatabase
import com.bancal.app.data.db.entity.CultivoEntity
import com.bancal.app.data.db.entity.PlantacionEntity
import com.bancal.app.data.preferences.BancalPreferences
import com.bancal.app.data.repository.BancalRepository
import com.bancal.app.domain.logic.SucesionEngine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class SucesionUiState(
    val escalonadas: List<SucesionEngine.SiembraEscalonada> = emptyList(),
    val relevos: List<Pair<PlantacionEntity, List<SucesionEngine.SugerenciaRelevo>>> = emptyList(),
    val cultivoMap: Map<Long, CultivoEntity> = emptyMap()
)

class SucesionViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = BancalRepository(
        db.cultivoDao(), db.plantacionDao(), db.tratamientoDao(),
        db.asociacionDao(), db.alertaDao(), db.diarioDao(), db.bancalDao()
    )

    private val bancalId = BancalPreferences.getSelectedId(application)

    val uiState: StateFlow<SucesionUiState> =
        combine(
            repository.getPlantacionesActivas(bancalId),
            repository.getCultivos()
        ) { plantaciones, cultivos ->
            val cultivoMap = cultivos.associateBy { it.id }

            val escalonadas = SucesionEngine.calcularSiembrasEscalonadas(plantaciones, cultivoMap)
            val relevos = SucesionEngine.sugerirRelevos(plantaciones, cultivoMap, cultivos)

            SucesionUiState(
                escalonadas = escalonadas,
                relevos = relevos,
                cultivoMap = cultivoMap
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SucesionUiState())
}
