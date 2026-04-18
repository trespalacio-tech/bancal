package com.bancal.app.data.repository

import com.bancal.app.data.db.dao.*
import com.bancal.app.data.db.entity.*
import kotlinx.coroutines.flow.flowOf
import com.bancal.app.domain.model.EstadoPlantacion
import com.bancal.app.domain.model.FamiliaCultivo
import kotlinx.coroutines.flow.Flow

class BancalRepository(
    private val cultivoDao: CultivoDao,
    private val plantacionDao: PlantacionDao,
    private val tratamientoDao: TratamientoDao,
    private val asociacionDao: AsociacionDao,
    private val alertaDao: AlertaDao,
    private val diarioDao: DiarioDao? = null,
    private val bancalDao: BancalDao? = null
) {

    // === Bancales ===
    fun getAllBancales(): Flow<List<BancalEntity>> = bancalDao?.getAll() ?: flowOf(emptyList())
    fun getBancal(id: Long): Flow<BancalEntity?> = bancalDao?.getByIdFlow(id) ?: flowOf(null)
    suspend fun getBancalSync(id: Long): BancalEntity? = bancalDao?.getById(id)
    suspend fun insertBancal(bancal: BancalEntity): Long = bancalDao?.insert(bancal) ?: 0L
    suspend fun updateBancal(bancal: BancalEntity) = bancalDao?.update(bancal)
    suspend fun deleteBancal(bancal: BancalEntity) = bancalDao?.delete(bancal)
    suspend fun getBancalesConTarping(): List<BancalEntity> =
        bancalDao?.getConTarping() ?: emptyList()
    suspend fun getBancalesConAbonoVerde(): List<BancalEntity> =
        bancalDao?.getConAbonoVerde() ?: emptyList()

    // === Cultivos ===
    fun getCultivos(): Flow<List<CultivoEntity>> = cultivoDao.getAll()
    fun getCultivosByFamilia(familia: FamiliaCultivo) = cultivoDao.getByFamilia(familia)
    fun searchCultivos(query: String) = cultivoDao.search(query)
    suspend fun getCultivo(id: Long) = cultivoDao.getById(id)
    suspend fun insertCultivo(cultivo: CultivoEntity): Long = cultivoDao.insert(cultivo)
    suspend fun deleteCultivoPersonalizado(id: Long) = cultivoDao.deletePersonalizado(id)

    // === Plantaciones (filtradas por bancalId) ===
    fun getPlantacionesActivas(bancalId: Long = 1): Flow<List<PlantacionEntity>> =
        plantacionDao.getActivasByBancal(bancalId)

    fun getAllPlantaciones(bancalId: Long = 1): Flow<List<PlantacionEntity>> =
        plantacionDao.getAllByBancal(bancalId)

    suspend fun getPlantacion(id: Long) = plantacionDao.getById(id)

    suspend fun getPlantacionesEnRango(startX: Int, endX: Int, bancalId: Long = 1) =
        plantacionDao.getInRangeByBancal(bancalId, startX, endX)

    suspend fun getTodasPlantacionesEnRango(startX: Int, endX: Int, bancalId: Long = 1) =
        plantacionDao.getAllInRangeByBancal(bancalId, startX, endX)

    suspend fun countPlantacionesActivas(bancalId: Long) =
        plantacionDao.countActivasByBancal(bancalId)

    suspend fun getMaxOcupado(bancalId: Long) =
        plantacionDao.getMaxOcupadoByBancal(bancalId)

    suspend fun insertPlantacion(plantacion: PlantacionEntity): Long =
        plantacionDao.insert(plantacion)

    suspend fun updatePlantacion(plantacion: PlantacionEntity) =
        plantacionDao.update(plantacion)

    suspend fun deletePlantacion(plantacion: PlantacionEntity) =
        plantacionDao.delete(plantacion)

    suspend fun updateEstadoPlantacion(id: Long, estado: EstadoPlantacion) =
        plantacionDao.updateEstado(id, estado)

    // === Plantaciones (globales, todos los bancales) ===
    fun getTodasPlantacionesActivas(): Flow<List<PlantacionEntity>> = plantacionDao.getActivas()
    fun getTodasPlantaciones(): Flow<List<PlantacionEntity>> = plantacionDao.getAll()

    // === Tratamientos ===
    fun getTratamientos(): Flow<List<TratamientoEntity>> = tratamientoDao.getAll()
    fun getTratamientosPlantacion(plantacionId: Long) = tratamientoDao.getByPlantacion(plantacionId)

    suspend fun insertTratamiento(tratamiento: TratamientoEntity): Long =
        tratamientoDao.insert(tratamiento)

    suspend fun deleteTratamiento(tratamiento: TratamientoEntity) =
        tratamientoDao.delete(tratamiento)

    suspend fun getUltimaFechaTratamiento(tipo: String) =
        tratamientoDao.getUltimaFecha(tipo)

    // === Asociaciones ===
    suspend fun getAsociaciones(cultivoId: Long) = asociacionDao.getByCultivo(cultivoId)
    suspend fun getAsociacion(id1: Long, id2: Long) = asociacionDao.getAsociacion(id1, id2)
    suspend fun getIntercalables(cultivoId: Long) = asociacionDao.getIntercalables(cultivoId)

    // === Intercalado ===
    suspend fun tieneIntercalado(plantacionId: Long) = plantacionDao.countIntercalados(plantacionId) > 0
    suspend fun getIntercalados(plantacionId: Long) = plantacionDao.getIntercalados(plantacionId)

    // === Alertas ===
    fun getAlertasPendientes(): Flow<List<AlertaEntity>> = alertaDao.getPendientes()
    fun getAllAlertas(): Flow<List<AlertaEntity>> = alertaDao.getAll()
    fun countAlertasPendientes(): Flow<Int> = alertaDao.countPendientes()

    suspend fun insertAlerta(alerta: AlertaEntity): Long = alertaDao.insert(alerta)
    suspend fun resolverAlerta(id: Long) = alertaDao.resolver(id)
    suspend fun limpiarAlertasResueltas() = alertaDao.limpiarResueltas()
    suspend fun existeAlerta(tipo: String, plantacionId: Long) =
        alertaDao.existeAlerta(tipo, plantacionId) > 0
    suspend fun getAlertasPendientesKeys() = alertaDao.getPendientesKeys()

    // === Diario ===
    fun getDiarioEntradas(limit: Int = 30) = diarioDao?.getRecientes(limit) ?: flowOf(emptyList())
    fun getAllDiario() = diarioDao?.getAll() ?: flowOf(emptyList())
    suspend fun getDiarioByFecha(fechaEpoch: Long) = diarioDao?.getByFecha(fechaEpoch)
    suspend fun insertDiario(diario: DiarioEntity) = diarioDao?.insert(diario) ?: 0L
    suspend fun acumularCosecha(fechaEpoch: Long, kg: Float, nota: String) {
        diarioDao?.acumularCosecha(fechaEpoch, kg, nota)
    }
    suspend fun updateDiario(diario: DiarioEntity) = diarioDao?.update(diario)
    suspend fun deleteDiario(diario: DiarioEntity) = diarioDao?.delete(diario)
    fun countDiasHelada() = diarioDao?.countDiasHelada() ?: flowOf(0)
    fun totalCosechaKg() = diarioDao?.totalCosechaKg() ?: flowOf(null)
    fun totalLluviaMm() = diarioDao?.totalLluviaMm() ?: flowOf(null)
    fun countEntradasDiario() = diarioDao?.countEntradas() ?: flowOf(0)
}
