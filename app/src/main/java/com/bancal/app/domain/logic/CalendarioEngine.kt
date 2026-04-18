package com.bancal.app.domain.logic

import com.bancal.app.data.db.entity.CultivoEntity
import com.bancal.app.domain.model.TipoSiembra
import java.time.LocalDate

/**
 * Motor de calendario de siembra adaptado al clima de Burgos.
 *
 * Burgos (zona 8b):
 * - Última helada primavera: ~15 abril (puede haber hasta mayo en años fríos)
 * - Primera helada otoño: ~15 octubre
 * - Temporada libre de heladas: ~180 días
 */
object CalendarioEngine {

    data class RecomendacionSiembra(
        val cultivo: CultivoEntity,
        val tipoSiembra: TipoSiembra,
        val recomendado: Boolean,
        val mensaje: String
    )

    /**
     * Comprueba si un mes está activo en un bitfield de meses.
     * @param bitfield campo de bits (bit 0 = enero, bit 11 = diciembre)
     * @param mes mes del año (1 = enero, 12 = diciembre)
     */
    fun mesActivo(bitfield: Int, mes: Int): Boolean =
        (bitfield and (1 shl (mes - 1))) != 0

    /**
     * Devuelve los meses activos como lista de enteros (1-12).
     */
    fun mesesActivos(bitfield: Int): List<Int> =
        (1..12).filter { mesActivo(bitfield, it) }

    /**
     * Evalúa si es buen momento para sembrar/plantar un cultivo.
     */
    fun evaluarSiembra(cultivo: CultivoEntity, fecha: LocalDate = LocalDate.now()): RecomendacionSiembra {
        val mes = fecha.monthValue

        // Prioridad: trasplante > semillero > directa
        if (mesActivo(cultivo.mesesTrasplante, mes)) {
            return RecomendacionSiembra(
                cultivo, TipoSiembra.TRASPLANTE, true,
                "Buen momento para trasplantar ${cultivo.nombre}"
            )
        }

        if (mesActivo(cultivo.mesesSemillero, mes)) {
            return RecomendacionSiembra(
                cultivo, TipoSiembra.SEMILLERO, true,
                "Buen momento para sembrar ${cultivo.nombre} en semillero"
            )
        }

        if (mesActivo(cultivo.mesesSiembraDirecta, mes)) {
            return RecomendacionSiembra(
                cultivo, TipoSiembra.DIRECTA, true,
                "Buen momento para siembra directa de ${cultivo.nombre}"
            )
        }

        // No es buen momento — buscar el próximo mes recomendado
        val proximoMes = encontrarProximoMes(cultivo, mes)
        val tipoProximo = when {
            proximoMes != null && mesActivo(cultivo.mesesSemillero, proximoMes) -> "semillero"
            proximoMes != null && mesActivo(cultivo.mesesTrasplante, proximoMes) -> "trasplante"
            else -> "siembra directa"
        }

        val nombreMes = nombreMes(proximoMes ?: mes)

        return RecomendacionSiembra(
            cultivo, TipoSiembra.DIRECTA, false,
            "No es época de ${cultivo.nombre}. Próxima ventana: $nombreMes ($tipoProximo)"
        )
    }

    /**
     * Devuelve los cultivos que se pueden sembrar/plantar en un mes dado.
     */
    fun cultivosParaMes(cultivos: List<CultivoEntity>, mes: Int): Map<TipoSiembra, List<CultivoEntity>> {
        val resultado = mutableMapOf<TipoSiembra, MutableList<CultivoEntity>>()

        for (cultivo in cultivos) {
            if (mesActivo(cultivo.mesesSemillero, mes)) {
                resultado.getOrPut(TipoSiembra.SEMILLERO) { mutableListOf() }.add(cultivo)
            }
            if (mesActivo(cultivo.mesesSiembraDirecta, mes)) {
                resultado.getOrPut(TipoSiembra.DIRECTA) { mutableListOf() }.add(cultivo)
            }
            if (mesActivo(cultivo.mesesTrasplante, mes)) {
                resultado.getOrPut(TipoSiembra.TRASPLANTE) { mutableListOf() }.add(cultivo)
            }
        }

        return resultado
    }

    /**
     * Calcula la fecha estimada de cosecha.
     */
    fun fechaCosechaEstimada(fechaSiembra: LocalDate, cultivo: CultivoEntity): LocalDate =
        fechaSiembra.plusDays(cultivo.diasCosecha.toLong())

    /**
     * Calcula la fecha estimada de trasplante (si se sembró en semillero).
     */
    fun fechaTrasplanteEstimada(fechaSiembra: LocalDate, cultivo: CultivoEntity): LocalDate =
        fechaSiembra.plusDays(cultivo.diasGerminacion.toLong() + 14) // germinación + 2 semanas de fortalecimiento

    /**
     * Evalúa riesgo de helada según la fecha.
     */
    fun riesgoHelada(fecha: LocalDate = LocalDate.now()): RiesgoHelada {
        val diaMes = fecha.monthValue * 100 + fecha.dayOfMonth
        return when {
            diaMes < 315 -> RiesgoHelada.ALTO    // antes del 15 de marzo
            diaMes < 415 -> RiesgoHelada.MEDIO   // 15 marzo - 15 abril
            diaMes < 501 -> RiesgoHelada.BAJO    // 15 abril - 1 mayo
            diaMes < 1001 -> RiesgoHelada.NULO   // mayo - septiembre
            diaMes < 1015 -> RiesgoHelada.BAJO   // 1-15 octubre
            diaMes < 1101 -> RiesgoHelada.MEDIO  // 15-31 octubre
            else -> RiesgoHelada.ALTO             // noviembre en adelante
        }
    }

    private fun encontrarProximoMes(cultivo: CultivoEntity, mesActual: Int): Int? {
        val todos = cultivo.mesesSiembraDirecta or cultivo.mesesSemillero or cultivo.mesesTrasplante
        for (i in 1..12) {
            val mes = ((mesActual - 1 + i) % 12) + 1
            if (mesActivo(todos, mes)) return mes
        }
        return null
    }

    fun nombreMes(mes: Int): String = when (mes) {
        1 -> "Enero"; 2 -> "Febrero"; 3 -> "Marzo"; 4 -> "Abril"
        5 -> "Mayo"; 6 -> "Junio"; 7 -> "Julio"; 8 -> "Agosto"
        9 -> "Septiembre"; 10 -> "Octubre"; 11 -> "Noviembre"; 12 -> "Diciembre"
        else -> ""
    }

    enum class RiesgoHelada { NULO, BAJO, MEDIO, ALTO }
}
