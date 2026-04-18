package com.bancal.app.ui.screens.alertas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bancal.app.ui.components.AlertaCard

@Composable
fun AlertasScreen(viewModel: AlertasViewModel) {
    val alertas by viewModel.alertas.collectAsState()
    val pendientes = alertas.count { !it.resuelta }
    val resueltas = alertas.count { it.resuelta }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Alertas", style = MaterialTheme.typography.headlineLarge)
                    Text(
                        "$pendientes pendientes · $resueltas resueltas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (resueltas > 0) {
                    IconButton(onClick = { viewModel.limpiarResueltas() }) {
                        Icon(
                            Icons.Default.CleaningServices,
                            "Limpiar resueltas",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        if (alertas.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("✅", style = MaterialTheme.typography.headlineLarge)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Todo en orden",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "No hay alertas pendientes",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Pendientes primero
        val pendientesList = alertas.filter { !it.resuelta }
        if (pendientesList.isNotEmpty()) {
            item {
                Text("Pendientes", style = MaterialTheme.typography.titleMedium)
            }
            items(pendientesList, key = { it.id }) { alerta ->
                AlertaCard(
                    alerta = alerta,
                    onResolver = { viewModel.resolverAlerta(alerta.id) }
                )
            }
        }

        // Resueltas
        val resueltasList = alertas.filter { it.resuelta }
        if (resueltasList.isNotEmpty()) {
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Resueltas",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            items(resueltasList, key = { it.id }) { alerta ->
                AlertaCard(
                    alerta = alerta,
                    onResolver = { }
                )
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}
