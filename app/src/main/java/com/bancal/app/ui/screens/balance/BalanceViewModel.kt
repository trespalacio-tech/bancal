package com.bancal.app.ui.screens.balance

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bancal.app.data.db.AppDatabase
import com.bancal.app.data.db.entity.CultivoEntity
import com.bancal.app.data.preferences.BancalPreferences
import com.bancal.app.data.repository.BancalRepository
import com.bancal.app.domain.model.CategoriaBiointensiva
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn

data class BalanceData(
    val cmCarbono: Int = 0,
    val cmCalorico: Int = 0,
    val cmVegetal: Int = 0,
    val cmLibre: Int = 0,
    val bancalTotalCm: Int = 1000,
    val cultivosCarbono: List<CultivoOcupacion> = emptyList(),
    val cultivosCalorico: List<CultivoOcupacion> = emptyList(),
    val cultivosVegetal: List<CultivoOcupacion> = emptyList()
) {
    val cmOcupado get() = cmCarbono + cmCalorico + cmVegetal

    val pctCarbono get() = if (cmOcupado > 0) cmCarbono * 100f / cmOcupado else 0f
    val pctCalorico get() = if (cmOcupado > 0) cmCalorico * 100f / cmOcupado else 0f
    val pctVegetal get() = if (cmOcupado > 0) cmVegetal * 100f / cmOcupado else 0f
}

data class CultivoOcupacion(
    val cultivo: CultivoEntity,
    val cmOcupados: Int
)

class BalanceViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = BancalRepository(
        db.cultivoDao(), db.plantacionDao(), db.tratamientoDao(),
        db.asociacionDao(), db.alertaDao(), db.diarioDao(), db.bancalDao()
    )

    private val bancalId = BancalPreferences.getSelectedId(application)

    val balance: StateFlow<BalanceData> =
        combine(
            repository.getPlantacionesActivas(bancalId),
            repository.getCultivos(),
            repository.getBancal(bancalId).filterNotNull()
        ) { plantaciones, cultivos, bancal ->
            val cultivoMap = cultivos.associateBy { it.id }
            var cmCarbono = 0
            var cmCalorico = 0
            var cmVegetal = 0

            val agrupado = mutableMapOf<Long, Int>()

            for (p in plantaciones) {
                val cultivo = cultivoMap[p.cultivoId] ?: continue
                agrupado[cultivo.id] = (agrupado[cultivo.id] ?: 0) + p.anchoCm
                when (cultivo.categoria) {
                    CategoriaBiointensiva.CARBONO -> cmCarbono += p.anchoCm
                    CategoriaBiointensiva.CALORICO -> cmCalorico += p.anchoCm
                    CategoriaBiointensiva.VEGETAL -> cmVegetal += p.anchoCm
                }
            }

            fun cultivosDeCategoria(cat: CategoriaBiointensiva): List<CultivoOcupacion> =
                agrupado.entries
                    .filter { (id, _) -> cultivoMap[id]?.categoria == cat }
                    .map { (id, cm) -> CultivoOcupacion(cultivoMap[id]!!, cm) }
                    .sortedByDescending { it.cmOcupados }

            BalanceData(
                cmCarbono = cmCarbono,
                cmCalorico = cmCalorico,
                cmVegetal = cmVegetal,
                cmLibre = bancal.largoCm - cmCarbono - cmCalorico - cmVegetal,
                bancalTotalCm = bancal.largoCm,
                cultivosCarbono = cultivosDeCategoria(CategoriaBiointensiva.CARBONO),
                cultivosCalorico = cultivosDeCategoria(CategoriaBiointensiva.CALORICO),
                cultivosVegetal = cultivosDeCategoria(CategoriaBiointensiva.VEGETAL)
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BalanceData())
}
