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
    val emptyColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface
    val errorColor = MaterialTheme.colorScheme.error
    val primaryColor = MaterialTheme.colorScheme.primary

    // Resolver colores de estado en contexto composable para pasar a DrawScope
    val estadoColors = mapOf(
        EstadoPlantacion.SEMILLERO to EstadoSemillero,
        EstadoPlantacion.TRASPLANTADO to EstadoTrasplantado,
        EstadoPlantacion.CRECIENDO to EstadoCreciendo,
        EstadoPlantacion.COSECHANDO to EstadoCosechando,
        EstadoPlantacion.RETIRADO to EstadoRetirado
    )

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
                        val yFraction = offset.y / size.height

                        // Buscar plantaciones en esta posición X
                        val enRango = plantaciones.filter { pv ->
                            val p = pv.plantacion
                            xCm >= p.posicionXCm && xCm <= p.posicionXCm + p.anchoCm
                        }

                        val tocada = if (enRango.size <= 1) {
                            enRango.firstOrNull()
                        } else {
                            // Par intercalado: madre arriba, hija abajo
                            val hija = enRango.find { it.plantacion.intercaladaCon != null }
                            val madre = enRango.find { it.plantacion.intercaladaCon == null }
                            if (hija != null && madre != null) {
                                if (yFraction < 0.5f) madre else hija
                            } else {
                                enRango.firstOrNull()
                            }
                        }

                        if (tocada != null) {
                            onTapPlantacion(tocada.plantacion)
                        } else {
                            onTapZonaVacia(xCm)
                        }
                    }
                }
        ) {
            // Fondo del bancal con leve gradiente vertical (sensación de cajón / tierra húmeda)
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

            // Marcas cada metro: tick fino arriba/abajo + etiqueta con pill discreta
            for (metro in 1 until bancalLargoCm / 100) {
                val x = metro * 100 * pxPerCm
                // Tick superior
                drawLine(
                    color = borderColor,
                    start = Offset(x, 0f),
                    end = Offset(x, 6f),
                    strokeWidth = 1f
                )
                // Tick inferior
                drawLine(
                    color = borderColor,
                    start = Offset(x, size.height - 6f),
                    end = Offset(x, size.height),
                    strokeWidth = 1f
                )
                // Línea vertical muy tenue de referencia
                drawLine(
                    color = borderColor.copy(alpha = 0.25f),
                    start = Offset(x, 8f),
                    end = Offset(x, size.height - 8f),
                    strokeWidth = 0.5f
                )
                // Etiqueta del metro
                val label = "${metro}m"
                val labelStyle = TextStyle(
                    color = textColor.copy(alpha = 0.55f),
                    fontSize = 9.sp
                )
                val labelLayout = textMeasurer.measure(label, labelStyle)
                val labelPad = 4f
                // Pill de fondo
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

            // Agrupar plantaciones intercaladas
            val intercaladas = plantaciones.filter { it.plantacion.intercaladaCon != null }
            val madresConHija = intercaladas.mapNotNull { it.plantacion.intercaladaCon }.toSet()
            val normales = plantaciones.filter {
                it.plantacion.intercaladaCon == null && it.plantacion.id !in madresConHija
            }

            // Dibujar plantaciones normales (altura completa)
            for (pv in normales) {
                drawPlantacion(pv, pxPerCm, textMeasurer, textColor, estadoColors)
            }

            // Dibujar pares intercalados (split vertical)
            for (hija in intercaladas) {
                val madre = plantaciones.find { it.plantacion.id == hija.plantacion.intercaladaCon }
                if (madre != null) {
                    drawPlantacionSplit(madre, isTop = true, pxPerCm, textMeasurer, textColor, estadoColors)
                    drawPlantacionSplit(hija, isTop = false, pxPerCm, textMeasurer, textColor, estadoColors)
                }
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

private fun DrawScope.drawPlantacion(
    pv: PlantacionVisual,
    pxPerCm: Float,
    textMeasurer: TextMeasurer,
    textColor: Color,
    estadoColors: Map<EstadoPlantacion, Color>
) {
    val p = pv.plantacion
    val c = pv.cultivo

    val x = p.posicionXCm * pxPerCm
    val w = p.anchoCm * pxPerCm
    val padding = 4f
    val h = size.height - padding * 2

    val color = estadoColors[p.estado] ?: Color.Gray

    // Rectángulo de la plantación con gradiente vertical (más claro arriba)
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                color.copy(alpha = 0.32f),
                color.copy(alpha = 0.18f)
            ),
            startY = padding,
            endY = padding + h
        ),
        topLeft = Offset(x + 2f, padding),
        size = Size(w - 4f, h),
        cornerRadius = CornerRadius(8f, 8f),
        style = Fill
    )

    // Borde fino
    drawRoundRect(
        color = color.copy(alpha = 0.55f),
        topLeft = Offset(x + 2f, padding),
        size = Size(w - 4f, h),
        cornerRadius = CornerRadius(8f, 8f),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
    )

    // Emoji del cultivo centrado
    if (w > 30f) {
        val emojiStyle = TextStyle(fontSize = 20.sp)
        val emojiLayout = textMeasurer.measure(c.icono, emojiStyle)
        drawText(
            textMeasurer = textMeasurer,
            text = c.icono,
            topLeft = Offset(
                x + (w - emojiLayout.size.width) / 2,
                padding + (h - emojiLayout.size.height) / 3
            ),
            style = emojiStyle
        )

        // Nombre debajo del emoji
        val nameStyle = TextStyle(color = textColor, fontSize = 9.sp)
        val nameLayout = textMeasurer.measure(c.nombre, nameStyle)
        if (nameLayout.size.width < w - 8f) {
            drawText(
                textMeasurer = textMeasurer,
                text = c.nombre,
                topLeft = Offset(
                    x + (w - nameLayout.size.width) / 2,
                    padding + h * 0.6f
                ),
                style = nameStyle
            )
        }
    }
}

private fun DrawScope.drawPlantacionSplit(
    pv: PlantacionVisual,
    isTop: Boolean,
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

    val topY = if (isTop) outerPad else outerPad + halfH + splitGap
    val h = halfH

    val color = estadoColors[p.estado] ?: Color.Gray

    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(color.copy(alpha = 0.32f), color.copy(alpha = 0.18f)),
            startY = topY, endY = topY + h
        ),
        topLeft = Offset(x + 2f, topY),
        size = Size(w - 4f, h),
        cornerRadius = CornerRadius(6f, 6f),
        style = Fill
    )

    drawRoundRect(
        color = color.copy(alpha = 0.55f),
        topLeft = Offset(x + 2f, topY),
        size = Size(w - 4f, h),
        cornerRadius = CornerRadius(6f, 6f),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
    )

    if (w > 30f) {
        val emojiStyle = TextStyle(fontSize = 16.sp)
        val emojiLayout = textMeasurer.measure(c.icono, emojiStyle)
        drawText(
            textMeasurer = textMeasurer,
            text = c.icono,
            topLeft = Offset(
                x + (w - emojiLayout.size.width) / 2,
                topY + (h - emojiLayout.size.height) / 2
            ),
            style = emojiStyle
        )
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

    // Fondo semitransparente con gradiente
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                color.copy(alpha = 0.22f),
                color.copy(alpha = 0.10f)
            ),
            startY = padding,
            endY = padding + h
        ),
        topLeft = Offset(x + 2f, padding),
        size = Size(w - 4f, h),
        cornerRadius = CornerRadius(8f, 8f),
        style = Fill
    )

    // Borde más marcado para destacar el preview
    drawRoundRect(
        color = color.copy(alpha = 0.75f),
        topLeft = Offset(x + 2f, padding),
        size = Size(w - 4f, h),
        cornerRadius = CornerRadius(8f, 8f),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
    )

    // Emoji centrado
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
