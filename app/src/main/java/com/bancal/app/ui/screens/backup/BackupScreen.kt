package com.bancal.app.ui.screens.backup

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bancal.app.data.backup.BackupManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupScreen(
    onBack: () -> Unit,
    onRestaurado: () -> Unit
) {
    val context = LocalContext.current
    val backupManager = remember { BackupManager(context) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showConfirmRestore by remember { mutableStateOf(false) }
    var pendingRestoreUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para elegir donde guardar el backup
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri ->
        if (uri != null) {
            val result = backupManager.exportar(uri)
            scope.launch {
                result.fold(
                    onSuccess = {
                        snackbarHostState.showSnackbar("Copia de seguridad creada correctamente")
                    },
                    onFailure = { e ->
                        snackbarHostState.showSnackbar("Error: ${e.message}")
                    }
                )
            }
        }
    }

    // Launcher para elegir el archivo de backup a restaurar
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {
                // Algunos proveedores no soportan permisos persistentes, continuamos igual
            }
            pendingRestoreUri = uri
            showConfirmRestore = true
        }
    }

    // Dialogo de confirmacion de restauracion
    if (showConfirmRestore && pendingRestoreUri != null) {
        AlertDialog(
            onDismissRequest = {
                showConfirmRestore = false
                pendingRestoreUri = null
            },
            icon = { Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Restaurar copia de seguridad") },
            text = {
                Text("Esto reemplazara TODOS los datos actuales (bancales, plantaciones, tratamientos, alertas y diario) con los de la copia de seguridad.\n\nEsta accion no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmRestore = false
                        val uri = pendingRestoreUri!!
                        pendingRestoreUri = null
                        val result = backupManager.importar(uri)
                        result.fold(
                            onSuccess = { onRestaurado() },
                            onFailure = { e ->
                                scope.launch {
                                    snackbarHostState.showSnackbar("Error al restaurar: ${e.message}")
                                }
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Restaurar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showConfirmRestore = false
                    pendingRestoreUri = null
                }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Copias de seguridad") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Explicacion
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Tus datos, a salvo",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Crea una copia de seguridad de todos tus datos (bancales, plantaciones, tratamientos, alertas, diario y cultivos personalizados) en un archivo que puedes guardar en Google Drive, enviar por correo o copiar a otro dispositivo.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Exportar
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CloudUpload, null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                "Crear copia de seguridad",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Exporta todos los datos a un archivo .db",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { exportLauncher.launch(backupManager.suggestedFileName()) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Exportar")
                    }
                }
            }

            // Importar
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CloudDownload, null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                "Restaurar copia de seguridad",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Importa datos desde un archivo .db previo",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { importLauncher.launch(arrayOf("application/octet-stream", "*/*")) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Restaurar desde archivo")
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Reemplaza todos los datos actuales. La app se reiniciara tras restaurar.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
