package com.bancal.app.domain.logic

import com.bancal.app.data.db.entity.AlertaEntity
import com.bancal.app.data.db.entity.CultivoEntity
import com.bancal.app.data.db.entity.PlantacionEntity
import com.bancal.app.data.repository.BancalRepository
import com.bancal.app.domain.model.EstadoPlantacion
import com.bancal.app.domain.model.TipoAlerta
import com.bancal.app.domain.model.TipoTratamiento
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class AlertaEngine(private val repository: BancalRepository) {

    private val zone = ZoneId.of("Europe/Madrid")

    suspend fun generarAlertas(
        plantaciones: List<PlantacionEntity>,
        cultivos: Map<Long, CultivoEntity>
    ): List<AlertaEntity> {
        val alertas = mutableListOf<AlertaEntity>()
        val hoy = LocalDate.now()
        val ahora = System.currentTimeMillis()

        for (plantacion in plantaciones) {
            if (plantacion.estado == EstadoPlantacion.RETIRADO) continue
            val cultivo = cultivos[plantacion.cultivoId] ?: continue

            alertaCosecha(plantacion, cultivo, hoy, ahora)?.let { alertas.add(it) }
            alertaTrasplante(plantacion, cultivo, hoy, ahora)?.let { alertas.add(it) }
            alertaHelada(plantacion, cultivo, hoy, ahora)?.let { alertas.add(it) }
        }

        alertaTratamientoPreventivo(hoy, ahora)?.let { alertas.add(it) }
        alertaTeCompost(hoy, ahora)?.let { alertas.add(it) }
        alertaDesherbado(hoy, ahora)?.let { alertas.add(it) }
        alertas.addAll(alertaTarping(hoy, ahora))
        alertas.addAll(alertaAbonoVerde(hoy, ahora))
        alertas.addAll(alertaSugerirAbonoVerde(hoy, ahora))

        // Dedupe en una sola query: cargamos las claves de pendientes
        val existentes = repository.getAlertasPendientesKeys()
            .map { it.tipo to it.plantacionId }
            .toHashSet()
        return alertas.filter { (it.tipo.name to it.plantacionId) !in existentes }
    }

    private fun alertaCosecha(
        plantacion: PlantacionEntity,
        cultivo: CultivoEntity,
        hoy: LocalDate,
        ahora: Long
    ): AlertaEntity? {
        if (plantacion.estado == EstadoPlantacion.COSECHANDO) return null

        val fechaCosecha = Instant.ofEpochMilli(plantacion.fechaCosechaEstimada)
            .atZone(zone).toLocalDate()
        val diasRestantes = ChronoUnit.DAYS.between(hoy, fechaCosecha).toInt()

        if (diasRestantes in 0..7) {
            return AlertaEntity(
                tipo = TipoAlerta.COSECHA,
                titulo = "${cultivo.icono} ${cultivo.nombre} lista para cosechar",
                mensaje = if (diasRestantes == 0)
                    "¡Hoy es el día estimado de cosecha!"
                else
                    "Faltan aproximadamente $diasRestantes días para la cosecha",
                fecha = ahora,
                plantacionId = plantacion.id
            )
        }
        return null
    }

    private fun alertaTrasplante(
        plantacion: PlantacionEntity,
        cultivo: CultivoEntity,
        hoy: LocalDate,
        ahora: Long
    ): AlertaEntity? {
        if (plantacion.estado != EstadoPlantacion.SEMILLERO) return null
        val fechaTrasplante = plantacion.fechaTrasplanteEstimada ?: return null

        val fechaT = Instant.ofEpochMilli(fechaTrasplante).atZone(zone).toLocalDate()
        val diasRestantes = ChronoUnit.DAYS.between(hoy, fechaT).toInt()

        if (diasRestantes in -3..3) {
            return AlertaEntity(
                tipo = TipoAlerta.TRASPLANTE,
                titulo = "${cultivo.icono} ${cultivo.nombre}: trasplantar al bancal",
                mensaje = when {
                    diasRestantes < 0 -> "El trasplante lleva ${-diasRestantes} días de retraso"
                    diasRestantes == 0 -> "Hoy es el día ideal para trasplantar"
                    else -> "En $diasRestantes días estará lista para trasplantar"
                },
                fecha = ahora,
                plantacionId = plantacion.id
            )
        }
        return null
    }

    private fun alertaHelada(
        plantacion: PlantacionEntity,
        cultivo: CultivoEntity,
        hoy: LocalDate,
        ahora: Long
    ): AlertaEntity? {
        val riesgo = CalendarioEngine.riesgoHelada(hoy)
        if (riesgo == CalendarioEngine.RiesgoHelada.NULO) return null

        if (cultivo.temperaturaMinima <= 5) return null

        if (riesgo == CalendarioEngine.RiesgoHelada.ALTO ||
            (riesgo == CalendarioEngine.RiesgoHelada.MEDIO && cultivo.temperaturaMinima > 10)
        ) {
            return AlertaEntity(
                tipo = TipoAlerta.HELADA,
                titulo = "⚠\uFE0F Proteger ${cultivo.nombre}",
                mensaje = "Riesgo de helada ${riesgo.name.lowercase()}. " +
                        "${cultivo.nombre} necesita mínimo ${cultivo.temperaturaMinima}°C. " +
                        "Considera cubrir con manta térmica.",
                fecha = ahora,
                plantacionId = plantacion.id
            )
        }
        return null
    }

    private suspend fun alertaTratamientoPreventivo(
        hoy: LocalDate,
        ahora: Long
    ): AlertaEntity? {
        if (hoy.monthValue !in 4..9) return null

        val ultimoTratamiento = repository.getUltimaFechaTratamiento(TipoTratamiento.ABONO.name)
        if (ultimoTratamiento != null) {
            val fechaUltimo = Instant.ofEpochMilli(ultimoTratamiento).atZone(zone).toLocalDate()
            val diasDesde = ChronoUnit.DAYS.between(fechaUltimo, hoy).toInt()
            if (diasDesde < 15) return null
        }

        return AlertaEntity(
            tipo = TipoAlerta.TRATAMIENTO,
            titulo = "\uD83C\uDF3F Tratamiento preventivo",
            mensaje = "Han pasado más de 15 días. Considera aplicar purín de ortiga " +
                    "como fortalecedor general del bancal.",
            fecha = ahora
        )
    }

    private suspend fun alertaTarping(
        hoy: LocalDate,
        ahora: Long
    ): List<AlertaEntity> {
        val bancales = repository.getBancalesConTarping()
        return bancales.mapNotNull { bancal ->
            val desde = Instant.ofEpochMilli(bancal.tarpingDesde!!).atZone(zone).toLocalDate()
            val semanas = ChronoUnit.WEEKS.between(desde, hoy).toInt()
            when {
                semanas >= 4 -> AlertaEntity(
                    tipo = TipoAlerta.INFO,
                    titulo = "\uD83E\uDEB7 ${bancal.nombre}: tarping listo",
                    mensaje = "Llevan $semanas semanas con tarping. El bancal está listo para plantar. " +
                            "Retira la lona y planta directamente.",
                    fecha = ahora
                )
                semanas >= 2 -> AlertaEntity(
                    tipo = TipoAlerta.INFO,
                    titulo = "\uD83E\uDEB7 ${bancal.nombre}: tarping en curso",
                    mensaje = "Llevan $semanas semanas con tarping. " +
                            "Mínimo recomendado: 2-4 semanas. Puedes retirar la lona si el suelo está oscuro y húmedo.",
                    fecha = ahora
                )
                else -> null
            }
        }
    }

    private suspend fun alertaDesherbado(
        hoy: LocalDate,
        ahora: Long
    ): AlertaEntity? {
        // Solo en temporada de crecimiento (abril-septiembre)
        if (hoy.monthValue !in 4..9) return null

        val ultimoDesherbado = repository.getUltimaFechaTratamiento(TipoTratamiento.DESHERBADO.name)
        if (ultimoDesherbado != null) {
            val fechaUltimo = Instant.ofEpochMilli(ultimoDesherbado).atZone(zone).toLocalDate()
            val diasDesde = ChronoUnit.DAYS.between(fechaUltimo, hoy).toInt()
            if (diasDesde < 7) return null
        }

        return AlertaEntity(
            tipo = TipoAlerta.TRATAMIENTO,
            titulo = "\uD83C\uDF3E Desherbado",
            mensaje = "Han pasado más de 7 días. Deshierba los bancales " +
                    "para evitar competencia por nutrientes y luz.",
            fecha = ahora
        )
    }

    private suspend fun alertaAbonoVerde(
        hoy: LocalDate,
        ahora: Long
    ): List<AlertaEntity> {
        val bancales = repository.getBancalesConAbonoVerde()
        return bancales.mapNotNull { bancal ->
            val desde = Instant.ofEpochMilli(bancal.abonoVerdeDesde!!).atZone(zone).toLocalDate()
            val semanas = ChronoUnit.WEEKS.between(desde, hoy).toInt()
            when {
                semanas >= 8 -> AlertaEntity(
                    tipo = TipoAlerta.INFO,
                    titulo = "\uD83C\uDF31 ${bancal.nombre}: incorporar abono verde",
                    mensaje = "El ${bancal.abonoVerdeTipo ?: "abono verde"} lleva $semanas semanas. " +
                            "Incorpóralo al suelo antes de que florezca.",
                    fecha = ahora
                )
                semanas >= 6 -> AlertaEntity(
                    tipo = TipoAlerta.INFO,
                    titulo = "\uD83C\uDF31 ${bancal.nombre}: abono verde casi listo",
                    mensaje = "El ${bancal.abonoVerdeTipo ?: "abono verde"} lleva $semanas semanas. " +
                            "En 2-4 semanas deberás incorporarlo al suelo antes de la floración.",
                    fecha = ahora
                )
                else -> null
            }
        }
    }

    /**
     * Sugiere plantar abono verde en otoño (oct-nov) si el bancal tiene más del 50% libre
     * y no tiene ya abono verde activo.
     */
    private suspend fun alertaSugerirAbonoVerde(
        hoy: LocalDate,
        ahora: Long
    ): List<AlertaEntity> {
        if (hoy.monthValue !in 10..11) return emptyList()

        val bancales = repository.getAllBancales().first()
        return bancales.mapNotNull { bancal ->
            // Saltar si ya tiene abono verde activo
            if (bancal.abonoVerdeDesde != null) return@mapNotNull null

            val ocupado = repository.getMaxOcupado(bancal.id)
            val libre = bancal.largoCm - ocupado
            val porcentajeLibre = libre.toFloat() / bancal.largoCm

            if (porcentajeLibre >= 0.5f) {
                AlertaEntity(
                    tipo = TipoAlerta.INFO,
                    titulo = "\uD83C\uDF31 ${bancal.nombre}: sembrar abono verde",
                    mensaje = "El bancal tiene ${(porcentajeLibre * 100).toInt()}% libre. " +
                            "Siembra abono verde (veza, centeno, trébol) para proteger y enriquecer el suelo en invierno.",
                    fecha = ahora
                )
            } else null
        }
    }

    private suspend fun alertaTeCompost(
        hoy: LocalDate,
        ahora: Long
    ): AlertaEntity? {
        // Té de compost recomendado durante temporada de crecimiento (abril-septiembre)
        if (hoy.monthValue !in 4..9) return null

        val ultimoTe = repository.getUltimaFechaTratamiento(TipoTratamiento.TE_COMPOST.name)
        if (ultimoTe != null) {
            val fechaUltimo = Instant.ofEpochMilli(ultimoTe).atZone(zone).toLocalDate()
            val diasDesde = ChronoUnit.DAYS.between(fechaUltimo, hoy).toInt()
            if (diasDesde < 14) return null
        }

        return AlertaEntity(
            tipo = TipoAlerta.TRATAMIENTO,
            titulo = "\u2615 Té de compost",
            mensaje = "Han pasado más de 2 semanas. Aplica té de compost: " +
                    "pulverización foliar o riego al suelo. Inocula microorganismos beneficiosos y fortalece las plantas.",
            fecha = ahora
        )
    }
}
