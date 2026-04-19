package com.bancal.app.ui.screens.sucesion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bancal.app.domain.logic.SucesionEngine
import com.bancal.app.ui.theme.*
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SucesionScreen(
    viewModel: SucesionViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val formatter = DateTimeFormatter.ofPattern("dd MMM", Locale("es", "ES"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Siembra escalonada") },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Explicación
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Cosecha continua",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "La siembra escalonada consiste en re-sembrar un mismo cultivo cada ciertos dias para obtener cosechas continuas en vez de un exceso puntual. " +
                                    "El relevo de cultivos aprovecha cada zona que se libera plantando algo nuevo, respetando la rotacion por familias.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // === SIEMBRAS ESCALONADAS ===
            item {
                Spacer(Modifier.height(4.dp))
                Text(
                    "Proximas siembras escalonadas",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (state.escalonadas.isEmpty()) {
                item {
                    Text(
                        "No hay siembras escalonadas pendientes. Planta lechuga, rabano, espinaca u otros cultivos de ciclo corto para activar esta funcion.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(state.escalonadas) { se ->
                    EscalonadaCard(se, formatter)
                }
            }

            // === RELEVOS ===
            if (state.relevos.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Relevo de cultivos",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Zonas proximas a liberarse y que plantar a continuacion",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                for ((plantacion, sugerencias) in state.relevos) {
                    val cultivoActual = state.cultivoMap[plantacion.cultivoId]
                    item {
                        Spacer(Modifier.height(4.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = WarningAmber.copy(alpha = 0.08f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    "${cultivoActual?.icono ?: ""} ${cultivoActual?.nombre ?: "?"} — Pos. ${plantacion.posicionXCm}cm",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    "Espacio: ${plantacion.anchoCm}cm · Familia: ${cultivoActual?.familia?.name?.lowercase()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    items(sugerencias) { sug ->
                        SugerenciaRelevoRow(sug)
                    }
                }
            }

            // Info cultivos escalonables
            item {
                Spacer(Modifier.height(8.dp))
                Text("Cultivos escalonables", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Cultivos que se benefician de siembra escalonada",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
            }

            val escalonables = state.cultivoMap.values
                .filter { it.intervaloSucesionDias > 0 }
                .sortedBy { it.intervaloSucesionDias }

            items(escalonables.toList()) { cultivo ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(cultivo.icono, style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(cultivo.nombre, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "${cultivo.diasCosecha}d hasta cosecha",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        "cada ${cultivo.intervaloSucesionDias}d",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun EscalonadaCard(
    se: SucesionEngine.SiembraEscalonada,
    formatter: DateTimeFormatter
) {
    val urgencia = when {
        se.diasRestantes <= 0 -> ErrorLight
        se.diasRestantes <= 3 -> WarningAmber
        else -> SuccessGreen
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = urgencia.copy(alpha = 0.08f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(se.cultivo.icono, style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    se.cultivo.nombre,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "Cada ${se.cultivo.intervaloSucesionDias} dias · ${se.siembrasRestantesEnTemporada} siembras mas esta temporada",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    when {
                        se.diasRestantes <= 0 -> "HOY"
                        se.diasRestantes == 1L -> "manana"
                        else -> "en ${se.diasRestantes}d"
                    },
                    style = MaterialTheme.typography.titleSmall,
                    color = urgencia,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    se.proximaSiembra.format(formatter),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SugerenciaRelevoRow(sug: SucesionEngine.SugerenciaRelevo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            if (sug.llegaAntesDeLaHelada) "✅" else "⚠\uFE0F"
        )
        Spacer(Modifier.width(8.dp))
        Text(sug.cultivo.icono, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(sug.cultivo.nombre, style = MaterialTheme.typography.bodyMedium)
            Text(
                sug.motivo,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (!sug.llegaAntesDeLaHelada) {
            Text(
                "riesgo helada",
                style = MaterialTheme.typography.labelSmall,
                color = ErrorLight
            )
        }
    }
}
