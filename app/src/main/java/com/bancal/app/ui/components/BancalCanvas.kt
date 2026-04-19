package com.bancal.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bancal.app.data.db.entity.CultivoEntity
import com.bancal.app.data.db.entity.PlantacionEntity
import com.bancal.app.domain.model.EstadoPlantacion
import com.bancal.app.ui.theme.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

data class PlantacionVisual(
    val plantacion: PlantacionEntity,
    val cultivo: CultivoEntity
)

@Composable
fun BancalCanvas(
    plantaciones: List<PlantacionVisual>,
    bancalLargoCm: Int = 1000,
    onTapZonaVacia: (posicionXCm: Int) -> Unit = {},
    onTapPlantacion: (PlantacionEntity) -> Unit = {},
    modifier: Modifier = Modifier,
    previewPosXCm: Int? = null,
    previewAnchoCm: Int = 0,
    previewEmoji: String = "",
    previewOcupado: Boolean = false,
    hoy: LocalDate = LocalDate.now()
) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val scrollState = rememberScrollState()

    // Escala adaptativa: máximo ~8000dp de ancho para no desbordar Compose Constraints
    val maxCanvasWidthDp = 8000
    val dpPerCm = (maxCanvasWidthDp.toFloat() / bancalLargoCm.coerceAtLeast(1)).coerceAtMost(2f)
    val pxPerCm = with(density) { dpPerCm.dp.toPx() }
    val canvasWidthDp = (bancalLargoCm * dpPerCm).dp
    val canvasHeight = 180.dp

    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant
    val borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    val textColor = MaterialTheme.colorScheme.onSurface
    val textMuted = MaterialTheme.colorScheme.onSurfaceVariant
    val errorColor = MaterialTheme.colorScheme.error
    val primaryColor = MaterialTheme.colorScheme.primary

    val estadoColors = mapOf(
        EstadoPlantacion.SEMILLERO to EstadoSemillero,
        EstadoPlantacion.TRASPLANTADO to EstadoTrasplantado,
        EstadoPlantacion.CRECIENDO to EstadoCreciendo,
        EstadoPlantacion.COSECHANDO to EstadoCosechando,
        EstadoPlantacion.RETIRADO to EstadoRetirado
    )

    // Agrupar: hijas -> su madre; madres con hijas
    val hijasPorMadreId = plantaciones
        .filter { it.plantacion.intercaladaCon != null }
        .groupBy { it.plantacion.intercaladaCon!! }
    val idsMadres = hijasPorMadreId.keys

    // Calcular huecos libres (zonas sin plantación que ocupe la banda principal)
    val ocupadoresDeBanda = plantaciones.filter { it.plantacion.intercaladaCon == null }
        .sortedBy { it.plantacion.posicionXCm }
    val huecosLibres = buildList {
        var cursor = 0
        for (pv in ocupadoresDeBanda) {
            val p = pv.plantacion
            if (p.posicionXCm > cursor) add(cursor to p.posicionXCm)
            cursor = maxOf(cursor, p.posicionXCm + p.anchoCm)
        }
        if (cursor < bancalLargoCm) add(cursor to bancalLargoCm)
    }

    Box(
        modifier = modifier
            .horizontalScroll(scrollState)
    ) {
        Canvas(
            modifier = Modifier
                .width(canvasWidthDp)
                .height(canvasHeight)
                .pointerInput(plantaciones) {
                    detectTapGestures { offset ->
                        val xCm = (offset.x / pxPerCm).toInt()
                        val enX = plantaciones.filter { pv ->
                            val p = pv.plantacion
                            xCm >= p.posicionXCm && xCm <= p.posicionXCm + p.anchoCm
                        }
                        val tocada = when {
                            enX.isEmpty() -> null
                            enX.size == 1 -> enX.first()
                            else -> {
                                val topHalf = offset.y < size.height / 2f
                                val noHija = enX.firstOrNull { it.plantacion.intercaladaCon == null }
                                val hija = enX.firstOrNull { it.plantacion.intercaladaCon != null }
                                if (topHalf) noHija ?: hija else hija ?: noHija
                            }
                        }
                        if (tocada != null) onTapPlantacion(tocada.plantacion)
                        else onTapZonaVacia(xCm)
                    }
                }
        ) {
            // Fondo del bancal con leve gradiente vertical
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        surfaceColor,
                        surfaceColor.copy(alpha = 0.85f)
                    )
                ),
                topLeft = Offset.Zero,
                size = Size(size.width, size.height),
                cornerRadius = CornerRadius(10f, 10f)
            )

            // Regla adaptativa: ticks menores cada 50cm, mayores cada 1m con label
            drawRegla(
                bancalLargoCm = bancalLargoCm,
                pxPerCm = pxPerCm,
                textMeasurer = textMeasurer,
                borderColor = borderColor,
                surfaceColor = surfaceColor,
                textColor = textColor
            )

            // Huecos libres: label "X cm libre" si hueco ≥ 50cm
            for ((startCm, finCm) in huecosLibres) {
                val anchoHueco = finCm - startCm
                if (anchoHueco < 50) continue
                val centroX = ((startCm + finCm) / 2f) * pxPerCm
                val label = "$anchoHueco cm libre"
                val labelStyle = TextStyle(
                    color = textMuted.copy(alpha = 0.7f),
                    fontSize = 10.sp
                )
                val layout = textMeasurer.measure(label, labelStyle)
                val padX = 6f
                val padY = 3f
                val boxW = layout.size.width + padX * 2
                val boxH = layout.size.height + padY * 2
                // Solo si el label cabe en el hueco con margen
                if (boxW < anchoHueco * pxPerCm - 12f) {
                    val boxX = centroX - boxW / 2
                    val boxY = (size.height - boxH) / 2
                    drawRoundRect(
                        color = surfaceColor.copy(alpha = 0.85f),
                        topLeft = Offset(boxX, boxY),
                        size = Size(boxW, boxH),
                        cornerRadius = CornerRadius(8f, 8f)
                    )
                    drawRoundRect(
                        color = borderColor.copy(alpha = 0.5f),
                        topLeft = Offset(boxX, boxY),
                        size = Size(boxW, boxH),
                        cornerRadius = CornerRadius(8f, 8f),
                        style = Stroke(width = 0.8f)
                    )
                    drawText(
                        textMeasurer = textMeasurer,
                        text = label,
                        topLeft = Offset(boxX + padX, boxY + padY),
                        style = labelStyle
                    )
                }
            }

            // Borde sutil del bancal (por encima de regla y huecos)
            drawRoundRect(
                color = borderColor,
                topLeft = Offset.Zero,
                size = Size(size.width, size.height),
                cornerRadius = CornerRadius(10f, 10f),
                style = Stroke(width = 1f)
            )

            // Plantaciones por banda
            for (pv in plantaciones) {
                val p = pv.plantacion
                val esHija = p.intercaladaCon != null
                val esMadre = p.intercaladaCon == null && idsMadres.contains(p.id)
                val banda = when {
                    esHija -> Banda.INFERIOR
                    esMadre -> Banda.SUPERIOR
                    else -> Banda.COMPLETA
                }
                drawPlantacionEnBanda(
                    pv = pv,
                    banda = banda,
                    pxPerCm = pxPerCm,
                    textMeasurer = textMeasurer,
                    textColor = textColor,
                    estadoColors = estadoColors,
                    hoy = hoy,
                    surfaceColor = surfaceColor
                )
            }

            // Preview fantasma de nueva plantación
            if (previewPosXCm != null && previewAnchoCm > 0) {
                drawPreview(
                    previewPosXCm, previewAnchoCm, previewEmoji, previewOcupado,
                    pxPerCm, textMeasurer, errorColor, primaryColor
                )
            }
        }
    }
}

private enum class Banda { COMPLETA, SUPERIOR, INFERIOR }

private fun DrawScope.drawRegla(
    bancalLargoCm: Int,
    pxPerCm: Float,
    textMeasurer: TextMeasurer,
    borderColor: Color,
    surfaceColor: Color,
    textColor: Color
) {
    // Ticks menores cada 50cm (sin label)
    val totalHalfMetros = bancalLargoCm / 50
    for (i in 1..totalHalfMetros) {
        val cm = i * 50
        if (cm % 100 == 0) continue // lo dibuja el tick mayor
        val x = cm * pxPerCm
        drawLine(
            color = borderColor.copy(alpha = 0.6f),
            start = Offset(x, 0f),
            end = Offset(x, 3f),
            strokeWidth = 0.8f
        )
        drawLine(
            color = borderColor.copy(alpha = 0.6f),
            start = Offset(x, size.height - 3f),
            end = Offset(x, size.height),
            strokeWidth = 0.8f
        )
    }

    // Ticks mayores cada 1m + grid vertical tenue + label
    for (metro in 1 until bancalLargoCm / 100) {
        val x = metro * 100 * pxPerCm
        drawLine(borderColor, Offset(x, 0f), Offset(x, 6f), strokeWidth = 1f)
        drawLine(borderColor, Offset(x, size.height - 6f), Offset(x, size.height), strokeWidth = 1f)
        drawLine(
            borderColor.copy(alpha = 0.18f),
            Offset(x, 8f),
            Offset(x, size.height - 8f),
            strokeWidth = 0.5f
        )
        val label = "${metro}m"
        val labelStyle = TextStyle(color = textColor.copy(alpha = 0.55f), fontSize = 9.sp)
        val labelLayout = textMeasurer.measure(label, labelStyle)
        val labelPad = 4f
        drawRoundRect(
            color = surfaceColor.copy(alpha = 0.9f),
            topLeft = Offset(x + 3f, 2f),
            size = Size(
                labelLayout.size.width + labelPad * 2,
                labelLayout.size.height + 2f
            ),
            cornerRadius = CornerRadius(6f, 6f)
        )
        drawText(
            textMeasurer = textMeasurer,
            text = label,
            topLeft = Offset(x + 3f + labelPad, 2f),
            style = labelStyle
        )
    }
}

private fun DrawScope.drawPlantacionEnBanda(
    pv: PlantacionVisual,
    banda: Banda,
    pxPerCm: Float,
    textMeasurer: TextMeasurer,
    textColor: Color,
    estadoColors: Map<EstadoPlantacion, Color>,
    hoy: LocalDate,
    surfaceColor: Color
) {
    val p = pv.plantacion
    val c = pv.cultivo

    val x = p.posicionXCm * pxPerCm
    val w = p.anchoCm * pxPerCm
    val outerPad = 4f
    val splitGap = 2f
    val fullH = size.height - outerPad * 2
    val halfH = (fullH - splitGap) / 2

    val (topY, h) = when (banda) {
        Banda.COMPLETA -> outerPad to fullH
        Banda.SUPERIOR -> outerPad to halfH
        Banda.INFERIOR -> (outerPad + halfH + splitGap) to halfH
    }

    val color = estadoColors[p.estado] ?: Color.Gray
    val cornerR = if (banda == Banda.COMPLETA) 8f else 6f

    // Rectángulo con gradiente vertical
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(color.copy(alpha = 0.32f), color.copy(alpha = 0.18f)),
            startY = topY, endY = topY + h
        ),
        topLeft = Offset(x + 2f, topY),
        size = Size(w - 4f, h),
        cornerRadius = CornerRadius(cornerR, cornerR),
        style = Fill
    )

    // Borde
    drawRoundRect(
        color = color.copy(alpha = 0.55f),
        topLeft = Offset(x + 2f, topY),
        size = Size(w - 4f, h),
        cornerRadius = CornerRadius(cornerR, cornerR),
        style = Stroke(width = 1.5f)
    )

    // Puntitos: uno por planta real
    val slotsX = if (c.marcoCm > 0) (p.anchoCm / c.marcoCm).coerceAtLeast(1) else 1
    drawPlantasDots(
        rectX = x + 2f,
        rectY = topY,
        rectW = w - 4f,
        rectH = h,
        slotsX = slotsX,
        lineas = c.lineasPorBancal.coerceAtLeast(1),
        color = color
    )

    // Texto (emoji + opcional nombre/días)
    if (w > 30f) {
        val emojiSize = when (banda) {
            Banda.COMPLETA -> 20.sp
            else -> 16.sp
        }
        val emojiStyle = TextStyle(fontSize = emojiSize)
        val emojiLayout = textMeasurer.measure(c.icono, emojiStyle)
        val emojiY = when (banda) {
            Banda.COMPLETA -> topY + (h - emojiLayout.size.height) / 3
            else -> topY + (h - emojiLayout.size.height) / 2
        }
        drawText(
            textMeasurer = textMeasurer,
            text = c.icono,
            topLeft = Offset(x + (w - emojiLayout.size.width) / 2, emojiY),
            style = emojiStyle
        )

        // En banda completa y si hay espacio: nombre y días a cosecha
        if (banda == Banda.COMPLETA) {
            val nameStyle = TextStyle(color = textColor, fontSize = 9.sp)
            val nameLayout = textMeasurer.measure(c.nombre, nameStyle)
            if (nameLayout.size.width < w - 8f) {
                drawText(
                    textMeasurer = textMeasurer,
                    text = c.nombre,
                    topLeft = Offset(
                        x + (w - nameLayout.size.width) / 2,
                        topY + h * 0.58f
                    ),
                    style = nameStyle
                )
            }
        }
    }

    // Progreso hacia cosecha: barra en la base de la plantación
    drawProgresoBase(
        x = x + 2f,
        rectY = topY,
        rectW = w - 4f,
        rectH = h,
        cornerR = cornerR,
        plantacion = p,
        hoy = hoy,
        color = color
    )

    // Badge de cosecha inminente (≤ 7 días) en la esquina superior derecha
    val diasACosecha = calcularDiasACosecha(p, hoy)
    if (diasACosecha in 0..7 && p.estado != EstadoPlantacion.RETIRADO) {
        drawBadgeCosecha(
            rightX = x + w - 4f,
            topY = topY + 2f,
            textMeasurer = textMeasurer,
            surfaceColor = surfaceColor
        )
    }

    // Indicador de enlace madre↔hija en la cabecera de la banda inferior (hija)
    if (banda == Banda.INFERIOR) {
        drawLinkIndicator(
            centerX = x + w / 2f,
            y = topY - splitGap / 2f,
            textMeasurer = textMeasurer,
            surfaceColor = surfaceColor
        )
    }
}

private fun DrawScope.drawProgresoBase(
    x: Float,
    rectY: Float,
    rectW: Float,
    rectH: Float,
    cornerR: Float,
    plantacion: PlantacionEntity,
    hoy: LocalDate,
    color: Color
) {
    if (plantacion.estado == EstadoPlantacion.RETIRADO) return
    val zone = ZoneId.of("Europe/Madrid")
    val fechaSiembra = Instant.ofEpochMilli(plantacion.fechaSiembra).atZone(zone).toLocalDate()
    val fechaCos = Instant.ofEpochMilli(plantacion.fechaCosechaEstimada).atZone(zone).toLocalDate()
    val totalDias = ChronoUnit.DAYS.between(fechaSiembra, fechaCos).toInt().coerceAtLeast(1)
    val diasTranscurridos = ChronoUnit.DAYS.between(fechaSiembra, hoy).toInt().coerceAtLeast(0)
    val progreso = (diasTranscurridos.toFloat() / totalDias).coerceIn(0f, 1f)

    val barH = 3f
    val barY = rectY + rectH - barH - 1.5f
    // Track
    drawRoundRect(
        color = color.copy(alpha = 0.18f),
        topLeft = Offset(x + 2f, barY),
        size = Size(rectW - 4f, barH),
        cornerRadius = CornerRadius(cornerR / 2, cornerR / 2)
    )
    // Fill
    val fillW = (rectW - 4f) * progreso
    if (fillW > 0f) {
        drawRoundRect(
            color = color.copy(alpha = 0.85f),
            topLeft = Offset(x + 2f, barY),
            size = Size(fillW, barH),
            cornerRadius = CornerRadius(cornerR / 2, cornerR / 2)
        )
    }
}

private fun DrawScope.drawBadgeCosecha(
    rightX: Float,
    topY: Float,
    textMeasurer: TextMeasurer,
    surfaceColor: Color
) {
    val emoji = "\uD83E\uDDFA" // 🧺
    val style = TextStyle(fontSize = 11.sp)
    val layout = textMeasurer.measure(emoji, style)
    val pad = 2f
    val boxW = layout.size.width + pad * 2
    val boxH = layout.size.height + pad
    val boxX = rightX - boxW - 2f
    val boxY = topY
    // Pastilla clara para que destaque sobre cualquier color de estado
    drawRoundRect(
        color = surfaceColor.copy(alpha = 0.95f),
        topLeft = Offset(boxX, boxY),
        size = Size(boxW, boxH),
        cornerRadius = CornerRadius(6f, 6f)
    )
    drawText(
        textMeasurer = textMeasurer,
        text = emoji,
        topLeft = Offset(boxX + pad, boxY),
        style = style
    )
}

private fun DrawScope.drawLinkIndicator(
    centerX: Float,
    y: Float,
    textMeasurer: TextMeasurer,
    surfaceColor: Color
) {
    val emoji = "\uD83D\uDD17" // 🔗
    val style = TextStyle(fontSize = 8.sp)
    val layout = textMeasurer.measure(emoji, style)
    val pad = 1.5f
    val boxW = layout.size.width + pad * 2
    val boxH = layout.size.height + pad
    val boxX = centerX - boxW / 2f
    val boxY = y - boxH / 2f
    drawRoundRect(
        color = surfaceColor.copy(alpha = 0.95f),
        topLeft = Offset(boxX, boxY),
        size = Size(boxW, boxH),
        cornerRadius = CornerRadius(4f, 4f)
    )
    drawText(
        textMeasurer = textMeasurer,
        text = emoji,
        topLeft = Offset(boxX + pad, boxY),
        style = style
    )
}

private fun calcularDiasACosecha(p: PlantacionEntity, hoy: LocalDate): Int {
    val zone = ZoneId.of("Europe/Madrid")
    val fechaCos = Instant.ofEpochMilli(p.fechaCosechaEstimada).atZone(zone).toLocalDate()
    return ChronoUnit.DAYS.between(hoy, fechaCos).toInt()
}

private fun DrawScope.drawPlantasDots(
    rectX: Float,
    rectY: Float,
    rectW: Float,
    rectH: Float,
    slotsX: Int,
    lineas: Int,
    color: Color
) {
    if (slotsX <= 0 || lineas <= 0 || rectW <= 6f || rectH <= 6f) return
    val cellW = rectW / slotsX
    // Reservar espacio inferior para la barra de progreso (no pisarla con los puntos)
    val usableH = (rectH - 6f).coerceAtLeast(rectH * 0.7f)
    val cellH = usableH / lineas
    val radius = (minOf(cellW, cellH) / 4f).coerceIn(1.5f, 4.5f)
    val dotColor = color.copy(alpha = 0.95f)
    for (col in 0 until slotsX) {
        for (row in 0 until lineas) {
            val cx = rectX + cellW * (col + 0.5f)
            val cy = rectY + cellH * (row + 0.5f)
            drawCircle(color = dotColor, radius = radius, center = Offset(cx, cy))
        }
    }
}

private fun DrawScope.drawPreview(
    posXCm: Int,
    anchoCm: Int,
    emoji: String,
    ocupado: Boolean,
    pxPerCm: Float,
    textMeasurer: TextMeasurer,
    errorColor: Color,
    primaryColor: Color
) {
    val x = posXCm * pxPerCm
    val w = anchoCm * pxPerCm
    val padding = 4f
    val h = size.height - padding * 2

    val color = if (ocupado) errorColor else primaryColor

    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(color.copy(alpha = 0.22f), color.copy(alpha = 0.10f)),
            startY = padding, endY = padding + h
        ),
        topLeft = Offset(x + 2f, padding),
        size = Size(w - 4f, h),
        cornerRadius = CornerRadius(8f, 8f),
        style = Fill
    )

    // Borde punteado para distinguir del resto
    drawRoundRect(
        color = color.copy(alpha = 0.8f),
        topLeft = Offset(x + 2f, padding),
        size = Size(w - 4f, h),
        cornerRadius = CornerRadius(8f, 8f),
        style = Stroke(
            width = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f))
        )
    )

    if (w > 30f && emoji.isNotEmpty()) {
        val emojiStyle = TextStyle(fontSize = 20.sp)
        val emojiLayout = textMeasurer.measure(emoji, emojiStyle)
        drawText(
            textMeasurer = textMeasurer,
            text = emoji,
            topLeft = Offset(
                x + (w - emojiLayout.size.width) / 2,
                padding + (h - emojiLayout.size.height) / 2
            ),
            style = emojiStyle
        )
    }
}
