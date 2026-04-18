package com.bancal.app.ui.screens.diario

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.bancal.app.data.db.entity.DiarioEntity
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiarioScreen(
    viewModel: DiarioViewModel,
    onBack: () -> Unit
) {
    val entradas by viewModel.entradas.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val showForm by viewModel.showForm.collectAsState()
    val editando by viewModel.editando.collectAsState()
    val fotoPath by viewModel.fotoPath.collectAsState()

    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    val zone = ZoneId.of("Europe/Madrid")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cuaderno de campo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.mostrarFormularioHoy() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Entrada de hoy")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Stats summary
            item {
                StatsCard(stats)
            }

            if (entradas.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📓", style = MaterialTheme.typography.headlineLarge)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Sin entradas todavía",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Pulsa + para registrar el día de hoy",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                item {
                    Text(
                        "Últimas entradas",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                items(entradas, key = { it.id }) { entrada ->
                    EntradaCard(
                        entrada = entrada,
                        formatter = formatter,
                        zone = zone,
                        onEdit = { viewModel.editarEntrada(entrada) },
                        onDelete = { viewModel.eliminar(entrada) }
                    )
                }
            }
        }
    }

    // Form dialog
    if (showForm && editando != null) {
        DiarioFormDialog(
            entrada = editando!!,
            fotoPath = fotoPath,
            formatter = formatter,
            zone = zone,
            onDismiss = { viewModel.cerrarFormulario() },
            onSave = { tempMin, tempMax, lluvia, helada, tareas, obs, cosechaKg, cosechaNotas ->
                viewModel.guardar(tempMin, tempMax, lluvia, helada, tareas, obs, cosechaKg, cosechaNotas)
            },
            onCrearUriParaFoto = { viewModel.crearUriParaFoto() },
            onFotoCapturada = { viewModel.onFotoCapturada(it) },
            onFotoSeleccionada = { viewModel.onFotoSeleccionada(it) },
            onEliminarFoto = { viewModel.eliminarFoto() }
        )
    }
}

@Composable
private fun StatsCard(stats: DiarioStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Resumen",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("Entradas", "${stats.entradas}")
                StatItem("Heladas", "${stats.diasHelada}")
                StatItem("Cosecha", stats.totalCosechaKg?.let { "%.1f kg".format(it) } ?: "—")
                StatItem("Lluvia", stats.totalLluviaMm?.let { "%.0f mm".format(it) } ?: "—")
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun EntradaCard(
    entrada: DiarioEntity,
    formatter: DateTimeFormatter,
    zone: ZoneId,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val fecha = Instant.ofEpochMilli(entrada.fecha).atZone(zone).toLocalDate()
    val esHoy = fecha == LocalDate.now()
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        if (esHoy) "Hoy" else fecha.format(formatter),
                        style = MaterialTheme.typography.titleSmall
                    )
                    if (esHoy) {
                        Text(
                            fecha.format(formatter),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Weather row
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (entrada.tempMin != null || entrada.tempMax != null) {
                    Text(
                        "🌡 ${entrada.tempMin ?: "—"}° / ${entrada.tempMax ?: "—"}°",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (entrada.lluviaMm != null) {
                    Text(
                        "🌧 %.1f mm".format(entrada.lluviaMm),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (entrada.helada) {
                    Text("❄ Helada", style = MaterialTheme.typography.bodySmall)
                }
            }

            if (entrada.tareas.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "Tareas: ${entrada.tareas}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )
            }

            if (entrada.observaciones.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    entrada.observaciones,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            if (entrada.cosechaKg != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "🥬 %.1f kg${if (entrada.cosechaNotas.isNotBlank()) " — ${entrada.cosechaNotas}" else ""}".format(entrada.cosechaKg),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Photo thumbnail
            if (entrada.fotoPath != null && File(entrada.fotoPath).exists()) {
                Spacer(Modifier.height(8.dp))
                Image(
                    painter = rememberAsyncImagePainter(File(entrada.fotoPath)),
                    contentDescription = "Foto del día",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Eliminar entrada") },
            text = { Text("¿Eliminar esta entrada del cuaderno?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    onDelete()
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DiarioFormDialog(
    entrada: DiarioEntity,
    fotoPath: String?,
    formatter: DateTimeFormatter,
    zone: ZoneId,
    onDismiss: () -> Unit,
    onSave: (Int?, Int?, Float?, Boolean, String, String, Float?, String) -> Unit,
    onCrearUriParaFoto: () -> Uri,
    onFotoCapturada: (Boolean) -> Unit,
    onFotoSeleccionada: (Uri?) -> Unit,
    onEliminarFoto: () -> Unit
) {
    val fecha = Instant.ofEpochMilli(entrada.fecha).atZone(zone).toLocalDate()

    var tempMin by remember { mutableStateOf(entrada.tempMin?.toString() ?: "") }
    var tempMax by remember { mutableStateOf(entrada.tempMax?.toString() ?: "") }
    var lluvia by remember { mutableStateOf(entrada.lluviaMm?.toString() ?: "") }
    var helada by remember { mutableStateOf(entrada.helada) }
    var tareas by remember { mutableStateOf(entrada.tareas) }
    var observaciones by remember { mutableStateOf(entrada.observaciones) }
    var cosechaKg by remember { mutableStateOf(entrada.cosechaKg?.toString() ?: "") }
    var cosechaNotas by remember { mutableStateOf(entrada.cosechaNotas) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        onFotoCapturada(success)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        onFotoSeleccionada(uri)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (entrada.id == 0L) "Nueva entrada — ${fecha.format(formatter)}"
                else "Editar — ${fecha.format(formatter)}"
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Temperatures
                Text("Temperatura", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = tempMin,
                        onValueChange = { tempMin = it },
                        label = { Text("Mín °C") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = tempMax,
                        onValueChange = { tempMax = it },
                        label = { Text("Máx °C") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                // Rain + frost
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = lluvia,
                        onValueChange = { lluvia = it },
                        label = { Text("Lluvia mm") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = helada,
                            onCheckedChange = { helada = it }
                        )
                        Text("Helada", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                HorizontalDivider()

                // Tasks
                OutlinedTextField(
                    value = tareas,
                    onValueChange = { tareas = it },
                    label = { Text("Tareas realizadas") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )

                // Observations
                OutlinedTextField(
                    value = observaciones,
                    onValueChange = { observaciones = it },
                    label = { Text("Observaciones") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )

                HorizontalDivider()

                // Harvest
                Text("Cosecha", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = cosechaKg,
                        onValueChange = { cosechaKg = it },
                        label = { Text("Kg") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = cosechaNotas,
                        onValueChange = { cosechaNotas = it },
                        label = { Text("Notas cosecha") },
                        modifier = Modifier.weight(2f),
                        singleLine = true
                    )
                }

                HorizontalDivider()

                // Photo section
                Text("Foto", style = MaterialTheme.typography.labelLarge)

                if (fotoPath != null && File(fotoPath).exists()) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Image(
                            painter = rememberAsyncImagePainter(File(fotoPath)),
                            contentDescription = "Foto del día",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = onEliminarFoto,
                            modifier = Modifier.align(Alignment.TopEnd),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Eliminar foto",
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                val uri = onCrearUriParaFoto()
                                cameraLauncher.launch(uri)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Cámara")
                        }
                        OutlinedButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Photo, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Galería")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    tempMin.toIntOrNull(),
                    tempMax.toIntOrNull(),
                    lluvia.replace(",", ".").toFloatOrNull(),
                    helada,
                    tareas,
                    observaciones,
                    cosechaKg.replace(",", ".").toFloatOrNull(),
                    cosechaNotas
                )
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
