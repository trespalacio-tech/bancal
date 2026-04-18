package com.bancal.app.ui.screens.calendario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bancal.app.domain.logic.CalendarioEngine
import com.bancal.app.domain.model.TipoSiembra

@Composable
fun CalendarioScreen(viewModel: CalendarioViewModel) {
    val mesSeleccionado by viewModel.mesSeleccionado.collectAsState()
    val cultivosPorTipo by viewModel.cultivosPorTipo.collectAsState()
    val riesgo = CalendarioEngine.riesgoHelada(
        java.time.LocalDate.of(java.time.LocalDate.now().year, mesSeleccionado, 15)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Spacer(Modifier.height(16.dp))
            Text(
                "Calendario de siembra",
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                "Adaptado al clima de Burgos",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
        }

        // Selector de mes
        item {
            ScrollableTabRow(
                selectedTabIndex = mesSeleccionado - 1,
                edgePadding = 0.dp
            ) {
                (1..12).forEach { mes ->
                    Tab(
                        selected = mesSeleccionado == mes,
                        onClick = { viewModel.selectMes(mes) },
                        text = {
                            Text(
                                CalendarioEngine.nombreMes(mes).take(3),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    )
                }
            }
        }

        // Info del mes
        item {
            Spacer(Modifier.height(8.dp))
            Text(
                CalendarioEngine.nombreMes(mesSeleccionado),
                style = MaterialTheme.typography.headlineMedium
            )
            if (riesgo != CalendarioEngine.RiesgoHelada.NULO) {
                Text(
                    "❄\uFE0F Riesgo de helada: ${riesgo.name.lowercase()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        // Semillero
        val semillero = cultivosPorTipo[TipoSiembra.SEMILLERO] ?: emptyList()
        if (semillero.isNotEmpty()) {
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    "\uD83C\uDF31 Semillero (${semillero.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            items(semillero) { cultivo ->
                CalendarioCultivoItem(
                    emoji = cultivo.icono,
                    nombre = cultivo.nombre,
                    detalle = "Prof. ${cultivo.profundidadSiembraCm}cm · Germ. ${cultivo.diasGerminacion}d"
                )
            }
        }

        // Siembra directa
        val directa = cultivosPorTipo[TipoSiembra.DIRECTA] ?: emptyList()
        if (directa.isNotEmpty()) {
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    "\uD83C\uDF3E Siembra directa (${directa.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            items(directa) { cultivo ->
                CalendarioCultivoItem(
                    emoji = cultivo.icono,
                    nombre = cultivo.nombre,
                    detalle = "Marco ${cultivo.marcoCm}cm · Prof. ${cultivo.profundidadSiembraCm}cm"
                )
            }
        }

        // Trasplante
        val trasplante = cultivosPorTipo[TipoSiembra.TRASPLANTE] ?: emptyList()
        if (trasplante.isNotEmpty()) {
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    "\uD83C\uDF3F Trasplante (${trasplante.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            items(trasplante) { cultivo ->
                CalendarioCultivoItem(
                    emoji = cultivo.icono,
                    nombre = cultivo.nombre,
                    detalle = "Marco ${cultivo.marcoCm}cm · ${cultivo.diasCosecha}d hasta cosecha"
                )
            }
        }

        // Mes vacío
        if (semillero.isEmpty() && directa.isEmpty() && trasplante.isEmpty()) {
            item {
                Spacer(Modifier.height(32.dp))
                Text(
                    "No hay cultivos recomendados para este mes",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun CalendarioCultivoItem(emoji: String, nombre: String, detalle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Text(emoji, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(nombre, style = MaterialTheme.typography.bodyLarge)
            Text(
                detalle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
