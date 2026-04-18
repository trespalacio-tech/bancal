package com.bancal.app.ui.screens.cultivopersonalizado

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.bancal.app.domain.model.*

private val MESES = listOf("E", "F", "M", "A", "My", "Jn", "Jl", "Ag", "S", "O", "N", "D")

private val EMOJIS_CULTIVO = listOf(
    "\uD83C\uDF3F", "\uD83C\uDF31", "\uD83C\uDF3E", "\uD83C\uDF3B",
    "\uD83C\uDF45", "\uD83E\uDD55", "\uD83C\uDF36\uFE0F", "\uD83E\uDD6C",
    "\uD83E\uDD52", "\uD83C\uDF3D", "\uD83E\uDED8", "\uD83C\uDF4F",
    "\uD83C\uDF4A", "\uD83C\uDF38", "\uD83C\uDF3A", "\uD83E\uDD66",
    "\uD83E\uDDC5", "\uD83E\uDDC4", "\uD83E\uDD54", "\uD83C\uDF46",
    "\uD83C\uDF4E", "\uD83C\uDF53", "\uD83C\uDF49", "\uD83C\uDF48"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CultivoPersonalizadoScreen(
    viewModel: CultivoPersonalizadoViewModel,
    onBack: () -> Unit
) {
    val nombre by viewModel.nombre.collectAsState()
    val icono by viewModel.icono.collectAsState()
    val familia by viewModel.familia.collectAsState()
    val marcoCm by viewModel.marcoCm.collectAsState()
    val diasCosecha by viewModel.diasCosecha.collectAsState()
    val diasGerminacion by viewModel.diasGerminacion.collectAsState()
    val temperaturaMinima by viewModel.temperaturaMinima.collectAsState()
    val temperaturaOptima by viewModel.temperaturaOptima.collectAsState()
    val profundidadSiembraCm by viewModel.profundidadSiembraCm.collectAsState()
    val riego by viewModel.riego.collectAsState()
    val categoria by viewModel.categoria.collectAsState()
    val exigencia by viewModel.exigencia.collectAsState()
    val lineasPorBancal by viewModel.lineasPorBancal.collectAsState()
    val semanasCosechando by viewModel.semanasCosechando.collectAsState()
    val admiteSiembraDirecta by viewModel.admiteSiembraDirecta.collectAsState()
    val admitePlantel by viewModel.admitePlantel.collectAsState()
    val notas by viewModel.notas.collectAsState()
    val mesesDirecta by viewModel.mesesDirecta.collectAsState()
    val mesesSemillero by viewModel.mesesSemillero.collectAsState()
    val mesesTrasplante by viewModel.mesesTrasplante.collectAsState()
    val guardado by viewModel.guardado.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(guardado) {
        if (guardado) {
            viewModel.resetGuardado()
            onBack()
        }
    }

    // Snackbar para errores
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
                title = { Text("Cultivo personalizado") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // === NOMBRE E ICONO ===
            item {
                Text("Basico", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            item {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { viewModel.nombre.value = it },
                    label = { Text("Nombre del cultivo *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                Text("Icono", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(EMOJIS_CULTIVO.size) { idx ->
                        val emoji = EMOJIS_CULTIVO[idx]
                        FilterChip(
                            selected = icono == emoji,
                            onClick = { viewModel.icono.value = emoji },
                            label = { Text(emoji) }
                        )
                    }
                }
            }

            // === FAMILIA Y CATEGORIAS ===
            item {
                Spacer(Modifier.height(4.dp))
                Text("Clasificacion", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            item {
                var expandedFamilia by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedFamilia,
                    onExpandedChange = { expandedFamilia = !expandedFamilia }
                ) {
                    OutlinedTextField(
                        value = familia.name.lowercase().replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Familia") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFamilia) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedFamilia,
                        onDismissRequest = { expandedFamilia = false }
                    ) {
                        FamiliaCultivo.entries.forEach { f ->
                            DropdownMenuItem(
                                text = { Text(f.name.lowercase().replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    viewModel.familia.value = f
                                    expandedFamilia = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Categoria biointensiva
                    var expandedCat by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedCat,
                        onExpandedChange = { expandedCat = !expandedCat },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = when (categoria) {
                                CategoriaBiointensiva.CARBONO -> "Carbono"
                                CategoriaBiointensiva.CALORICO -> "Calorico"
                                CategoriaBiointensiva.VEGETAL -> "Vegetal"
                            },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Categoria") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCat) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCat,
                            onDismissRequest = { expandedCat = false }
                        ) {
                            CategoriaBiointensiva.entries.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text(c.name.lowercase().replaceFirstChar { it.uppercase() }) },
                                    onClick = {
                                        viewModel.categoria.value = c
                                        expandedCat = false
                                    }
                                )
                            }
                        }
                    }

                    // Exigencia
                    var expandedExig by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedExig,
                        onExpandedChange = { expandedExig = !expandedExig },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = when (exigencia) {
                                ExigenciaNutricional.MUY_EXIGENTE -> "Muy exigente"
                                ExigenciaNutricional.POCO_EXIGENTE -> "Poco exigente"
                                ExigenciaNutricional.NADA_EXIGENTE -> "Nada exigente"
                            },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Exigencia") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedExig) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedExig,
                            onDismissRequest = { expandedExig = false }
                        ) {
                            ExigenciaNutricional.entries.forEach { e ->
                                val label = when (e) {
                                    ExigenciaNutricional.MUY_EXIGENTE -> "Muy exigente"
                                    ExigenciaNutricional.POCO_EXIGENTE -> "Poco exigente"
                                    ExigenciaNutricional.NADA_EXIGENTE -> "Nada exigente"
                                }
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        viewModel.exigencia.value = e
                                        expandedExig = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            item {
                // Riego
                Text("Riego", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NivelRiego.entries.forEach { r ->
                        FilterChip(
                            selected = riego == r,
                            onClick = { viewModel.riego.value = r },
                            label = { Text(r.name.lowercase().replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }
            }

            // === NUMEROS ===
            item {
                Spacer(Modifier.height(4.dp))
                Text("Parametros de cultivo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = marcoCm,
                        onValueChange = { viewModel.marcoCm.value = it },
                        label = { Text("Marco (cm)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = lineasPorBancal,
                        onValueChange = { viewModel.lineasPorBancal.value = it },
                        label = { Text("Lineas/bancal") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = diasGerminacion,
                        onValueChange = { viewModel.diasGerminacion.value = it },
                        label = { Text("Germinacion (d)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = diasCosecha,
                        onValueChange = { viewModel.diasCosecha.value = it },
                        label = { Text("Cosecha (d)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = semanasCosechando,
                        onValueChange = { viewModel.semanasCosechando.value = it },
                        label = { Text("Sem. cosecha") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = temperaturaMinima,
                        onValueChange = { viewModel.temperaturaMinima.value = it },
                        label = { Text("Temp. min (\u00B0C)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = temperaturaOptima,
                        onValueChange = { viewModel.temperaturaOptima.value = it },
                        label = { Text("Temp. optima (\u00B0C)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = profundidadSiembraCm,
                        onValueChange = { viewModel.profundidadSiembraCm.value = it },
                        label = { Text("Prof. (cm)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
            }

            // === MODO DE SIEMBRA ===
            item {
                Spacer(Modifier.height(4.dp))
                Text("Modo de siembra", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = admiteSiembraDirecta,
                        onClick = { viewModel.admiteSiembraDirecta.value = !admiteSiembraDirecta },
                        label = { Text("Siembra directa") },
                        leadingIcon = if (admiteSiembraDirecta) {
                            { Icon(Icons.Default.Check, null, modifier = Modifier.size(FilterChipDefaults.IconSize)) }
                        } else null
                    )
                    FilterChip(
                        selected = admitePlantel,
                        onClick = { viewModel.admitePlantel.value = !admitePlantel },
                        label = { Text("Plantel") },
                        leadingIcon = if (admitePlantel) {
                            { Icon(Icons.Default.Check, null, modifier = Modifier.size(FilterChipDefaults.IconSize)) }
                        } else null
                    )
                }
            }

            // === CALENDARIO: MESES DE SIEMBRA ===
            item {
                Spacer(Modifier.height(4.dp))
                Text("Calendario de siembra", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Selecciona los meses en que se puede sembrar/trasplantar en tu zona.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                MesesSelector(
                    label = "Siembra directa",
                    bitfield = mesesDirecta,
                    onToggle = { viewModel.toggleMesDirecta(it) }
                )
            }

            item {
                MesesSelector(
                    label = "Semillero",
                    bitfield = mesesSemillero,
                    onToggle = { viewModel.toggleMesSemillero(it) }
                )
            }

            item {
                MesesSelector(
                    label = "Trasplante",
                    bitfield = mesesTrasplante,
                    onToggle = { viewModel.toggleMesTrasplante(it) }
                )
            }

            // === NOTAS ===
            item {
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = notas,
                    onValueChange = { viewModel.notas.value = it },
                    label = { Text("Notas (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )
            }

            // === BOTON GUARDAR ===
            item {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.guardar() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Check, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Crear cultivo")
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MesesSelector(
    label: String,
    bitfield: Int,
    onToggle: (Int) -> Unit
) {
    Column {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(4.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            MESES.forEachIndexed { idx, nombre ->
                val activo = (bitfield and (1 shl idx)) != 0
                FilterChip(
                    selected = activo,
                    onClick = { onToggle(idx) },
                    label = { Text(nombre, style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.height(32.dp)
                )
            }
        }
    }
}
