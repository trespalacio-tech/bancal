package com.bancal.app.ui.screens.tratamientos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bancal.app.domain.model.TipoTratamiento
import com.bancal.app.ui.theme.ErrorLight
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private fun TipoTratamiento.displayName(): String = when (this) {
    TipoTratamiento.RIEGO -> "Riego"
    TipoTratamiento.ABONO -> "Abono"
    TipoTratamiento.FUNGICIDA -> "Fungi"
    TipoTratamiento.INSECTICIDA -> "Insect"
    TipoTratamiento.PODA -> "Poda"
    TipoTratamiento.ACOLCHADO -> "Acolch"
    TipoTratamiento.TE_COMPOST -> "Té comp"
    TipoTratamiento.DESHERBADO -> "Deshierb"
    TipoTratamiento.OTRO -> "Otro"
}

@Composable
fun TratamientosScreen(viewModel: TratamientosViewModel) {
    val tratamientos by viewModel.tratamientos.collectAsState()
    val showDialog by viewModel.showDialog.collectAsState()
    val plantaciones by viewModel.plantacionesActivas.collectAsState()

    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    val zone = ZoneId.of("Europe/Madrid")

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Spacer(Modifier.height(16.dp))
                Text("Tratamientos", style = MaterialTheme.typography.headlineLarge)
                Text(
                    "Historial y registro de tratamientos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
            }

            // Sugerencias rápidas
            item {
                Text("Productos ecológicos sugeridos", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                SugerenciaChip("\uD83C\uDF3F Purín de ortiga", "Fortalecedor general, cada 15 días")
                SugerenciaChip("\uD83E\uDDEA Caldo bordelés", "Fungicida preventivo, tras lluvias")
                SugerenciaChip("\uD83E\uDD1B Jabón potásico", "Contra pulgón y mosca blanca")
                SugerenciaChip("\uD83C\uDF3E Compost maduro", "Abono base, al plantar")
                SugerenciaChip("\uD83E\uDEB5 Acolchado de hierba segada", "Retiene humedad, aporta nutrientes, evita adventicias")
                SugerenciaChip("\u2615 Té de compost", "Foliar c/7-14 días, suelo c/15-30 días")
                Spacer(Modifier.height(12.dp))
            }

            item {
                Text(
                    "Historial (${tratamientos.size})",
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
                items(tratamientos, key = { it.tratamiento.id }) { tc ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "${tc.tratamiento.tipo.displayName()} — ${tc.tratamiento.producto}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                if (tc.cultivo != null) {
                                    Text(
                                        "${tc.cultivo.icono} ${tc.cultivo.nombre}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                } else {
                                    Text(
                                        "Bancal completo",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (tc.tratamiento.dosis.isNotBlank()) {
                                    Text(
                                        "Dosis: ${tc.tratamiento.dosis}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    Instant.ofEpochMilli(tc.tratamiento.fecha).atZone(zone).format(formatter),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                            IconButton(onClick = { viewModel.deleteTratamiento(tc.tratamiento) }) {
                                Icon(Icons.Default.Delete, "Eliminar", tint = ErrorLight.copy(alpha = 0.6f))
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }

        FloatingActionButton(
            onClick = { viewModel.showAddDialog() },
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, "Añadir tratamiento")
        }
    }

    if (showDialog) {
        AddTratamientoDialog(
            plantaciones = plantaciones,
            onDismiss = { viewModel.hideDialog() },
            onConfirm = { plantacionId, tipo, producto, dosis, notas ->
                viewModel.addTratamiento(plantacionId, tipo, producto, dosis, notas)
            }
        )
    }
}

@Composable
private fun SugerenciaChip(titulo: String, descripcion: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(titulo, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.width(8.dp))
        Text(
            descripcion,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTratamientoDialog(
    plantaciones: List<com.bancal.app.data.db.entity.PlantacionEntity>,
    onDismiss: () -> Unit,
    onConfirm: (Long?, TipoTratamiento, String, String, String) -> Unit
) {
    var selectedPlantacionId by remember { mutableStateOf<Long?>(null) }
    var selectedTipo by remember { mutableStateOf(TipoTratamiento.ABONO) }
    var producto by remember { mutableStateOf("") }
    var dosis by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var plantacionExpanded by remember { mutableStateOf(false) }

    val plantacionLabel = if (selectedPlantacionId == null) "Bancal completo"
    else plantaciones.find { it.id == selectedPlantacionId }
        ?.let { "Pos. ${it.posicionXCm}cm" } ?: "Bancal completo"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo tratamiento") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Destino
                Text("Aplicar a", style = MaterialTheme.typography.labelMedium)
                ExposedDropdownMenuBox(
                    expanded = plantacionExpanded,
                    onExpandedChange = { plantacionExpanded = it }
                ) {
                    OutlinedTextField(
                        value = plantacionLabel,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = plantacionExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        singleLine = true
                    )
                    ExposedDropdownMenu(
                        expanded = plantacionExpanded,
                        onDismissRequest = { plantacionExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Bancal completo") },
                            onClick = {
                                selectedPlantacionId = null
                                plantacionExpanded = false
                            }
                        )
                        plantaciones.forEach { p ->
                            DropdownMenuItem(
                                text = { Text("Pos. ${p.posicionXCm}cm (${p.anchoCm}cm)") },
                                onClick = {
                                    selectedPlantacionId = p.id
                                    plantacionExpanded = false
                                }
                            )
                        }
                    }
                }

                // Tipo
                Text("Tipo", style = MaterialTheme.typography.labelMedium)
                @OptIn(ExperimentalLayoutApi::class)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TipoTratamiento.entries.forEach { tipo ->
                        FilterChip(
                            selected = selectedTipo == tipo,
                            onClick = { selectedTipo = tipo },
                            label = { Text(tipo.displayName(), style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                OutlinedTextField(
                    value = producto,
                    onValueChange = { producto = it },
                    label = { Text("Producto") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = dosis,
                    onValueChange = { dosis = it },
                    label = { Text("Dosis (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = notas,
                    onValueChange = { notas = it },
                    label = { Text("Notas (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(selectedPlantacionId, selectedTipo, producto, dosis, notas) },
                enabled = producto.isNotBlank()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
