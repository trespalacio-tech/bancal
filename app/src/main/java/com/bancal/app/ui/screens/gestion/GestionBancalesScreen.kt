package com.bancal.app.ui.screens.gestion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.BorderStroke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionBancalesScreen(
    viewModel: GestionBancalesViewModel,
    onBack: () -> Unit
) {
    val bancales by viewModel.bancalesConInfo.collectAsState()
    val selectedId by viewModel.selectedId.collectAsState()
    val error by viewModel.error.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editando by remember { mutableStateOf<BancalConInfo?>(null) }
    var eliminando by remember { mutableStateOf<BancalConInfo?>(null) }

    // Mostrar error como snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Bancales") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo bancal")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Selecciona un bancal para trabajar con él, o crea uno nuevo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(bancales, key = { it.bancal.id }) { info ->
                BancalCard(
                    info = info,
                    isSelected = info.bancal.id == selectedId,
                    onSelect = { viewModel.seleccionar(info.bancal.id) },
                    onEdit = { editando = info },
                    onDelete = { eliminando = info }
                )
            }
        }
    }

    // Diálogo crear
    if (showAddDialog) {
        BancalFormDialog(
            titulo = "Nuevo bancal",
            nombreInicial = "",
            largoMInicial = "",
            anchoCmInicial = "",
            onDismiss = { showAddDialog = false },
            onConfirm = { nombre, largoCm, anchoCm ->
                viewModel.crearBancal(nombre, largoCm, anchoCm)
                showAddDialog = false
            }
        )
    }

    // Diálogo editar
    editando?.let { info ->
        BancalFormDialog(
            titulo = "Editar bancal",
            nombreInicial = info.bancal.nombre,
            largoMInicial = "%.1f".format(info.bancal.largoCm / 100f),
            anchoCmInicial = info.bancal.anchoCm.toString(),
            onDismiss = { editando = null },
            onConfirm = { nombre, largoCm, anchoCm ->
                viewModel.editarBancal(info.bancal, nombre, largoCm, anchoCm)
                editando = null
            }
        )
    }

    // Diálogo eliminar
    eliminando?.let { info ->
        AlertDialog(
            onDismissRequest = { eliminando = null },
            title = { Text("Eliminar bancal") },
            text = {
                Column {
                    Text("¿Eliminar \"${info.bancal.nombre}\"?")
                    if (info.plantacionesActivas > 0) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Se eliminarán ${info.plantacionesActivas} plantaciones activas.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminarBancal(info.bancal)
                    eliminando = null
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { eliminando = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun BancalCard(
    info: BancalConInfo,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val b = info.bancal
    val largoTexto = if (b.largoCm % 100 == 0) "${b.largoCm / 100}m" else "%.1fm".format(b.largoCm / 100f)

    Card(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth(),
        colors = if (isSelected) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        } else {
            CardDefaults.cardColors()
        },
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        b.nombre,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (isSelected) {
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Seleccionado",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "$largoTexto x ${b.anchoCm}cm",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    if (info.plantacionesActivas == 0) "Sin plantaciones"
                    else "${info.plantacionesActivas} plantacion${if (info.plantacionesActivas == 1) "" else "es"} activa${if (info.plantacionesActivas == 1) "" else "s"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Editar",
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun BancalFormDialog(
    titulo: String,
    nombreInicial: String,
    largoMInicial: String,
    anchoCmInicial: String,
    onDismiss: () -> Unit,
    onConfirm: (nombre: String, largoCm: Int, anchoCm: Int) -> Unit
) {
    var nombre by remember { mutableStateOf(nombreInicial) }
    var largoM by remember { mutableStateOf(largoMInicial) }
    var anchoCm by remember { mutableStateOf(anchoCmInicial) }

    val largoCmCalculado = (largoM.replace(",", ".").toFloatOrNull()?.times(100))?.toInt()
    val anchoCmCalculado = anchoCm.toIntOrNull()
    val valido = nombre.isNotBlank()
            && (largoCmCalculado ?: 0) in 50..5000
            && (anchoCmCalculado ?: 0) in 20..300

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(titulo) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = largoM,
                        onValueChange = { largoM = it },
                        label = { Text("Largo (m)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        supportingText = {
                            largoCmCalculado?.let { Text("${it}cm") }
                        }
                    )
                    OutlinedTextField(
                        value = anchoCm,
                        onValueChange = { anchoCm = it },
                        label = { Text("Ancho (cm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (valido) {
                        onConfirm(nombre.trim(), largoCmCalculado!!, anchoCmCalculado!!)
                    }
                },
                enabled = valido
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
