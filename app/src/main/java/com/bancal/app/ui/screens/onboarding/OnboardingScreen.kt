package com.bancal.app.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class OnboardingPage(
    val emoji: String,
    val titulo: String,
    val descripcion: String
)

private val pages = listOf(
    OnboardingPage(
        emoji = "\uD83C\uDF31",
        titulo = "Bienvenido a Bancal",
        descripcion = "Tu huerto biointensivo en el bolsillo. Planifica, planta y cosecha con la guia del metodo de John Jeavons, adaptado al clima de Burgos."
    ),
    OnboardingPage(
        emoji = "\uD83D\uDDFA\uFE0F",
        titulo = "Tu bancal, visual",
        descripcion = "Ve tus plantaciones en un mapa interactivo. Toca una zona vacia para plantar, toca un cultivo para ver su detalle. Todo a un vistazo."
    ),
    OnboardingPage(
        emoji = "\uD83E\uDD1D",
        titulo = "Asociaciones inteligentes",
        descripcion = "La app te avisa si un cultivo es buen o mal vecino, sugiere intercalados y vigila la rotacion para que tu suelo se mantenga fertil."
    ),
    OnboardingPage(
        emoji = "\uD83D\uDD14",
        titulo = "Alertas que ayudan",
        descripcion = "Recibe avisos de cosecha, trasplante, heladas y tratamientos preventivos. Sin ruido, solo lo que importa."
    ),
    OnboardingPage(
        emoji = "\uD83C\uDF3F",
        titulo = "Hemos plantado por ti",
        descripcion = "Para que veas como funciona, hemos creado un bancal de ejemplo con 5 cultivos de temporada. Puedes eliminarlos cuando quieras y empezar con los tuyos."
    )
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    var currentPage by remember { mutableIntStateOf(0) }
    val page = pages[currentPage]
    val isLast = currentPage == pages.lastIndex

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Spacer superior flexible
            Spacer(Modifier.weight(1f))

            // Contenido animado
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "onboarding_page"
            ) { pageIdx ->
                val p = pages[pageIdx]
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Emoji grande con fondo circular
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = p.emoji,
                            fontSize = 56.sp
                        )
                    }

                    Spacer(Modifier.height(32.dp))

                    Text(
                        text = p.titulo,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = p.descripcion,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Spacer inferior flexible
            Spacer(Modifier.weight(1f))

            // Indicadores de pagina
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                pages.forEachIndexed { idx, _ ->
                    Box(
                        modifier = Modifier
                            .size(if (idx == currentPage) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (idx == currentPage)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.outlineVariant
                            )
                    )
                }
            }

            // Botones
            if (isLast) {
                Button(
                    onClick = onFinish,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Empezar", style = MaterialTheme.typography.titleMedium)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onFinish) {
                        Text("Saltar")
                    }
                    Button(
                        onClick = { currentPage++ },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Siguiente")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
