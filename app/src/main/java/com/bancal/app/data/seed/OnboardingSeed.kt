package com.bancal.app.data.seed

import com.bancal.app.data.db.entity.AlertaEntity
import com.bancal.app.data.db.entity.PlantacionEntity
import com.bancal.app.domain.model.EstadoPlantacion
import com.bancal.app.domain.model.TipoAlerta
import com.bancal.app.domain.model.TipoSiembra
import java.time.LocalDate
import java.time.ZoneId

/**
 * Datos de ejemplo para el onboarding: un bancal de 10m con 5 plantaciones
 * de temporada y una alerta activa, para que el usuario vea la app en acción
 * desde el primer momento.
 *
 * Las fechas se calculan en tiempo real para que siempre sean coherentes.
 */
object OnboardingSeed {

    private fun LocalDate.toEpoch(): Long =
        atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    fun plantaciones(): List<PlantacionEntity> {
        val hoy = LocalDate.now()

        return listOf(
            // Lechugas — plantadas hace 2 semanas, creciendo (posición 0-90cm)
            PlantacionEntity(
                cultivoId = 23, // Lechuga
                bancalId = 1,
                fechaSiembra = hoy.minusDays(14).toEpoch(),
                fechaCosechaEstimada = hoy.plusDays(28).toEpoch(),
                posicionXCm = 0,
                anchoCm = 90, // 3 × 30cm
                tipoSiembra = TipoSiembra.TRASPLANTE,
                estado = EstadoPlantacion.CRECIENDO,
                notas = "Ejemplo: lechugas trasplantadas"
            ),

            // Rabanitos — siembra directa hace 10 días (posición 100-150cm)
            PlantacionEntity(
                cultivoId = 18, // Rabanito
                bancalId = 1,
                fechaSiembra = hoy.minusDays(10).toEpoch(),
                fechaCosechaEstimada = hoy.plusDays(11).toEpoch(),
                posicionXCm = 100,
                anchoCm = 50, // 5 × 10cm
                tipoSiembra = TipoSiembra.DIRECTA,
                estado = EstadoPlantacion.CRECIENDO,
                notas = "Ejemplo: rabanitos casi listos"
            ),

            // Zanahorias — recién sembradas (posición 160-220cm)
            PlantacionEntity(
                cultivoId = 30, // Zanahoria
                bancalId = 1,
                fechaSiembra = hoy.minusDays(3).toEpoch(),
                fechaCosechaEstimada = hoy.plusDays(74).toEpoch(),
                posicionXCm = 160,
                anchoCm = 60, // 4 × 15cm
                tipoSiembra = TipoSiembra.DIRECTA,
                estado = EstadoPlantacion.CRECIENDO,
                notas = "Ejemplo: zanahorias recién sembradas"
            ),

            // Tomate — en semillero, pendiente de trasplante (posición 240-340cm)
            PlantacionEntity(
                cultivoId = 1, // Tomate
                bancalId = 1,
                fechaSiembra = hoy.minusDays(30).toEpoch(),
                fechaTrasplanteEstimada = hoy.plusDays(10).toEpoch(),
                fechaCosechaEstimada = hoy.plusDays(68).toEpoch(),
                posicionXCm = 240,
                anchoCm = 100, // 2 × 50cm
                tipoSiembra = TipoSiembra.SEMILLERO,
                estado = EstadoPlantacion.SEMILLERO,
                notas = "Ejemplo: tomates en semillero"
            ),

            // Caléndula — flor auxiliar (posición 350-400cm)
            PlantacionEntity(
                cultivoId = 25, // Caléndula
                bancalId = 1,
                fechaSiembra = hoy.minusDays(20).toEpoch(),
                fechaCosechaEstimada = hoy.plusDays(40).toEpoch(),
                posicionXCm = 350,
                anchoCm = 50, // 2 × 25cm
                tipoSiembra = TipoSiembra.DIRECTA,
                estado = EstadoPlantacion.CRECIENDO,
                notas = "Ejemplo: caléndulas para atraer polinizadores"
            )
        )
    }

    fun alertas(): List<AlertaEntity> {
        val hoy = LocalDate.now()

        return listOf(
            AlertaEntity(
                tipo = TipoAlerta.COSECHA,
                titulo = "Rabanitos casi listos",
                mensaje = "Los rabanitos se sembran hace 10 dias y estaran listos en unos 11 dias mas. Vigila su tamaño.",
                fecha = hoy.toEpoch(),
                plantacionId = null // se actualizará tras insertar
            ),
            AlertaEntity(
                tipo = TipoAlerta.TRASPLANTE,
                titulo = "Trasplantar tomates pronto",
                mensaje = "Los tomates llevan 30 dias en semillero. Prepara el trasplante al bancal cuando pasen las ultimas heladas.",
                fecha = hoy.toEpoch(),
                plantacionId = null
            ),
            AlertaEntity(
                tipo = TipoAlerta.INFO,
                titulo = "Bienvenido a Bancal",
                mensaje = "Hemos creado un bancal de ejemplo con 5 plantaciones de temporada para que veas como funciona la app. Puedes eliminarlas y empezar con tus propios cultivos.",
                fecha = hoy.toEpoch()
            )
        )
    }
}
