package com.bancal.app.ui.screens.bancal

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Canvas
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.ui.platform.LocalContext
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bancal.app.domain.logic.CalendarioEngine
import com.bancal.app.ui.components.BancalCanvas
import com.bancal.app.ui.components.PlantacionVisual
import com.bancal.app.ui.theme.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@Composable
fun BancalScreen(
    viewModel: BancalViewModel,
    onNavigateToPlantarAt: (Int) -> Unit,
    onNavigateToDetalle: (Long) -> Unit,
    onNavigateToPlantar: () -> Unit,
    onNavigateToBalance: () -> Unit = {},
    onNavigateToSucesion: () -> Unit = {},
    onNavigateToDiario: () -> Unit = {},
    onNavigateToGestion: () -> Unit = {},
    onNavigateToBackup: () -> Unit = {}
) {
    // Sincronizar selección al entrar (otro ViewModel pudo cambiarla en SharedPreferences)
    LaunchedEffect(Unit) {
        viewModel.syncSelection()
    }

    val plantaciones by viewModel.plantacionesVisuales.collectAsState()
    val bancal by viewModel.bancal.collectAsState()
    val bancales by viewModel.bancales.collectAsState()
    val riesgoHelada = CalendarioEngine.riesgoHelada()
    val hoy = LocalDate.now()

    val largoTexto = if (bancal.largoCm % 100 == 0) "${bancal.largoCm / 100}m" else "%.1fm".format(bancal.largoCm / 100f)
    val ocupadoCm = plantaciones.sumOf { it.plantacion.anchoCm }
    val porcentajeOcupado = if (bancal.largoCm > 0) (ocupadoCm * 100 / bancal.largoCm) else 0
    val context = LocalContext.current

    val zoneCosecha = ZoneId.of("Europe/Madrid")
    val proxima = plantaciones
        .filter { it.plantacion.estado != com.bancal.app.domain.model.EstadoPlantacion.RETIRADO }
        .minByOrNull { it.plantacion.fechaCosechaEstimada }
    val sortedPlantaciones = plantaciones.sortedBy { it.plantacion.fechaCosechaEstimada }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Hero card del bancal
            item(key = "hero") {
                val heroGradient = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        MaterialTheme.colorScheme.surface
                    )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(heroGradient)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = bancal.nombre,
                                    style = MaterialTheme.typography.headlineLarge
                                )
                                Spacer(Modifier.height(6.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    StatPill(text = "$largoTexto × ${bancal.anchoCm}cm")
                                    StatPill(text = "${CalendarioEngine.nombreMes(hoy.monthValue)} ${hoy.year}")
                                }
                            }
                            IconButton(onClick = {
                                val texto = construirTextoCompartir(bancal, plantaciones, hoy)
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "Mi bancal: ${bancal.nombre}")
                                    putExtra(Intent.EXTRA_TEXT, texto)
                                }
                                context.startActivity(Intent.createChooser(intent, "Compartir bancal"))
                            }) {
                                Icon(
                                    Icons.Default.Share,
                                    contentDescription = "Compartir bancal",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Box {
                                var showMenu by remember { mutableStateOf(false) }
                                IconButton(onClick = { showMenu = true }) {
                                    Icon(
                                        Icons.Default.Settings,
                                        contentDescription = "Ajustes",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Gestionar bancales") },
                                        onClick = {
                                            showMenu = false
                                            onNavigateToGestion()
                                        },
                                        leadingIcon = { Icon(Icons.Default.Grass, null) }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Copias de seguridad") },
                                        onClick = {
                                            showMenu = false
                                            onNavigateToBackup()
                                        },
                                        leadingIcon = { Icon(Icons.Default.CloudUpload, null) }
                                    )
                                }
                            }
                        }

                        // Barra de ocupación visual
                        Spacer(Modifier.height(12.dp))
                        val fraction = if (bancal.largoCm > 0) (ocupadoCm.toFloat() / bancal.largoCm).coerceIn(0f, 1f) else 0f
                        val animatedFraction by animateFloatAsState(
                            targetValue = fraction,
                            animationSpec = tween(durationMillis = 600),
                            label = "occupancy"
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(animatedFraction)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = "${ocupadoCm}/${bancal.largoCm} cm",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Selector de bancal (solo si hay más de 1)
            if (bancales.size > 1) {
                item(key = "selector") {
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (b in bancales) {
                            FilterChip(
                                selected = b.id == bancal.id,
                                onClick = { viewModel.seleccionarBancal(b.id) },
                                label = { Text(b.nombre) },
                                leadingIcon = if (b.id == bancal.id) {
                                    {
                                        Icon(
                                            Icons.Default.Grass,
                                            contentDescription = null,
                                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                                        )
                                    }
                                } else null
                            )
                        }
                    }
                }
            }

            // Alerta de helada si aplica
            if (riesgoHelada != CalendarioEngine.RiesgoHelada.NULO) {
                item(key = "helada") {
                    Spacer(Modifier.height(8.dp))
                    HelperChip(riesgoHelada)
                }
            }

            // Tarping activo (capturamos a local para evitar crash si bancal cambia entre recomposiciones)
            val tarpingDesde = bancal.tarpingDesde
            if (tarpingDesde != null) {
                item(key = "tarping") {
                    val desde = java.time.Instant.ofEpochMilli(tarpingDesde)
                        .atZone(zoneCosecha).toLocalDate()
                    val semanas = ChronoUnit.WEEKS.between(desde, hoy).toInt()
                    val dias = ChronoUnit.DAYS.between(desde, hoy).toInt()
                    val texto = when {
                        semanas >= 4 -> "Tarping listo ($semanas sem). Retira la lona y planta."
                        semanas >= 2 -> "Tarping en curso: $semanas sem ($dias días). Mínimo 2-4 sem."
                        else -> "Tarping iniciado hace $dias días. Mínimo 2-4 semanas."
                    }
                    val accentColor = if (semanas >= 4) SuccessGreen else WarningAmber
                    Spacer(Modifier.height(6.dp))
                    InfoBanner(emoji = "\uD83E\uDEB7", text = texto, accent = accentColor)
                }
            }

            // Abono verde activo (mismo patrón)
            val abonoVerdeDesde = bancal.abonoVerdeDesde
            if (abonoVerdeDesde != null) {
                item(key = "abono_verde") {
                    val desde = java.time.Instant.ofEpochMilli(abonoVerdeDesde)
                        .atZone(zoneCosecha).toLocalDate()
                    val semanas = ChronoUnit.WEEKS.between(desde, hoy).toInt()
                    val dias = ChronoUnit.DAYS.between(desde, hoy).toInt()
                    val tipo = bancal.abonoVerdeTipo ?: "Abono verde"
                    val texto = when {
                        semanas >= 8 -> "$tipo listo ($semanas sem). Incorpora al suelo antes de florecer."
                        semanas >= 6 -> "$tipo: $semanas sem ($dias días). Casi listo para incorporar."
                        else -> "$tipo sembrado hace $dias días. Incorporar en 6-8 semanas."
                    }
                    val accentColor = if (semanas >= 8) SuccessGreen else WarningAmber
                    Spacer(Modifier.height(6.dp))
                    InfoBanner(emoji = "\uD83C\uDF31", text = texto, accent = accentColor)
                }
            }

            // Próxima cosecha
            if (proxima != null) {
                item(key = "cosecha") {
                    val fc = java.time.Instant.ofEpochMilli(proxima.plantacion.fechaCosechaEstimada)
                        .atZone(zoneCosecha).toLocalDate()
                    val dias = ChronoUnit.DAYS.between(hoy, fc).toInt()
                    val texto = when {
                        dias < 0 -> "lista hace ${-dias}d"
                        dias == 0 -> "lista hoy"
                        dias == 1 -> "en 1 día"
                        else -> "en $dias días"
                    }
                    Spacer(Modifier.height(6.dp))
                    InfoBanner(
                        emoji = proxima.cultivo.icono,
                        text = "Próxima cosecha: ${proxima.cultivo.nombre} · $texto",
                        accent = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Action chips
            item(key = "actions") {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ActionChip(
                        label = "Balance 60/30/10",
                        icon = Icons.Default.BarChart,
                        onClick = onNavigateToBalance
                    )
                    ActionChip(
                        label = "Escalonada",
                        icon = Icons.Default.Loop,
                        onClick = onNavigateToSucesion
                    )
                    ActionChip(
                        label = "Cuaderno",
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        onClick = onNavigateToDiario
                    )
                    ActionChip(
                        label = if (bancal.tarpingDesde != null) "Quitar tarping" else "Tarping",
                        icon = Icons.Default.Landscape,
                        onClick = { viewModel.toggleTarping() }
                    )
                    ActionChip(
                        label = if (bancal.abonoVerdeDesde != null) "Quitar abono verde" else "Abono verde",
                        icon = Icons.Default.Grass,
                        onClick = { viewModel.toggleAbonoVerde() }
                    )
                }
            }

            // Vista del bancal
            item(key = "canvas_header") {
                Spacer(Modifier.height(12.dp))
                FadeDivider()
                SectionHeader(title = "Vista cenital")
                Spacer(Modifier.height(8.dp))
            }

            item(key = "canvas") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(4.dp)
                ) {
                    BancalCanvas(
                        plantaciones = plantaciones,
                        bancalLargoCm = bancal.largoCm,
                        onTapZonaVacia = { posX -> onNavigateToPlantarAt(posX) },
                        onTapPlantacion = { p -> onNavigateToDetalle(p.id) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Lista de plantaciones activas
            item(key = "list_header") {
                Spacer(Modifier.height(16.dp))
                FadeDivider()
                SectionHeader(
                    title = "Plantaciones activas",
                    count = plantaciones.size
                )
                Spacer(Modifier.height(8.dp))
            }

            if (plantaciones.isEmpty()) {
                item(key = "empty") {
                    EmptyBancalState()
                }
            } else {
                items(
                    sortedPlantaciones,
                    key = { it.plantacion.id }
                ) { pv ->
                    PlantacionListItem(
                        plantacionVisual = pv,
                        onClick = { onNavigateToDetalle(pv.plantacion.id) }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        // FAB extendido cuando el bancal está vacío (más invitador), normal cuando hay plantaciones
        if (plantaciones.isEmpty()) {
            ExtendedFloatingActionButton(
                onClick = onNavigateToPlantar,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(22.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Plantar") },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        } else {
            FloatingActionButton(
                onClick = onNavigateToPlantar,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir plantación")
            }
        }
    }
}

private fun construirTextoCompartir(
    bancal: com.bancal.app.data.db.entity.BancalEntity,
    plantaciones: List<PlantacionVisual>,
    hoy: LocalDate
): String {
    val sb = StringBuilder()
    val zone = ZoneId.of("Europe/Madrid")
    val fmtFecha = java.time.format.DateTimeFormatter.ofPattern("d MMM", java.util.Locale("es", "ES"))

    val largoTexto = if (bancal.largoCm % 100 == 0) "${bancal.largoCm / 100} m"
        else "%.1f m".format(bancal.largoCm / 100f)
    val anchoTexto = "${bancal.anchoCm} cm"
    val superficieM2 = (bancal.largoCm / 100f) * (bancal.anchoCm / 100f)

    // Cabecera
    sb.append("🌱 *${bancal.nombre}*\n")
    sb.append("📏 $largoTexto × $anchoTexto · ${"%.2f".format(superficieM2)} m²\n")

    if (plantaciones.isEmpty()) {
        // Si el bancal está en barbecho, mencionarlo
        val estado = when {
            bancal.tarpingDesde != null -> "🟫 En tarping (acolchado opaco)"
            bancal.abonoVerdeDesde != null -> "🌾 Con abono verde" +
                (bancal.abonoVerdeTipo?.let { " ($it)" } ?: "")
            else -> "Sin plantaciones activas."
        }
        sb.append("\n$estado\n")
    } else {
        val ocupadoCm = plantaciones.sumOf { it.plantacion.anchoCm }
        val porcentaje = if (bancal.largoCm > 0) ocupadoCm * 100 / bancal.largoCm else 0
        val totalPlantas = plantaciones.sumOf { pv ->
            val slots = if (pv.cultivo.marcoCm > 0) pv.plantacion.anchoCm / pv.cultivo.marcoCm else 1
            slots.coerceAtLeast(1) * pv.cultivo.lineasPorBancal.coerceAtLeast(1)
        }
        val familias = plantaciones.map { it.cultivo.familia }.toSet().size

        sb.append("📊 $porcentaje% ocupado · $totalPlantas plantas · ${plantaciones.size} plantaciones · $familias familias\n")

        // Resumen por estado
        val porEstado = plantaciones.groupingBy { it.plantacion.estado }.eachCount()
        if (porEstado.isNotEmpty()) {
            val estadoTxt = listOf(
                com.bancal.app.domain.model.EstadoPlantacion.SEMILLERO to "🌱 Semillero",
                com.bancal.app.domain.model.EstadoPlantacion.TRASPLANTADO to "🪴 Trasplantado",
                com.bancal.app.domain.model.EstadoPlantacion.CRECIENDO to "🌿 Creciendo",
                com.bancal.app.domain.model.EstadoPlantacion.COSECHANDO to "🧺 Cosechando"
            ).mapNotNull { (est, label) ->
                porEstado[est]?.let { "$label $it" }
            }
            if (estadoTxt.isNotEmpty()) {
                sb.append(estadoTxt.joinToString("  ·  "))
                sb.append("\n")
            }
        }

        // Cosechas próximas (≤ 21 días)
        val proximas = plantaciones
            .map { pv ->
                val fechaCos = java.time.Instant.ofEpochMilli(pv.plantacion.fechaCosechaEstimada)
                    .atZone(zone).toLocalDate()
                Triple(pv, fechaCos, ChronoUnit.DAYS.between(hoy, fechaCos).toInt())
            }
            .filter { it.third in 0..21 && it.first.plantacion.estado != com.bancal.app.domain.model.EstadoPlantacion.COSECHANDO }
            .sortedBy { it.third }
            .take(3)
        if (proximas.isNotEmpty()) {
            sb.append("\n📅 *Próximas cosechas*\n")
            for ((pv, fecha, dias) in proximas) {
                val cuando = when (dias) {
                    0 -> "hoy"
                    1 -> "mañana"
                    else -> "en $dias días (${fecha.format(fmtFecha)})"
                }
                sb.append("• ${pv.cultivo.icono} ${pv.cultivo.nombre} — $cuando\n")
            }
        }

        // Detalle de plantaciones: madres con hijas anidadas
        sb.append("\n🌿 *Plantaciones* (ordenadas por posición)\n")
        val byId = plantaciones.associateBy { it.plantacion.id }
        val hijasPorMadre = plantaciones
            .filter { it.plantacion.intercaladaCon != null }
            .groupBy { it.plantacion.intercaladaCon!! }
        val madresYSolas = plantaciones
            .filter { it.plantacion.intercaladaCon == null }
            .sortedBy { it.plantacion.posicionXCm }

        for (pv in madresYSolas) {
            sb.append(formatearLinea(pv, hoy, zone, prefix = "• "))
            hijasPorMadre[pv.plantacion.id]?.sortedBy { it.plantacion.posicionXCm }?.forEach { hija ->
                sb.append(formatearLinea(hija, hoy, zone, prefix = "   └ intercalado: "))
            }
        }

        // Hijas huérfanas (madre eliminada): listarlas aparte
        val huerfanas = plantaciones.filter {
            it.plantacion.intercaladaCon != null && byId[it.plantacion.intercaladaCon] == null
        }
        if (huerfanas.isNotEmpty()) {
            for (pv in huerfanas.sortedBy { it.plantacion.posicionXCm }) {
                sb.append(formatearLinea(pv, hoy, zone, prefix = "• "))
            }
        }

        // Estado del bancal (tarping / abono verde) si está activo
        if (bancal.tarpingDesde != null || bancal.abonoVerdeDesde != null) {
            sb.append("\n🧪 *Estado del bancal*\n")
            bancal.tarpingDesde?.let {
                val desde = java.time.Instant.ofEpochMilli(it).atZone(zone).toLocalDate()
                val dias = ChronoUnit.DAYS.between(desde, hoy).toInt().coerceAtLeast(0)
                sb.append("• Tarping activo desde ${desde.format(fmtFecha)} ($dias días)\n")
            }
            bancal.abonoVerdeDesde?.let {
                val desde = java.time.Instant.ofEpochMilli(it).atZone(zone).toLocalDate()
                val dias = ChronoUnit.DAYS.between(desde, hoy).toInt().coerceAtLeast(0)
                val tipo = bancal.abonoVerdeTipo?.let { t -> " ($t)" } ?: ""
                sb.append("• Abono verde$tipo desde ${desde.format(fmtFecha)} ($dias días)\n")
            }
        }
    }

    sb.append("\n— Bancal · huerto biointensivo")
    return sb.toString().trimEnd()
}

private fun formatearLinea(
    pv: PlantacionVisual,
    hoy: LocalDate,
    zone: ZoneId,
    prefix: String
): String {
    val p = pv.plantacion
    val c = pv.cultivo
    val slots = if (c.marcoCm > 0) (p.anchoCm / c.marcoCm).coerceAtLeast(1) else 1
    val totalPl = slots * c.lineasPorBancal.coerceAtLeast(1)
    val inicioM = p.posicionXCm / 100f
    val finM = (p.posicionXCm + p.anchoCm) / 100f
    val rango = "%.1f–%.1f m".format(inicioM, finM)
    val estado = p.estado.name.lowercase().replaceFirstChar { it.uppercase() }

    val fechaSiembra = java.time.Instant.ofEpochMilli(p.fechaSiembra).atZone(zone).toLocalDate()
    val diasSembrada = ChronoUnit.DAYS.between(fechaSiembra, hoy).toInt().coerceAtLeast(0)
    val fechaCos = java.time.Instant.ofEpochMilli(p.fechaCosechaEstimada).atZone(zone).toLocalDate()
    val diasACos = ChronoUnit.DAYS.between(hoy, fechaCos).toInt()

    val cosechaTxt = when {
        p.estado == com.bancal.app.domain.model.EstadoPlantacion.COSECHANDO -> "cosechando"
        diasACos < 0 -> "cosecha pasada"
        diasACos == 0 -> "cosecha hoy"
        else -> "cosecha en ${diasACos}d"
    }

    val plantasTxt = if (totalPl == 1) "1 planta" else "$totalPl plantas"
    return "$prefix${c.icono} ${c.nombre} · $rango · $plantasTxt · $estado · hace ${diasSembrada}d · $cosechaTxt\n"
}

@Composable
private fun StatPill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun InfoBanner(
    emoji: String,
    text: String,
    accent: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(accent.copy(alpha = 0.10f))
            .border(
                width = 1.dp,
                color = accent.copy(alpha = 0.25f),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(emoji, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = accent
        )
    }
}

@Composable
private fun ActionChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = { Text(label, style = MaterialTheme.typography.labelMedium) },
        leadingIcon = {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(AssistChipDefaults.IconSize)
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = AssistChipDefaults.assistChipBorder(
            enabled = true,
            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        )
    )
}

@Composable
private fun SectionHeader(title: String, count: Int? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (count != null) {
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                    .padding(horizontal = 8.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun EmptyBancalState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(vertical = 32.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "\uD83C\uDF31",
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Tu bancal está vacío",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Pulsa Plantar para empezar a sembrar",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HelperChip(riesgo: CalendarioEngine.RiesgoHelada) {
    val color = when (riesgo) {
        CalendarioEngine.RiesgoHelada.ALTO -> ErrorLight
        CalendarioEngine.RiesgoHelada.MEDIO -> WarningAmber
        CalendarioEngine.RiesgoHelada.BAJO -> WarningAmber.copy(alpha = 0.75f)
        CalendarioEngine.RiesgoHelada.NULO -> SuccessGreen
    }
    InfoBanner(
        emoji = "❄\uFE0F",
        text = "Riesgo de helada: ${riesgo.name.lowercase()}",
        accent = color
    )
}

@Composable
private fun PlantacionListItem(
    plantacionVisual: PlantacionVisual,
    onClick: () -> Unit
) {
    val p = plantacionVisual.plantacion
    val c = plantacionVisual.cultivo

    val zone = ZoneId.of("Europe/Madrid")
    val hoy = LocalDate.now()
    val fechaSiembra = java.time.Instant.ofEpochMilli(p.fechaSiembra).atZone(zone).toLocalDate()
    val fechaCosecha = java.time.Instant.ofEpochMilli(p.fechaCosechaEstimada).atZone(zone).toLocalDate()
    val diasDesdeSiembra = ChronoUnit.DAYS.between(fechaSiembra, hoy).toInt().coerceAtLeast(0)
    val diasACosecha = ChronoUnit.DAYS.between(hoy, fechaCosecha).toInt()
    val tiempoTexto = buildString {
        append(if (diasDesdeSiembra == 0) "hoy" else "hace ${diasDesdeSiembra}d")
        append(" · ")
        append(
            when {
                diasACosecha < 0 -> "lista hace ${-diasACosecha}d"
                diasACosecha == 0 -> "cosecha hoy"
                else -> "cosecha en ${diasACosecha}d"
            }
        )
    }

    val estadoColor = when (p.estado) {
        com.bancal.app.domain.model.EstadoPlantacion.SEMILLERO -> EstadoSemillero
        com.bancal.app.domain.model.EstadoPlantacion.TRASPLANTADO -> EstadoTrasplantado
        com.bancal.app.domain.model.EstadoPlantacion.CRECIENDO -> EstadoCreciendo
        com.bancal.app.domain.model.EstadoPlantacion.COSECHANDO -> EstadoCosechando
        com.bancal.app.domain.model.EstadoPlantacion.RETIRADO -> EstadoRetirado
    }

    androidx.compose.material3.Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Barra de acento del estado a la izquierda
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(estadoColor)
            )
            // Avatar circular con anillo de progreso
            Spacer(Modifier.width(12.dp))
            val totalDias = ChronoUnit.DAYS.between(fechaSiembra, fechaCosecha).toInt().coerceAtLeast(1)
            val progreso = (diasDesdeSiembra.toFloat() / totalDias).coerceIn(0f, 1f)
            val animatedProgress by animateFloatAsState(
                targetValue = progreso,
                animationSpec = tween(durationMillis = 500),
                label = "progress"
            )
            val trackColor = estadoColor.copy(alpha = 0.15f)
            val arcColor = estadoColor
            Box(
                modifier = Modifier.size(44.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(44.dp)) {
                    val strokeW = 3.dp.toPx()
                    val pad = strokeW / 2
                    val arcSize = Size(size.width - strokeW, size.height - strokeW)
                    // Track
                    drawArc(
                        color = trackColor,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = Offset(pad, pad),
                        size = arcSize,
                        style = Stroke(width = strokeW, cap = StrokeCap.Round)
                    )
                    // Progress
                    drawArc(
                        color = arcColor,
                        startAngle = -90f,
                        sweepAngle = animatedProgress * 360f,
                        useCenter = false,
                        topLeft = Offset(pad, pad),
                        size = arcSize,
                        style = Stroke(width = strokeW, cap = StrokeCap.Round)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(estadoColor.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(c.icono, style = MaterialTheme.typography.titleMedium)
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp, end = 8.dp)
            ) {
                Text(c.nombre, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = tiempoTexto,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(end = 12.dp)
            ) {
                EstadoBadge(
                    label = p.estado.name.lowercase().replaceFirstChar { it.uppercase() },
                    color = estadoColor
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${p.posicionXCm}cm",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun FadeDivider() {
    Spacer(Modifier.height(6.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Gray.copy(alpha = 0.18f),
                        Color.Gray.copy(alpha = 0.18f),
                        Color.Transparent
                    ),
                    startX = 0f,
                    endX = Float.POSITIVE_INFINITY
                )
            )
    )
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun EstadoBadge(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}
