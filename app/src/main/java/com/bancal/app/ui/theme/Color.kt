package com.bancal.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Paleta tierra — Light
val GreenPrimary = Color(0xFF2D6A4F)
val GreenSecondary = Color(0xFF52B788)
val BrownTertiary = Color(0xFF8B5E3C)
val CreamBackground = Color(0xFFFDF6EC)
val CreamSurface = Color(0xFFFFFBF5)
val OnPrimaryLight = Color(0xFFFFFFFF)
val OnBackgroundLight = Color(0xFF1B1B1B)
val OnSurfaceLight = Color(0xFF2C2C2C)

// Paleta tierra — Dark
val GreenPrimaryDark = Color(0xFF95D5B2)
val GreenSecondaryDark = Color(0xFF74C69D)
val BrownTertiaryDark = Color(0xFFD4A574)
val DarkBackground = Color(0xFF1A1C19)
val DarkSurface = Color(0xFF2D2F2B)
val OnPrimaryDark = Color(0xFF003822)
val OnBackgroundDark = Color(0xFFE2E3DE)
val OnSurfaceDark = Color(0xFFE2E3DE)

// Colores semánticos — adaptan a light/dark
internal val _ErrorLight = Color(0xFFBA1A1A)
internal val _ErrorDark = Color(0xFFFFB4AB)
private val _WarningLight = Color(0xFFE8A317)
private val _WarningDark = Color(0xFFFFD580)
private val _SuccessLight = Color(0xFF2D6A4F)
private val _SuccessDark = Color(0xFF74C69D)

val ErrorLight: Color @Composable get() = if (isSystemInDarkTheme()) _ErrorDark else _ErrorLight
val WarningAmber: Color @Composable get() = if (isSystemInDarkTheme()) _WarningDark else _WarningLight
val SuccessGreen: Color @Composable get() = if (isSystemInDarkTheme()) _SuccessDark else _SuccessLight

// Colores de estado de plantación — adaptativos
private val _EstadoSemilleroL = Color(0xFFFFD166)
private val _EstadoSemilleroD = Color(0xFFFFD166)
private val _EstadoTrasplantadoL = Color(0xFF06D6A0)
private val _EstadoTrasplantadoD = Color(0xFF06D6A0)
private val _EstadoCreciendoL = Color(0xFF2D6A4F)
private val _EstadoCreciendoD = Color(0xFF74C69D)
private val _EstadoCosechandoL = Color(0xFFEF476F)
private val _EstadoCosechandoD = Color(0xFFFF8FA3)
private val _EstadoRetiradoL = Color(0xFFADB5BD)
private val _EstadoRetiradoD = Color(0xFF6C757D)

val EstadoSemillero: Color @Composable get() = if (isSystemInDarkTheme()) _EstadoSemilleroD else _EstadoSemilleroL
val EstadoTrasplantado: Color @Composable get() = if (isSystemInDarkTheme()) _EstadoTrasplantadoD else _EstadoTrasplantadoL
val EstadoCreciendo: Color @Composable get() = if (isSystemInDarkTheme()) _EstadoCreciendoD else _EstadoCreciendoL
val EstadoCosechando: Color @Composable get() = if (isSystemInDarkTheme()) _EstadoCosechandoD else _EstadoCosechandoL
val EstadoRetirado: Color @Composable get() = if (isSystemInDarkTheme()) _EstadoRetiradoD else _EstadoRetiradoL

// Colores de asociación — adaptativos
private val _AsociacionBuenaL = Color(0xFF2D6A4F)
private val _AsociacionBuenaD = Color(0xFF74C69D)
private val _AsociacionMalaL = Color(0xFFBA1A1A)
private val _AsociacionMalaD = Color(0xFFFFB4AB)
private val _AsociacionNeutraL = Color(0xFFADB5BD)
private val _AsociacionNeutraD = Color(0xFF6C757D)

val AsociacionBuena: Color @Composable get() = if (isSystemInDarkTheme()) _AsociacionBuenaD else _AsociacionBuenaL
val AsociacionMala: Color @Composable get() = if (isSystemInDarkTheme()) _AsociacionMalaD else _AsociacionMalaL
val AsociacionNeutra: Color @Composable get() = if (isSystemInDarkTheme()) _AsociacionNeutraD else _AsociacionNeutraL
