package com.bancal.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bancal.app.data.db.entity.AlertaEntity
import com.bancal.app.domain.model.TipoAlerta
import com.bancal.app.ui.theme.ErrorLight
import com.bancal.app.ui.theme.SuccessGreen
import com.bancal.app.ui.theme.WarningAmber
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun AlertaCard(
    alerta: AlertaEntity,
    onResolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = when (alerta.tipo) {
        TipoAlerta.HELADA -> ErrorLight.copy(alpha = 0.1f)
        TipoAlerta.COSECHA -> SuccessGreen.copy(alpha = 0.1f)
        TipoAlerta.TRASPLANTE -> WarningAmber.copy(alpha = 0.1f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alerta.titulo,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = alerta.mensaje,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = formatFecha(alerta.fecha),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            if (!alerta.resuelta) {
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = onResolver) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Resolver",
                        tint = SuccessGreen
                    )
                }
            }
        }
    }
}

private fun formatFecha(epochMillis: Long): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", java.util.Locale("es", "ES"))
    return Instant.ofEpochMilli(epochMillis)
        .atZone(ZoneId.of("Europe/Madrid"))
        .format(formatter)
}
