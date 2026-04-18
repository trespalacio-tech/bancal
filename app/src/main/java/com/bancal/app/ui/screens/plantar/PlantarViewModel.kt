package com.bancal.app.ui.screens.plantar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bancal.app.data.db.AppDatabase
import com.bancal.app.data.db.entity.BancalEntity
import com.bancal.app.data.db.entity.CultivoEntity
import com.bancal.app.data.preferences.BancalPreferences
import com.bancal.app.data.db.entity.PlantacionEntity
import com.bancal.app.data.repository.BancalRepository
import com.bancal.app.domain.logic.AsociacionEngine
import com.bancal.app.domain.logic.CalendarioEngine
import com.bancal.app.domain.model.EstadoPlantacion
import com.bancal.app.domain.model.ExigenciaNutricional
import com.bancal.app.domain.model.TipoSiembra
import com.bancal.app.ui.components.PlantacionVisual
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

data class Hueco(val startCm: Int, val anchoCm: Int)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class PlantarViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = BancalRepository(
        db.cultivoDao(), db.plantacionDao(), db.tratamientoDao(),
        db.asociacionDao(), db.alertaDao(), db.diarioDao(), db.bancalDao()
    )
    private val asociacionEngine = AsociacionEngine(repository)

    private val bancalId = BancalPreferences.getSelectedId(application)

    val bancal: StateFlow<BancalEntity> = repository.getBancal(bancalId)
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BancalEntity())

    private val bancalLargoCm: Int get() = bancal.value.largoCm

    // --- Búsqueda ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val cultivos: StateFlow<List<CultivoEntity>> = _searchQuery
        .debounce(200)
        .flatMapLatest { query ->
            if (query.isBlank()) repository.getCultivos()
            else repository.searchCultivos(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Cultivo seleccionado ---
    private val _selectedCultivo = MutableStateFlow<CultivoEntity?>(null)
    val selectedCultivo: StateFlow<CultivoEntity?> = _selectedCultivo

    // --- Cantidad ---
    private val _cantidad = MutableStateFlow(1)
    val cantidad: StateFlow<Int> = _cantidad

    val totalAnchoCm: StateFlow<Int> = combine(_selectedCultivo, _cantidad) { cultivo, cant ->
        (cultivo?.marcoCm ?: 0) * cant
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // --- Posición ---
    private val _posicionX = MutableStateFlow(0)
    val posicionX: StateFlow<Int> = _posicionX

    // --- Recomendación siembra ---
    private val _recomendacion = MutableStateFlow<CalendarioEngine.RecomendacionSiembra?>(null)
    val recomendacion: StateFlow<CalendarioEngine.RecomendacionSiembra?> = _recomendacion

    // --- Asociaciones ---
    private val _asociaciones = MutableStateFlow<List<AsociacionEngine.ResultadoAsociacion>>(emptyList())
    val asociaciones: StateFlow<List<AsociacionEngine.ResultadoAsociacion>> = _asociaciones

    // --- Zona ocupada ---
    private val _zonaOcupada = MutableStateFlow(false)
    val zonaOcupada: StateFlow<Boolean> = _zonaOcupada

    // --- Intercalado ---
    data class SugerenciaIntercalado(
        val plantacionMadre: PlantacionEntity,
        val cultivoMadre: CultivoEntity,
        val motivo: String
    )
    private val _intercalaciones = MutableStateFlow<List<SugerenciaIntercalado>>(emptyList())
    val intercalaciones: StateFlow<List<SugerenciaIntercalado>> = _intercalaciones

    private val _intercalarCon = MutableStateFlow<PlantacionEntity?>(null)
    val intercalarCon: StateFlow<PlantacionEntity?> = _intercalarCon

    // --- Aviso de rotación ---
    data class AvisoRotacion(val cultivoPrevio: String, val mensaje: String)
    private val _avisoRotacion = MutableStateFlow<AvisoRotacion?>(null)
    val avisoRotacion: StateFlow<AvisoRotacion?> = _avisoRotacion

    // --- Plantado ---
    private val _plantado = MutableStateFlow(false)
    val plantado: StateFlow<Boolean> = _plantado

    // --- Plantaciones activas para el canvas ---
    val plantacionesVisuales: StateFlow<List<PlantacionVisual>> =
        combine(
            repository.getPlantacionesActivas(bancalId),
            repository.getCultivos()
        ) { plantaciones, cultivos ->
            val map = cultivos.associateBy { it.id }
            plantaciones.mapNotNull { p -> map[p.cultivoId]?.let { PlantacionVisual(p, it) } }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Huecos disponibles ---
    val huecos: StateFlow<List<Hueco>> = repository.getPlantacionesActivas(bancalId)
        .combine(bancal) { plantaciones, b -> calcularHuecos(plantaciones, b.largoCm) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Cantidad máxima que cabe en el hueco actual ---
    val cantidadMaxima: StateFlow<Int> = combine(
        _selectedCultivo, _posicionX, huecos
    ) { cultivo, posX, listaHuecos ->
        if (cultivo == null) return@combine 1
        val marco = cultivo.marcoCm
        if (marco <= 0) return@combine 1
        val hueco = listaHuecos.find { posX >= it.startCm && posX < it.startCm + it.anchoCm }
        if (hueco != null) {
            val espacioDesde = hueco.startCm + hueco.anchoCm - posX
            (espacioDesde / marco).coerceAtLeast(1)
        } else {
            1
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)

    fun updateSearch(query: String) {
        _searchQuery.value = query
    }

    fun selectCultivo(cultivo: CultivoEntity) {
        _selectedCultivo.value = cultivo
        _cantidad.value = 1
        _recomendacion.value = CalendarioEngine.evaluarSiembra(cultivo)
        autoPosition()
    }

    fun clearSelection() {
        _selectedCultivo.value = null
        _recomendacion.value = null
        _asociaciones.value = emptyList()
        _zonaOcupada.value = false
        _intercalaciones.value = emptyList()
        _intercalarCon.value = null
        _avisoRotacion.value = null
        _cantidad.value = 1
    }

    fun setCantidad(cant: Int) {
        val max = cantidadMaxima.value
        _cantidad.value = cant.coerceIn(1, max.coerceAtLeast(1))
        evaluarPosicion()
    }

    fun incrementarCantidad() {
        setCantidad(_cantidad.value + 1)
    }

    fun decrementarCantidad() {
        setCantidad(_cantidad.value - 1)
    }

    fun setCantidadMaxima() {
        setCantidad(cantidadMaxima.value)
    }

    fun setPosicionInicial(posXCm: Int) {
        _posicionX.value = posXCm.coerceIn(0, bancalLargoCm)
        evaluarPosicion()
    }

    fun updatePosicion(posXCm: Int) {
        val ancho = totalAnchoCm.value
        _posicionX.value = posXCm.coerceIn(0, (bancalLargoCm - ancho).coerceAtLeast(0))
        val max = cantidadMaxima.value
        if (_cantidad.value > max) _cantidad.value = max.coerceAtLeast(1)
        evaluarPosicion()
    }

    fun ajustarPosicion(deltaCm: Int) {
        updatePosicion(_posicionX.value + deltaCm)
    }

    fun autoPosition() {
        val cultivo = _selectedCultivo.value ?: return
        val ancho = _cantidad.value * cultivo.marcoCm
        viewModelScope.launch {
            val sugerida = asociacionEngine.sugerirPosicion(cultivo.id, ancho, bancalLargoCm, bancalId)
            if (sugerida != null) {
                _posicionX.value = sugerida
            } else {
                val listaHuecos = huecos.value
                val hueco = listaHuecos.find { it.anchoCm >= ancho }
                if (hueco != null) {
                    _posicionX.value = hueco.startCm
                }
            }
            evaluarPosicion()
        }
    }

    fun seleccionarHueco(hueco: Hueco) {
        _posicionX.value = hueco.startCm
        val cultivo = _selectedCultivo.value ?: return
        val maxEnHueco = hueco.anchoCm / cultivo.marcoCm
        if (_cantidad.value > maxEnHueco) {
            _cantidad.value = maxEnHueco.coerceAtLeast(1)
        }
        evaluarPosicion()
    }

    fun seleccionarIntercalacion(plantacion: PlantacionEntity?) {
        _intercalarCon.value = plantacion
    }

    private fun evaluarPosicion() {
        val cultivo = _selectedCultivo.value ?: return
        viewModelScope.launch {
            val ancho = totalAnchoCm.value
            val posX = _posicionX.value
            _zonaOcupada.value = asociacionEngine.zonaOcupada(posX, ancho, bancalId)
            _asociaciones.value = asociacionEngine.evaluarAsociaciones(cultivo.id, posX, ancho, bancalId)

            // Evaluar rotación: avisar si cultivo MUY_EXIGENTE tras otro MUY_EXIGENTE
            if (cultivo.exigencia == ExigenciaNutricional.MUY_EXIGENTE) {
                val todas = repository.getTodasPlantacionesEnRango(posX, posX + ancho, bancalId)
                val previoExigente = todas.firstOrNull { p ->
                    p.estado == EstadoPlantacion.RETIRADO && run {
                        val c = repository.getCultivo(p.cultivoId)
                        c?.exigencia == ExigenciaNutricional.MUY_EXIGENTE
                    }
                }
                if (previoExigente != null) {
                    val nombre = repository.getCultivo(previoExigente.cultivoId)?.nombre ?: "?"
                    _avisoRotacion.value = AvisoRotacion(
                        nombre,
                        "Aquí hubo $nombre (muy exigente). Alterna con un cultivo poco exigente o leguminosa para recuperar el suelo."
                    )
                } else {
                    _avisoRotacion.value = null
                }
            } else {
                _avisoRotacion.value = null
            }

            // Detectar oportunidades de intercalado
            val intercalables = asociacionEngine.getIntercalablesEn(cultivo.id, posX, ancho, bancalId)
            _intercalaciones.value = intercalables.mapNotNull { p ->
                val cultivoMadre = repository.getCultivo(p.cultivoId) ?: return@mapNotNull null
                val asoc = repository.getAsociacion(cultivo.id, p.cultivoId)
                SugerenciaIntercalado(p, cultivoMadre, asoc?.motivo ?: "Cultivos intercalables")
            }
            // Si ya no hay intercalaciones válidas, limpiar selección
            if (_intercalarCon.value != null && intercalables.none { it.id == _intercalarCon.value?.id }) {
                _intercalarCon.value = null
            }
        }
    }

    fun plantar() {
        val cultivo = _selectedCultivo.value ?: return
        val recom = _recomendacion.value ?: return
        val posX = _posicionX.value
        val cant = _cantidad.value
        val anchoTotal = cant * cultivo.marcoCm

        viewModelScope.launch {
            val hoy = LocalDate.now()
            val zone = ZoneId.of("Europe/Madrid")
            val fechaSiembra = hoy.atStartOfDay(zone).toInstant().toEpochMilli()

            val tipoSiembra = recom.tipoSiembra
            val estado = when (tipoSiembra) {
                TipoSiembra.SEMILLERO -> EstadoPlantacion.SEMILLERO
                TipoSiembra.TRASPLANTE -> EstadoPlantacion.CRECIENDO
                TipoSiembra.DIRECTA -> EstadoPlantacion.CRECIENDO
            }

            val fechaTrasplante = if (tipoSiembra == TipoSiembra.SEMILLERO) {
                CalendarioEngine.fechaTrasplanteEstimada(hoy, cultivo)
                    .atStartOfDay(zone).toInstant().toEpochMilli()
            } else null

            val fechaCosecha = CalendarioEngine.fechaCosechaEstimada(hoy, cultivo)
                .atStartOfDay(zone).toInstant().toEpochMilli()

            val madre = _intercalarCon.value
            val plantacion = PlantacionEntity(
                cultivoId = cultivo.id,
                bancalId = bancalId,
                fechaSiembra = fechaSiembra,
                fechaTrasplanteEstimada = fechaTrasplante,
                fechaCosechaEstimada = fechaCosecha,
                posicionXCm = madre?.posicionXCm ?: posX,
                anchoCm = madre?.anchoCm ?: anchoTotal,
                tipoSiembra = tipoSiembra,
                estado = estado,
                intercaladaCon = madre?.id
            )

            repository.insertPlantacion(plantacion)
            _plantado.value = true
        }
    }

    fun resetPlantado() {
        _plantado.value = false
    }

    private fun calcularHuecos(plantaciones: List<PlantacionEntity>, largoCm: Int): List<Hueco> {
        val sorted = plantaciones.sortedBy { it.posicionXCm }
        val huecos = mutableListOf<Hueco>()
        var cursor = 0
        for (p in sorted) {
            if (p.posicionXCm > cursor) {
                huecos.add(Hueco(cursor, p.posicionXCm - cursor))
            }
            cursor = maxOf(cursor, p.posicionXCm + p.anchoCm)
        }
        if (cursor < largoCm) {
            huecos.add(Hueco(cursor, largoCm - cursor))
        }
        return huecos
    }
}
