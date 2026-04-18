package com.bancal.app.domain.logic

import com.bancal.app.data.db.entity.AsociacionEntity
import com.bancal.app.data.db.entity.CultivoEntity
import com.bancal.app.data.db.entity.PlantacionEntity
import com.bancal.app.data.repository.BancalRepository
import com.bancal.app.domain.model.TipoAsociacion

/**
 * Motor de asociaciones de cultivos.
 * Evalúa la compatibilidad entre cultivos en el bancal,
 * considerando la distancia entre plantaciones.
 *
 * Todas las queries de plantaciones reciben bancalId para soportar multi-bancal.
 */
class AsociacionEngine(private val repository: BancalRepository) {

    data class ResultadoAsociacion(
        val cultivoVecino: CultivoEntity,
        val tipo: TipoAsociacion,
        val motivo: String,
        val distanciaCm: Int
    )

    /**
     * Radio de influencia: cultivos a menos de esta distancia se consideran vecinos.
     */
    private val radioInfluenciaCm = 100 // 1 metro

    /**
     * Evalúa las asociaciones de un cultivo con los vecinos existentes en el bancal.
     */
    suspend fun evaluarAsociaciones(
        cultivoId: Long,
        posicionXCm: Int,
        anchoCm: Int,
        bancalId: Long = 1
    ): List<ResultadoAsociacion> {
        val resultados = mutableListOf<ResultadoAsociacion>()

        // Buscar plantaciones vecinas dentro del radio de influencia
        val startX = (posicionXCm - radioInfluenciaCm).coerceAtLeast(0)
        val endX = posicionXCm + anchoCm + radioInfluenciaCm
        val vecinos = repository.getPlantacionesEnRango(startX, endX, bancalId)

        for (vecino in vecinos) {
            val cultivoVecino = repository.getCultivo(vecino.cultivoId) ?: continue
            val asociacion = repository.getAsociacion(cultivoId, vecino.cultivoId)
            val distancia = calcularDistancia(posicionXCm, anchoCm, vecino)

            if (asociacion != null) {
                resultados.add(
                    ResultadoAsociacion(
                        cultivoVecino = cultivoVecino,
                        tipo = asociacion.tipo,
                        motivo = asociacion.motivo,
                        distanciaCm = distancia
                    )
                )
            }
        }

        return resultados.sortedBy { it.distanciaCm }
    }

    /**
     * Verifica si hay alguna asociación perjudicial.
     */
    suspend fun tieneIncompatibilidades(
        cultivoId: Long,
        posicionXCm: Int,
        anchoCm: Int,
        bancalId: Long = 1
    ): List<ResultadoAsociacion> =
        evaluarAsociaciones(cultivoId, posicionXCm, anchoCm, bancalId)
            .filter { it.tipo == TipoAsociacion.PERJUDICIAL }

    /**
     * Verifica si hay asociaciones beneficiosas.
     */
    suspend fun tieneCompañerosBeneficiosos(
        cultivoId: Long,
        posicionXCm: Int,
        anchoCm: Int,
        bancalId: Long = 1
    ): List<ResultadoAsociacion> =
        evaluarAsociaciones(cultivoId, posicionXCm, anchoCm, bancalId)
            .filter { it.tipo == TipoAsociacion.BENEFICIOSA }

    /**
     * Comprueba si una zona del bancal ya está ocupada por otra plantación.
     */
    suspend fun zonaOcupada(posicionXCm: Int, anchoCm: Int, bancalId: Long = 1): Boolean {
        val plantaciones = repository.getPlantacionesEnRango(posicionXCm, posicionXCm + anchoCm, bancalId)
        return plantaciones.isNotEmpty()
    }

    /**
     * Comprueba si dos cultivos pueden intercalarse (compartir el mismo espacio).
     */
    suspend fun esIntercalable(cultivoId1: Long, cultivoId2: Long): Boolean {
        val asociacion = repository.getAsociacion(cultivoId1, cultivoId2)
        return asociacion?.intercalable == true
    }

    /**
     * Devuelve las plantaciones en una zona con las que se podría intercalar un cultivo dado.
     */
    suspend fun getIntercalablesEn(
        cultivoId: Long,
        posicionXCm: Int,
        anchoCm: Int,
        bancalId: Long = 1
    ): List<PlantacionEntity> {
        val plantaciones = repository.getPlantacionesEnRango(posicionXCm, posicionXCm + anchoCm, bancalId)
        return plantaciones.filter { p ->
            p.intercaladaCon == null &&
                !repository.tieneIntercalado(p.id) &&
                esIntercalable(cultivoId, p.cultivoId)
        }
    }

    /**
     * Sugiere la mejor posición para un cultivo considerando asociaciones.
     * Devuelve la posición X que maximiza asociaciones beneficiosas y minimiza perjudiciales.
     * @param bancalLargoCm longitud del bancal en cm
     * @param bancalId ID del bancal donde se evalúan las posiciones
     */
    suspend fun sugerirPosicion(cultivoId: Long, anchoCm: Int, bancalLargoCm: Int, bancalId: Long = 1): Int? {
        // Cargar plantaciones y asociaciones del cultivo UNA sola vez
        val plantaciones = repository.getPlantacionesEnRango(0, bancalLargoCm, bancalId)
        val asociacionesCultivo = repository.getAsociaciones(cultivoId)
        // Map vecinoCultivoId -> tipo (para lookup O(1))
        val asocMap: Map<Long, TipoAsociacion> = asociacionesCultivo.associate { a ->
            val otro = if (a.cultivoId1 == cultivoId) a.cultivoId2 else a.cultivoId1
            otro to a.tipo
        }

        val paso = 10
        var mejorPosicion: Int? = null
        var mejorPuntuacion = Int.MIN_VALUE

        for (x in 0..bancalLargoCm - anchoCm step paso) {
            // ¿Solapa con alguna plantación?
            val finX = x + anchoCm
            val solapa = plantaciones.any { p ->
                val pFin = p.posicionXCm + p.anchoCm
                x < pFin && finX > p.posicionXCm
            }
            if (solapa) continue

            // Puntuar vecinos dentro del radio de influencia
            var puntuacion = 0
            for (p in plantaciones) {
                val distancia = distanciaEntre(x, anchoCm, p)
                if (distancia > radioInfluenciaCm) continue
                val tipo = asocMap[p.cultivoId] ?: continue
                puntuacion += when (tipo) {
                    TipoAsociacion.BENEFICIOSA -> 10
                    TipoAsociacion.PERJUDICIAL -> -20
                }
            }

            if (puntuacion > mejorPuntuacion) {
                mejorPuntuacion = puntuacion
                mejorPosicion = x
            }
        }

        return mejorPosicion
    }

    private fun distanciaEntre(posX: Int, ancho: Int, vecino: PlantacionEntity): Int {
        val finA = posX + ancho
        val finB = vecino.posicionXCm + vecino.anchoCm
        // Distancia entre intervalos (0 si solapan)
        return when {
            finA < vecino.posicionXCm -> vecino.posicionXCm - finA
            finB < posX -> posX - finB
            else -> 0
        }
    }

    private fun calcularDistancia(posX: Int, ancho: Int, vecino: PlantacionEntity): Int {
        val centroNuevo = posX + ancho / 2
        val centroVecino = vecino.posicionXCm + vecino.anchoCm / 2
        return kotlin.math.abs(centroNuevo - centroVecino)
    }
}
