package com.bancal.app.ui.screens.balance

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bancal.app.ui.theme.*

// Colores para las categorías biointensivas — adaptivos a tema
private val _CarbonoLight = Color(0xFF8B6914)
private val _CarbonoNight = Color(0xFFD4A84A)
private val _CaloricoLight = Color(0xFFD4763C)
private val _CaloricoNight = Color(0xFFE8A070)
private val _VegetalLight = Color(0xFF2D6A4F)
private val _VegetalNight = Color(0xFF74C69D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BalanceScreen(
    viewModel: BalanceViewModel,
    onBack: () -> Unit
) {
    val balance by viewModel.balance.collectAsState()
    val isDark = isSystemInDarkTheme()
    val carbonoColor = if (isDark) _CarbonoNight else _CarbonoLight
    val caloricoColor = if (isDark) _CaloricoNight else _CaloricoLight
    val vegetalColor = if (isDark) _VegetalNight else _VegetalLight
    val libreColor = MaterialTheme.colorScheme.surfaceVariant

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Balance biointensivo") },
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
                            "Regla 60 / 30 / 10",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "El método biointensivo de John Jeavons recomienda distribuir el espacio de cultivo para mantener la fertilidad del suelo a largo plazo:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))
                        CategoriaExplicacion(
                            color = carbonoColor,
                            titulo = "60% Carbono / Compost",
                            descripcion = "Cereales y gramíneas que producen biomasa para compostar (maíz, girasol). Sostienen la fertilidad."
                        )
                        CategoriaExplicacion(
                            color = caloricoColor,
                            titulo = "30% Calórico",
                            descripcion = "Raíces, tubérculos y leguminosas de alta densidad calórica (patata, ajo, habas, zanahoria)."
                        )
                        CategoriaExplicacion(
                            color = vegetalColor,
                            titulo = "10% Vegetal",
                            descripcion = "Hortalizas, aromáticas y flores: vitaminas y minerales (tomate, lechuga, albahaca)."
                        )
                    }
                }
            }

            // Barra de balance actual
            item {
                Text("Tu distribución actual", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))

                if (balance.cmOcupado == 0) {
                    Text(
                        "Aún no hay plantaciones activas. Planta algo para ver tu balance.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    BalanceBar(balance, carbonoColor, caloricoColor, vegetalColor, libreColor)
                    Spacer(Modifier.height(8.dp))
                    BalanceLeyenda(balance, carbonoColor, caloricoColor, vegetalColor)
                }
            }

            // Barra ideal de referencia
            item {
                Text("Distribución ideal", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                IdealBar(carbonoColor, caloricoColor, vegetalColor)
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("60% carbono", style = MaterialTheme.typography.labelSmall, color = carbonoColor)
                    Text("30% calórico", style = MaterialTheme.typography.labelSmall, color = caloricoColor)
                    Text("10% vegetal", style = MaterialTheme.typography.labelSmall, color = vegetalColor)
                }
            }

            // Diagnóstico
            if (balance.cmOcupado > 0) {
                item {
                    Spacer(Modifier.height(4.dp))
                    DiagnosticoCard(balance)
                }
            }

            // Detalle por categoría
            if (balance.cultivosCarbono.isNotEmpty()) {
                item {
                    CategoriaHeader("Carbono / Compost", carbonoColor, balance.cmCarbono)
                }
                items(balance.cultivosCarbono) { co ->
                    CultivoRow(co)
                }
            }

            if (balance.cultivosCalorico.isNotEmpty()) {
                item {
                    CategoriaHeader("Calórico", caloricoColor, balance.cmCalorico)
                }
                items(balance.cultivosCalorico) { co ->
                    CultivoRow(co)
                }
            }

            if (balance.cultivosVegetal.isNotEmpty()) {
                item {
                    CategoriaHeader("Vegetal", vegetalColor, balance.cmVegetal)
                }
                items(balance.cultivosVegetal) { co ->
                    CultivoRow(co)
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun BalanceBar(
    balance: BalanceData,
    carbonoColor: Color,
    caloricoColor: Color,
    vegetalColor: Color,
    libreColor: Color
) {
    val total = balance.bancalTotalCm.toFloat()
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
    ) {
        val w = size.width
        val h = size.height
        val radius = CornerRadius(8f, 8f)

        // Fondo (libre)
        drawRoundRect(color = libreColor, size = Size(w, h), cornerRadius = radius)

        // Segmentos proporcionales
        var offsetX = 0f
        val segments = listOf(
            balance.cmCarbono to carbonoColor,
            balance.cmCalorico to caloricoColor,
            balance.cmVegetal to vegetalColor
        )
        for ((cm, color) in segments) {
            if (cm > 0) {
                val segW = (cm / total) * w
                drawRect(color = color, topLeft = Offset(offsetX, 0f), size = Size(segW, h))
                offsetX += segW
            }
        }

        // Bordes redondeados encima
        drawRoundRect(
            color = Color.Transparent,
            size = Size(w, h),
            cornerRadius = radius,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
        )
    }
}

@Composable
private fun IdealBar(carbonoColor: Color, caloricoColor: Color, vegetalColor: Color) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
    ) {
        val w = size.width
        val h = size.height
        drawRect(color = carbonoColor.copy(alpha = 0.6f), topLeft = Offset.Zero, size = Size(w * 0.6f, h))
        drawRect(color = caloricoColor.copy(alpha = 0.6f), topLeft = Offset(w * 0.6f, 0f), size = Size(w * 0.3f, h))
        drawRect(color = vegetalColor.copy(alpha = 0.6f), topLeft = Offset(w * 0.9f, 0f), size = Size(w * 0.1f, h))
    }
}

@Composable
private fun BalanceLeyenda(balance: BalanceData, carbonoColor: Color, caloricoColor: Color, vegetalColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LeyendaItem(carbonoColor, "Carbono", balance.pctCarbono, balance.cmCarbono)
        LeyendaItem(caloricoColor, "Calórico", balance.pctCalorico, balance.cmCalorico)
        LeyendaItem(vegetalColor, "Vegetal", balance.pctVegetal, balance.cmVegetal)
    }

    Spacer(Modifier.height(4.dp))
    Text(
        "${balance.cmOcupado}cm ocupados de ${balance.bancalTotalCm}cm (${balance.cmLibre}cm libres)",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun LeyendaItem(color: Color, label: String, pct: Float, cm: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(Modifier.size(12.dp)) {
            drawCircle(color = color)
        }
        Text(
            "${String.format("%.0f", pct)}%",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(label, style = MaterialTheme.typography.labelSmall)
        Text("${cm}cm", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun DiagnosticoCard(balance: BalanceData) {
    val consejos = mutableListOf<String>()

    if (balance.pctCarbono < 40f) {
        consejos.add("Falta carbono: necesitas mas maiz, girasol u otros cereales para producir compost y mantener la fertilidad del suelo.")
    }
    if (balance.pctCalorico < 15f && balance.pctVegetal > 20f) {
        consejos.add("Pocas calorias: anade patatas, zanahorias, habas u otras raices/leguminosas para mayor autosuficiencia.")
    }
    if (balance.pctVegetal > 50f) {
        consejos.add("Exceso de hortalizas: un bancal solo con vegetales agota el suelo. Sustituye parte por cultivos de carbono.")
    }
    if (balance.pctCarbono >= 40f && balance.pctCalorico >= 15f && balance.pctVegetal <= 30f) {
        consejos.add("Buen equilibrio. Tu bancal se acerca al modelo biointensivo sostenible.")
    }

    if (consejos.isEmpty()) return

    val esBueno = consejos.any { it.startsWith("Buen") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (esBueno)
                SuccessGreen.copy(alpha = 0.1f)
            else
                WarningAmber.copy(alpha = 0.1f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                if (esBueno) "Diagnostico" else "Recomendaciones",
                style = MaterialTheme.typography.titleMedium
            )
            for (consejo in consejos) {
                Spacer(Modifier.height(4.dp))
                Text(
                    consejo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CategoriaExplicacion(color: Color, titulo: String, descripcion: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Canvas(Modifier.size(12.dp).padding(top = 4.dp)) {
            drawCircle(color = color)
        }
        Spacer(Modifier.width(8.dp))
        Column {
            Text(titulo, style = MaterialTheme.typography.labelLarge, color = color)
            Text(
                descripcion,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CategoriaHeader(nombre: String, color: Color, cm: Int) {
    Spacer(Modifier.height(4.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(nombre, style = MaterialTheme.typography.titleMedium, color = color)
        Text("${cm}cm", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun CultivoRow(co: CultivoOcupacion) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(co.cultivo.icono, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.width(12.dp))
        Text(
            co.cultivo.nombre,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            "${co.cmOcupados}cm",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
