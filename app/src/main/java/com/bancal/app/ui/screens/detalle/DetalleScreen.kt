package com.bancal.app.ui.screens.detalle

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material3.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bancal.app.domain.model.EstadoPlantacion
import com.bancal.app.domain.model.TipoSiembra
import com.bancal.app.ui.components.CultivoInfoRow
import com.bancal.app.ui.theme.*
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    viewModel: DetalleViewModel,
    plantacionId: Long,
    onBack: () -> Unit
) {
    val plantacion by viewModel.plantacion.collectAsState()
    val cultivo by viewModel.cultivo.collectAsState()
    val companeros by viewModel.companeros.collectAsState()
    val tratamientos by viewModel.tratamientos.collectAsState()
    val deleted by viewModel.deleted.collectAsState()
    val cosechaRegistrada by viewModel.cosechaRegistrada.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showCosechaDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(plantacionId) { viewModel.loadPlantacion(plantacionId) }
    LaunchedEffect(deleted) { if (deleted) onBack() }
    LaunchedEffect(cosechaRegistrada) {
        if (cosechaRegistrada) {
            snackbarHostState.showSnackbar("Cosecha registrada en el cuaderno")
            viewModel.resetCosechaRegistrada()
        }
    }

    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("es", "ES"))
    val zone = ZoneId.of("Europe/Madrid")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(cultivo?.let { "${it.icono} ${it.nombre}" } ?: "Detalle") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showCosechaDialog = true }) {
                        Icon(Icons.Default.Agriculture, "Registrar cosecha")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, "Eliminar", tint = ErrorLight)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        plantacion?.let { p ->
            cultivo?.let { c ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Info básica
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                val slotsX = if (c.marcoCm > 0) (p.anchoCm / c.marcoCm).coerceAtLeast(1) else 1
                                val totalPlantas = slotsX * c.lineasPorBancal.coerceAtLeast(1)
                                CultivoInfoRow("Estado", p.estado.name.lowercase().replaceFirstChar { it.uppercase() })
                                CultivoInfoRow("Tipo de siembra", p.tipoSiembra.name.lowercase().replaceFirstChar { it.uppercase() })
                                CultivoInfoRow("Posición", formatCm(p.posicionXCm))
                                CultivoInfoRow("Espacio", formatCm(p.anchoCm))
                                CultivoInfoRow("Plantas", "$totalPlantas")
                                CultivoInfoRow(
                                    "Fecha de siembra",
                                    Instant.ofEpochMilli(p.fechaSiembra).atZone(zone).format(formatter)
                                )
                                p.fechaTrasplanteEstimada?.let {
                                    CultivoInfoRow(
                                        "Trasplante estimado",
                                        Instant.ofEpochMilli(it).atZone(zone).format(formatter)
                                    )
                                }
                                CultivoInfoRow(
                                    "Cosecha estimada",
                                    Instant.ofEpochMilli(p.fechaCosechaEstimada).atZone(zone).format(formatter)
                                )
                            }
                        }
                    }

                    // Compañeros intercalados (madre + hermanas si es hija, o todas las hijas si es madre)
                    if (companeros.isNotEmpty()) {
                        item {
                            val soyHija = p.intercaladaCon != null
                            val titulo = if (soyHija) "Intercalado con" else "Intercalados en esta plantación"
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        "\uD83D\uDD17 $titulo",
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    for (comp in companeros) {
                                        Row(
                                            modifier = Modifier.padding(vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                comp.cultivo.icono,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            Spacer(Modifier.width(8.dp))
                                            val etiquetaRol = when (comp.rol) {
                                                com.bancal.app.ui.screens.detalle.DetalleViewModel.Companero.Rol.MADRE -> "madre"
                                                com.bancal.app.ui.screens.detalle.DetalleViewModel.Companero.Rol.HERMANA -> "hermana"
                                                com.bancal.app.ui.screens.detalle.DetalleViewModel.Companero.Rol.HIJA -> "hija"
                                            }
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    comp.cultivo.nombre,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                Text(
                                                    "$etiquetaRol · ${formatCm(comp.plantacion.posicionXCm)} · ${formatCm(comp.plantacion.anchoCm)} de ancho",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Cambiar estado
                    item {
                        Text("Cambiar estado", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            EstadoPlantacion.entries.forEach { estado ->
                                FilterChip(
                                    selected = p.estado == estado,
                                    onClick = { viewModel.updateEstado(estado) },
                                    label = {
                                        Text(
                                            estado.name.lowercase().replaceFirstChar { it.uppercase() },
                                            style = MaterialTheme.typography.labelSmall,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                )
                            }
                        }
                    }

                    // Info del cultivo
                    item {
                        Text("Sobre ${c.nombre}", style = MaterialTheme.typography.titleMedium)
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                CultivoInfoRow("Familia", c.familia.name.lowercase().replaceFirstChar { it.uppercase() })
                                CultivoInfoRow("Marco", "${c.marcoCm} cm")
                                CultivoInfoRow("Riego", c.riego.name.lowercase().replaceFirstChar { it.uppercase() })
                                CultivoInfoRow("Temp. mínima", "${c.temperaturaMinima}°C")
                                CultivoInfoRow("Temp. óptima", "${c.temperaturaOptima}°C")
                                if (c.notas.isNotBlank()) {
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        "💡 ${c.notas}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Tratamientos
                    item {
                        Text(
                            "Tratamientos (${tratamientos.size})",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    if (tratamientos.isEmpty()) {
                        item {
                            Text(
                                "Sin tratamientos registrados",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(tratamientos) { t ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        "${t.tipo.name.lowercase().replaceFirstChar { it.uppercase() }} — ${t.producto}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        Instant.ofEpochMilli(t.fecha).atZone(zone).format(formatter),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }

    if (showCosechaDialog) {
        var kgTexto by remember { mutableStateOf("") }
        val kgValido = kgTexto.replace(",", ".").toFloatOrNull()?.let { it > 0f } == true
        AlertDialog(
            onDismissRequest = { showCosechaDialog = false },
            title = { Text("Registrar cosecha") },
            text = {
                Column {
                    Text(
                        "Se añadirá al cuaderno de campo de hoy. Puedes registrar varias cosechas — los kilos se acumulan.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = kgTexto,
                        onValueChange = { kgTexto = it },
                        label = { Text("Kilos cosechados") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val kg = kgTexto.replace(",", ".").toFloatOrNull() ?: 0f
                        viewModel.registrarCosecha(kg)
                        showCosechaDialog = false
                    },
                    enabled = kgValido
                ) { Text("Registrar") }
            },
            dismissButton = {
                TextButton(onClick = { showCosechaDialog = false }) { Text("Cancelar") }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar plantación") },
            text = { Text("¿Eliminar esta plantación y todos sus tratamientos?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deletePlantacion()
                    showDeleteDialog = false
                }) {
                    Text("Eliminar", color = ErrorLight)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

private fun formatCm(cm: Int): String {
    if (cm < 100) return "$cm cm"
    if (cm % 100 == 0) return "${cm / 100} m"
    val entero = cm / 100
    val decimales = cm % 100
    return if (decimales % 10 == 0) "$entero,${decimales / 10} m"
    else "$entero,${decimales.toString().padStart(2, '0')} m"
}
