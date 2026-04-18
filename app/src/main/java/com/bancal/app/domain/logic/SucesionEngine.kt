package com.bancal.app.domain.logic

import com.bancal.app.data.db.entity.CultivoEntity
import com.bancal.app.data.db.entity.PlantacionEntity
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/**
 * Motor de siembra escalonada y relevo de cultivos.
 *
 * Calcula:
 * 1. Próximas siembras escalonadas para cultivos activos que lo soporten
 * 2. Sugerencias de relevo cuando una zona queda libre (respetando rotación por familia)
 */
object SucesionEngine {

    private val zone = ZoneId.of("Europe/Madrid")

    // Primera helada otoño en Burgos: ~15 octubre
    private fun primeraHelada(year: Int): LocalDate = LocalDate.of(year, 10, 15)

    data class SiembraEscalonada(
        val cultivo: CultivoEntity,
        val proximaSiembra: LocalDate,
        val diasRestantes: Long,
        val siembrasRestantesEnTemporada: Int
    )

    data class SugerenciaRelevo(
        val cultivo: CultivoEntity,
        val motivo: String,
        val diasHastaCosecha: Int,
        val llegaAntesDeLaHelada: Boolean
    )

    /**
     * Para cada plantación activa cuyo cultivo soporta sucesión,
     * calcula cuándo debería sembrarse la siguiente tanda.
     */
    fun calcularSiembrasEscalonadas(
        plantaciones: List<PlantacionEntity>,
        cultivoMap: Map<Long, CultivoEntity>,
        hoy: LocalDate = LocalDate.now()
    ): List<SiembraEscalonada> {
        val resultado = mutableListOf<SiembraEscalonada>()
        val yaCalculados = mutableSetOf<Long>() // cultivoId — una entrada por cultivo

        for (p in plantaciones) {
            val cultivo = cultivoMap[p.cultivoId] ?: continue
            if (cultivo.intervaloSucesionDias <= 0) continue
            if (cultivo.id in yaCalculados) continue
            yaCalculados.add(cultivo.id)

            val fechaSiembra = Instant.ofEpochMilli(p.fechaSiembra).atZone(zone).toLocalDate()
            val intervalo = cultivo.intervaloSucesionDias.toLong()

            // Calcular la próxima fecha de siembra a partir de la última siembra
            var proximaSiembra = fechaSiembra.plusDays(intervalo)
            while (proximaSiembra.isBefore(hoy)) {
                proximaSiembra = proximaSiembra.plusDays(intervalo)
            }

            // Verificar que estamos en un mes válido para sembrar
            val mesProxima = proximaSiembra.monthValue
            val puedeEnMes = CalendarioEngine.mesActivo(cultivo.mesesSiembraDirecta, mesProxima)
                    || CalendarioEngine.mesActivo(cultivo.mesesSemillero, mesProxima)
                    || CalendarioEngine.mesActivo(cultivo.mesesTrasplante, mesProxima)

            if (!puedeEnMes) continue

            // Verificar que la cosecha llegaría antes de la helada
            val fechaCosecha = proximaSiembra.plusDays(cultivo.diasCosecha.toLong())
            val helada = primeraHelada(hoy.year)
            if (cultivo.temperaturaMinima > 0 && fechaCosecha.isAfter(helada)) continue

            val diasRestantes = ChronoUnit.DAYS.between(hoy, proximaSiembra)

            // Contar cuántas siembras más caben en la temporada
            var count = 0
            var fecha = proximaSiembra
            while (true) {
                val cosecha = fecha.plusDays(cultivo.diasCosecha.toLong())
                val mesFecha = fecha.monthValue
                val puedeEnEsteMes = CalendarioEngine.mesActivo(cultivo.mesesSiembraDirecta, mesFecha)
                        || CalendarioEngine.mesActivo(cultivo.mesesSemillero, mesFecha)
                        || CalendarioEngine.mesActivo(cultivo.mesesTrasplante, mesFecha)
                if (!puedeEnEsteMes) break
                if (cultivo.temperaturaMinima > 0 && cosecha.isAfter(helada)) break
                count++
                fecha = fecha.plusDays(intervalo)
            }

            resultado.add(
                SiembraEscalonada(
                    cultivo = cultivo,
                    proximaSiembra = proximaSiembra,
                    diasRestantes = diasRestantes,
                    siembrasRestantesEnTemporada = count
                )
            )
        }

        return resultado.sortedBy { it.diasRestantes }
    }

    /**
     * Para plantaciones próximas a cosechar o ya en cosecha,
     * sugiere qué plantar a continuación respetando rotación por familia.
     */
    fun sugerirRelevos(
        plantaciones: List<PlantacionEntity>,
        cultivoMap: Map<Long, CultivoEntity>,
        todosCultivos: List<CultivoEntity>,
        hoy: LocalDate = LocalDate.now()
    ): List<Pair<PlantacionEntity, List<SugerenciaRelevo>>> {
        val resultado = mutableListOf<Pair<PlantacionEntity, List<SugerenciaRelevo>>>()
        val helada = primeraHelada(hoy.year)

        for (p in plantaciones) {
            val cultivoActual = cultivoMap[p.cultivoId] ?: continue

            // Solo sugerir para plantaciones próximas a cosechar (< 14 días) o ya cosechando
            val fechaCosecha = Instant.ofEpochMilli(p.fechaCosechaEstimada).atZone(zone).toLocalDate()
            val diasParaCosecha = ChronoUnit.DAYS.between(hoy, fechaCosecha)
            if (diasParaCosecha > 14) continue

            val fechaLibre = if (fechaCosecha.isAfter(hoy)) fechaCosecha else hoy
            val mesLibre = fechaLibre.monthValue

            val sugerencias = todosCultivos
                .filter { c ->
                    // Diferente familia (rotación)
                    c.familia != cultivoActual.familia
                            // Se puede sembrar en ese mes
                            && (CalendarioEngine.mesActivo(c.mesesSiembraDirecta, mesLibre)
                            || CalendarioEngine.mesActivo(c.mesesSemillero, mesLibre)
                            || CalendarioEngine.mesActivo(c.mesesTrasplante, mesLibre))
                            // Cabe en el espacio
                            && c.marcoCm <= p.anchoCm + 10
                }
                .map { c ->
                    val cosechaRelevo = fechaLibre.plusDays(c.diasCosecha.toLong())
                    val llegaAntesHelada = c.temperaturaMinima <= 0 || cosechaRelevo.isBefore(helada)
                    val motivo = buildString {
                        append("${c.diasCosecha}d hasta cosecha")
                        if (c.intervaloSucesionDias > 0) {
                            append(" · escalonable cada ${c.intervaloSucesionDias}d")
                        }
                    }
                    SugerenciaRelevo(
                        cultivo = c,
                        motivo = motivo,
                        diasHastaCosecha = c.diasCosecha,
                        llegaAntesDeLaHelada = llegaAntesHelada
                    )
                }
                .sortedWith(compareByDescending<SugerenciaRelevo> { it.llegaAntesDeLaHelada }
                    .thenBy { it.diasHastaCosecha })
                .take(6)

            if (sugerencias.isNotEmpty()) {
                resultado.add(p to sugerencias)
            }
        }

        return resultado
    }
}
