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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
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
    previewOcupado: Boolean = false
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
    val errorColor = MaterialTheme.colorScheme.error
    val primaryColor = MaterialTheme.colorScheme.primary

    val estadoColors = mapOf(
        EstadoPlantacion.SEMILLERO to EstadoSemillero,
        EstadoPlantacion.TRASPLANTADO to EstadoTrasplantado,
        EstadoPlantacion.CRECIENDO to EstadoCreciendo,
        EstadoPlantacion.COSECHANDO to EstadoCosechando,
        EstadoPlantacion.RETIRADO to EstadoRetirado
    )

    // Agrupar: hijas -> su madre; madres con hijas; libres (sin hijas y sin madre)
    val hijasPorMadreId = plantaciones
        .filter { it.plantacion.intercaladaCon != null }
        .groupBy { it.plantacion.intercaladaCon!! }
    val idsMadres = hijasPorMadreId.keys

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
                                // Varias plantaciones en el mismo X: distinguir por banda.
                                // Banda superior (y < H/2) = madre/libre; inferior = hija.
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

            // Borde sutil del bancal
            drawRoundRect(
                color = borderColor,
                topLeft = Offset.Zero,
                size = Size(size.width, size.height),
                cornerRadius = CornerRadius(10f, 10f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f)
            )

            // Marcas cada metro
            for (metro in 1 until bancalLargoCm / 100) {
                val x = metro * 100 * pxPerCm
                drawLine(borderColor, Offset(x, 0f), Offset(x, 6f), strokeWidth = 1f)
                drawLine(borderColor, Offset(x, size.height - 6f), Offset(x, size.height), strokeWidth = 1f)
                drawLine(borderColor.copy(alpha = 0.25f), Offset(x, 8f), Offset(x, size.height - 8f), strokeWidth = 0.5f)
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

            // 1. Libres (sin hijas) → altura completa
            // 2. Madres con hijas → banda superior
            // 3. Hijas → banda inferior (cada una en su rango X)
            for (pv in plantaciones) {
                val p = pv.plantacion
                val esHija = p.intercaladaCon != null
                val esMadre = p.intercaladaCon == null && idsMadres.contains(p.id)
                val banda = when {
                    esHija -> Banda.INFERIOR
                    esMadre -> Banda.SUPERIOR
                    else -> Banda.COMPLETA
                }
                drawPlantacionEnBanda(pv, banda, pxPerCm, textMeasurer, textColor, estadoColors)
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

private fun DrawScope.drawPlantacionEnBanda(
    pv: PlantacionVisual,
    banda: Banda,
    pxPerCm: Float,
    textMeasurer: TextMeasurer,
    textColor: Color,
    estadoColors: Map<EstadoPlantacion, Color>
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
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
    )

    // Puntitos: uno por planta real (slotsX × líneas)
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

    // Emoji centrado
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

        // Nombre debajo del emoji solo en banda completa
        if (banda == Banda.COMPLETA) {
            val nameStyle = TextStyle(color = textColor, fontSize = 9.sp)
            val nameLayout = textMeasurer.measure(c.nombre, nameStyle)
            if (nameLayout.size.width < w - 8f) {
                drawText(
                    textMeasurer = textMeasurer,
                    text = c.nombre,
                    topLeft = Offset(
                        x + (w - nameLayout.size.width) / 2,
                        topY + h * 0.6f
                    ),
                    style = nameStyle
                )
            }
        }
    }
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
    val cellH = rectH / lineas
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

    drawRoundRect(
        color = color.copy(alpha = 0.75f),
        topLeft = Offset(x + 2f, padding),
        size = Size(w - 4f, h),
        cornerRadius = CornerRadius(8f, 8f),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
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
