package com.bancal.app.ui.screens.plantar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bancal.app.domain.logic.CalendarioEngine
import com.bancal.app.domain.model.TipoAsociacion
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.bancal.app.ui.components.BancalCanvas
import com.bancal.app.ui.components.CultivoChip
import com.bancal.app.ui.components.CultivoInfoRow
import com.bancal.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantarScreen(
    viewModel: PlantarViewModel,
    initialPosX: Int = 0,
    onBack: () -> Unit,
    onNavigateToCultivoPersonalizado: () -> Unit = {}
) {
    val cultivos by viewModel.cultivos.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCultivo by viewModel.selectedCultivo.collectAsState()
    val posicionX by viewModel.posicionX.collectAsState()
    val cantidad by viewModel.cantidad.collectAsState()
    val totalAncho by viewModel.totalAnchoCm.collectAsState()
    val cantidadMax by viewModel.cantidadMaxima.collectAsState()
    val recomendacion by viewModel.recomendacion.collectAsState()
    val asociaciones by viewModel.asociaciones.collectAsState()
    val zonaOcupada by viewModel.zonaOcupada.collectAsState()
    val intercalaciones by viewModel.intercalaciones.collectAsState()
    val intercalarCon by viewModel.intercalarCon.collectAsState()
    val solapaHermana by viewModel.solapaHermana.collectAsState()
    val calidadIntercalado by viewModel.calidadIntercalado.collectAsState()
    val avisoRotacion by viewModel.avisoRotacion.collectAsState()
    val plantado by viewModel.plantado.collectAsState()
    val plantacionesVisuales by viewModel.plantacionesVisuales.collectAsState()
    val huecos by viewModel.huecos.collectAsState()
    val bancal by viewModel.bancal.collectAsState()

    var soloTemporada by remember { mutableStateOf(false) }

    LaunchedEffect(initialPosX) {
        viewModel.setPosicionInicial(initialPosX)
    }

    LaunchedEffect(plantado) {
        if (plantado) {
            viewModel.resetPlantado()
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Plantar") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // === BUSCADOR ===
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearch(it) },
                    label = { Text("Buscar cultivo") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // === FILTRO DE TEMPORADA ===
            if (selectedCultivo == null) {
                item {
                    FilterChip(
                        selected = soloTemporada,
                        onClick = { soloTemporada = !soloTemporada },
                        label = { Text("Solo de temporada") },
                        leadingIcon = if (soloTemporada) {
                            { Icon(Icons.Default.Check, null, modifier = Modifier.size(FilterChipDefaults.IconSize)) }
                        } else null
                    )
                }
            }

            // === CREAR CULTIVO PERSONALIZADO ===
            if (selectedCultivo == null) {
                item {
                    OutlinedButton(
                        onClick = onNavigateToCultivoPersonalizado,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.NoteAdd, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Crear cultivo personalizado")
                    }
                }
            }

            // === LISTA DE CULTIVOS (si no hay seleccionado) ===
            if (selectedCultivo == null) {
                val cultivosFiltrados = if (soloTemporada) {
                    cultivos.filter { CalendarioEngine.evaluarSiembra(it).recomendado }
                } else {
                    cultivos
                }
                items(cultivosFiltrados, key = { it.id }) { cultivo ->
                    CultivoChip(
                        cultivo = cultivo,
                        onClick = { viewModel.selectCultivo(cultivo) }
                    )
                }
            }

            // === CULTIVO SELECCIONADO ===
            selectedCultivo?.let { cultivo ->
                // Card de info del cultivo
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(cultivo.icono, style = MaterialTheme.typography.headlineLarge)
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(cultivo.nombre, style = MaterialTheme.typography.headlineMedium)
                                    Text(
                                        cultivo.familia.name.lowercase().replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                TextButton(onClick = { viewModel.clearSelection() }) {
                                    Text("Cambiar")
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            CultivoInfoRow("Marco", "${cultivo.marcoCm} cm por planta · ${cultivo.lineasPorBancal} ${if (cultivo.lineasPorBancal == 1) "línea" else "líneas"}")
                            val fechaCosecha = CalendarioEngine.fechaCosechaEstimada(LocalDate.now(), cultivo)
                            val fmt = DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("es", "ES"))
                            CultivoInfoRow("Cosecha", "${cultivo.diasCosecha} días (≈ ${fechaCosecha.format(fmt)})")
                            CultivoInfoRow("Riego", cultivo.riego.name.lowercase())
                            val siembraTexto = buildList {
                                if (cultivo.admiteSiembraDirecta) add("siembra directa")
                                if (cultivo.admitePlantel) add("plantel")
                            }.joinToString(" / ")
                            if (siembraTexto.isNotEmpty()) {
                                CultivoInfoRow("Modo", siembraTexto)
                            }
                        }
                    }
                }

                // === RECOMENDACIÓN DE SIEMBRA ===
                recomendacion?.let { rec ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (rec.recomendado)
                                    SuccessGreen.copy(alpha = 0.1f)
                                else
                                    WarningAmber.copy(alpha = 0.1f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(if (rec.recomendado) "✅" else "⚠\uFE0F")
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        if (rec.recomendado) "Buena epoca" else "Fuera de temporada",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        rec.mensaje,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (rec.recomendado) {
                                        Text(
                                            "Tipo: ${rec.tipoSiembra.name.lowercase()}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // === SELECTOR DE CANTIDAD ===
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Cantidad", style = MaterialTheme.typography.titleMedium)
                        val cabenBancal = if (cultivo.marcoCm > 0)
                            (bancal.largoCm / cultivo.marcoCm) * cultivo.lineasPorBancal else 0
                        Text(
                            "caben hasta $cabenBancal en el bancal",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilledIconButton(
                                onClick = { viewModel.decrementarCantidad() },
                                enabled = cantidad > 1,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(Icons.Default.Remove, "Menos")
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                val totalPlantas = cantidad * cultivo.lineasPorBancal
                                Text(
                                    "$totalPlantas",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    if (totalPlantas == 1) "planta" else "plantas",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (cultivo.lineasPorBancal > 1) {
                                    Text(
                                        "$cantidad × ${cultivo.lineasPorBancal} líneas",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(Modifier.width(16.dp))
                            FilledIconButton(
                                onClick = { viewModel.incrementarCantidad() },
                                enabled = cantidad < cantidadMax,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(Icons.Default.Add, "Mas")
                            }
                            Spacer(Modifier.width(8.dp))
                            AssistChip(
                                onClick = { viewModel.setCantidad(1) },
                                label = { Text("1") }
                            )
                            Spacer(Modifier.width(4.dp))
                            AssistChip(
                                onClick = { viewModel.setCantidadMaxima() },
                                label = { Text("máx") }
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "${totalAncho} cm",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "$cantidad × ${cultivo.marcoCm}cm",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // === POSICIÓN EN EL BANCAL — CANVAS VISUAL ===
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Posicion en el bancal", style = MaterialTheme.typography.titleMedium)
                        AssistChip(
                            onClick = { viewModel.autoPosition() },
                            label = { Text("Auto") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.AutoFixHigh, null,
                                    modifier = Modifier.size(AssistChipDefaults.IconSize)
                                )
                            }
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Toca el bancal para elegir posicion",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))

                    // Canvas con preview
                    BancalCanvas(
                        plantaciones = plantacionesVisuales,
                        bancalLargoCm = bancal.largoCm,
                        onTapZonaVacia = { posX -> viewModel.updatePosicion(posX) },
                        onTapPlantacion = { p -> viewModel.updatePosicion(p.posicionXCm) },
                        modifier = Modifier.fillMaxWidth(),
                        previewPosXCm = posicionX,
                        previewAnchoCm = totalAncho,
                        previewEmoji = cultivo.icono,
                        previewOcupado = (intercalarCon == null && zonaOcupada) ||
                            (intercalarCon != null && solapaHermana)
                    )

                    Spacer(Modifier.height(8.dp))

                    // Info de posición + botones de ajuste fino
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.ajustarPosicion(-10) },
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Text("-10cm")
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "${posicionX}cm",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${String.format("%.1f", posicionX / 100f)}m — ${String.format("%.1f", (posicionX + totalAncho) / 100f)}m",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        OutlinedButton(
                            onClick = { viewModel.ajustarPosicion(10) },
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Text("+10cm")
                        }
                    }

                    if (intercalarCon != null && solapaHermana) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "❌ Esta zona pisa a otra planta intercalada",
                            style = MaterialTheme.typography.bodySmall,
                            color = ErrorLight
                        )
                    } else if (zonaOcupada && intercalarCon == null && intercalaciones.isEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "❌ Esta zona ya esta ocupada",
                            style = MaterialTheme.typography.bodySmall,
                            color = ErrorLight
                        )
                    } else if (zonaOcupada && intercalarCon == null && intercalaciones.isNotEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Zona ocupada — puedes intercalar (ver abajo)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // === HUECOS DISPONIBLES ===
                val huecosValidos = huecos.filter { it.anchoCm >= cultivo.marcoCm }
                if (huecosValidos.isNotEmpty()) {
                    item {
                        Text("Huecos disponibles", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(huecosValidos) { hueco ->
                                val cabenPlantas = (hueco.anchoCm / cultivo.marcoCm) * cultivo.lineasPorBancal
                                val seleccionado = posicionX >= hueco.startCm
                                        && posicionX < hueco.startCm + hueco.anchoCm
                                FilterChip(
                                    selected = seleccionado,
                                    onClick = { viewModel.seleccionarHueco(hueco) },
                                    label = {
                                        Text(
                                            "${hueco.anchoCm}cm (${cabenPlantas} pl.) — ${String.format("%.1f", hueco.startCm / 100f)}m",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                // === ASOCIACIONES ===
                if (asociaciones.isNotEmpty()) {
                    item {
                        Text("Vecinos", style = MaterialTheme.typography.titleMedium)
                    }
                    items(asociaciones) { asoc ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                if (asoc.tipo == TipoAsociacion.BENEFICIOSA) "✅" else "❌"
                            )
                            Spacer(Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "${asoc.cultivoVecino.icono} ${asoc.cultivoVecino.nombre}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    asoc.motivo,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                "${asoc.distanciaCm}cm",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // === INTERCALADO ===
                if (intercalaciones.isNotEmpty()) {
                    item {
                        Text("Intercalar cultivo", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Selecciona una plantación para intercalar con ella. Podrás ajustar cantidad y posición dentro de la madre.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    items(intercalaciones) { sug ->
                        val seleccionado = intercalarCon?.id == sug.plantacionMadre.id
                        Card(
                            onClick = {
                                viewModel.seleccionarIntercalacion(
                                    if (seleccionado) null else sug.plantacionMadre
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (seleccionado)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(sug.cultivoMadre.icono, style = MaterialTheme.typography.headlineSmall)
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Con ${sug.cultivoMadre.nombre}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        sug.motivo,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (seleccionado) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                    // Info de calidad agronómica cuando hay madre seleccionada
                    calidadIntercalado?.let { eval ->
                        item {
                            val (emoji, color) = when (eval.calidad) {
                                com.bancal.app.domain.logic.AsociacionEngine.CalidadIntercalado.IDEAL ->
                                    "✅" to SuccessGreen
                                com.bancal.app.domain.logic.AsociacionEngine.CalidadIntercalado.MAL_EMPAREJADO ->
                                    "⚠\uFE0F" to WarningAmber
                                com.bancal.app.domain.logic.AsociacionEngine.CalidadIntercalado.ACEPTABLE ->
                                    "\uD83D\uDCA1" to MaterialTheme.colorScheme.primary
                            }
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = color.copy(alpha = 0.1f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(emoji)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        eval.mensaje,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = color
                                    )
                                }
                            }
                        }
                    }
                }

                // === AVISO DE ROTACIÓN ===
                avisoRotacion?.let { aviso ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = WarningAmber.copy(alpha = 0.1f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("\uD83D\uDD04")
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        "Rotacion recomendada",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        aviso.mensaje,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // === BOTÓN PLANTAR ===
                item {
                    Spacer(Modifier.height(8.dp))
                    val puedeIntercalar = intercalarCon != null
                    val puedePlantar = if (puedeIntercalar) !solapaHermana else !zonaOcupada
                    Button(
                        onClick = { viewModel.plantar() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = puedePlantar
                    ) {
                        Icon(Icons.Default.Check, null)
                        Spacer(Modifier.width(8.dp))
                        val totalPlantas = cantidad * cultivo.lineasPorBancal
                        Text(
                            if (puedeIntercalar) {
                                if (totalPlantas == 1) "Intercalar ${cultivo.nombre}"
                                else "Intercalar $totalPlantas × ${cultivo.nombre} (${totalAncho}cm)"
                            }
                            else if (totalPlantas == 1) "Plantar ${cultivo.nombre}"
                            else "Plantar $totalPlantas × ${cultivo.nombre} (${totalAncho}cm)"
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}
